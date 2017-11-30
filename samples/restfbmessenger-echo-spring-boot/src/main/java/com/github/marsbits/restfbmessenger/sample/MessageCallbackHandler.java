package com.github.marsbits.restfbmessenger.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.marsbits.restfbmessenger.Messenger;
import com.github.marsbits.restfbmessenger.webhook.AbstractCallbackHandler;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.webhook.messaging.MessageItem;
import com.restfb.types.webhook.messaging.MessagingAttachment;
import com.restfb.types.webhook.messaging.MessagingItem;
import model.TransitionItem;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Component
public class MessageCallbackHandler extends AbstractCallbackHandler {

    private static final Logger logger = Logger.getLogger(MessageCallbackHandler.class.getName());
    private static final String TRANSITION_FILE_NAME = "transitions2.json";
    private static final String DEFAULT_STAGE_NAME = "DEFAULT_STAGE";
    private static final String LOCATION_ATTACHMENT_NAME = "location";
    private static final String NOT_EXIST_STAGE_MESSAGE = "No stage with such identifier";

    private Map<String, TransitionItem> stagesMap = new HashMap<>();
    private Map<String, TransitionItem> currentStage = new HashMap<>();

    {
        initStagesMap();
    }

    private void initStagesMap() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(TRANSITION_FILE_NAME);
            TransitionItem[] keyboardButtons = objectMapper.readValue(inputStream, TransitionItem[].class);
            for (TransitionItem item : keyboardButtons) {
                stagesMap.put(item.getName(), item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Messenger messenger, MessagingItem messaging) {

        String senderId = messaging.getSender().getId();
        MessageItem message = messaging.getMessage();
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().markSeen(recipient);
        messenger.send().typingOn(recipient);
        sleep(TimeUnit.SECONDS, 1);

        if (isQuickReplyButton(message)) {
            handleQuickReplyButtonPush(messenger, senderId, message, recipient);
        } else if (isTextInput(message)) {
            handleTextInput(messenger, senderId, recipient);
        } else if (message.getAttachments() != null) {
            handleLocationInput(messenger, senderId, message, recipient);
        }

        messenger.send().typingOff(recipient);
    }

    private boolean isQuickReplyButton(MessageItem message) {
        return message.getQuickReply() != null;
    }

    private void handleQuickReplyButtonPush(Messenger messenger, String senderId, MessageItem message, IdMessageRecipient recipient) {
        String stageKey = message.getQuickReply().getPayload();
        TransitionItem transitionItem = stagesMap.get(stageKey);
        if (transitionItem == null) {
            throw new RuntimeException(NOT_EXIST_STAGE_MESSAGE);
        }

        currentStage.put(senderId, transitionItem);
        transitionItem.sendMessage(messenger, recipient);
    }

    private boolean isTextInput(MessageItem message) {
        return message.getText() != null;
    }

    private void handleTextInput(Messenger messenger, String senderId, IdMessageRecipient recipient) {
        TransitionItem currentStage = this.currentStage.get(senderId) != null ?
                this.currentStage.get(senderId) : stagesMap.get(DEFAULT_STAGE_NAME);

        TransitionItem nextStage = stagesMap.get(currentStage.getNextStageName());
        if (nextStage != null) {
            nextStage.sendMessage(messenger, recipient);
            this.currentStage.put(senderId, nextStage);
        }
    }

    private void handleLocationInput(Messenger messenger, String senderId, MessageItem message, IdMessageRecipient recipient) {
        List<MessagingAttachment> messagingAttachments = message.getAttachments();
        messagingAttachments.stream().filter(item -> LOCATION_ATTACHMENT_NAME.equals(item.getType())).forEach(item -> {
            TransitionItem currentStage = this.currentStage.get(senderId);
            TransitionItem newTransitionItem = stagesMap.get(currentStage.getNextStageName());
            newTransitionItem.sendMessage(messenger, recipient);
        });
    }

    @Override
    public void onPostback(Messenger messenger, MessagingItem messaging) {
        super.onPostback(messenger, messaging);

        String senderId = messaging.getSender().getId();
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().markSeen(recipient);
        messenger.send().typingOn(recipient);
        sleep(TimeUnit.SECONDS, 1);

        String stageKey = messaging.getPostback().getPayload();

        TransitionItem transitionItem = stagesMap.get(stageKey);
        if (transitionItem == null) {
            throw new RuntimeException(NOT_EXIST_STAGE_MESSAGE);
        }

        currentStage.put(senderId, transitionItem);
        transitionItem.sendMessage(messenger, recipient);

        messenger.send().typingOff(recipient);
    }

    private void sleep(TimeUnit timeUnit, long duration) {
        try {
            timeUnit.sleep(duration);
        } catch (InterruptedException ignore) {
        }
    }
}
