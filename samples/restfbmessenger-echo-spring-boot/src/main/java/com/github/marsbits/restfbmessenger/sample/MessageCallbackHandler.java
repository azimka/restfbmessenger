package com.github.marsbits.restfbmessenger.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.marsbits.restfbmessenger.Messenger;
import com.github.marsbits.restfbmessenger.webhook.AbstractCallbackHandler;
import com.restfb.types.User;
import com.restfb.types.send.*;
import com.restfb.types.webhook.messaging.MessageItem;
import com.restfb.types.webhook.messaging.MessagingAttachment;
import com.restfb.types.webhook.messaging.MessagingItem;
import model.KeyboardButtonItem;
import model.TransitionItem;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Component
public class MessageCallbackHandler extends AbstractCallbackHandler {

    private static final Logger logger = Logger.getLogger(MessageCallbackHandler.class.getName());
    private static final String HELLO_MESSAGE = "Выбирете ...";
    private static final String TRANSITION_FILE_NAME = "transitions.json";

    private Map<String, TransitionItem> transitionMap = new HashMap<>();
    private Map<String, TransitionItem> currentState = new HashMap<>();

    {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(TRANSITION_FILE_NAME);
            TransitionItem[] keyboardButtons = objectMapper.readValue(inputStream, TransitionItem[].class);
            for (TransitionItem item : keyboardButtons) {
                transitionMap.put(item.getName(), item);
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
        User user = messenger.getUserProfile(senderId);

        messenger.send().markSeen(recipient);
        messenger.send().typingOn(recipient);

        sleep(TimeUnit.SECONDS, 1);

//        9k49URc6nBYH

        if (message.getText() != null && message.getQuickReply() != null && message.getQuickReply().getPayload() != null) {

            String key = message.getQuickReply().getPayload();

            TransitionItem transitionItem = transitionMap.get(key);
            if (transitionItem == null) {
                return;
            }

            currentState.put(senderId, transitionItem);
            sendMessage(messenger, recipient, transitionItem);

        } else if (message.getText() != null) {
//            logger.warning("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ " + message.getText() + currentState.get(senderId));
//            String messageTest = message.getText();
            TransitionItem currentStage = currentState.get(senderId);
            TransitionItem nextStage = null;
            if (currentStage != null) {
                nextStage = transitionMap.get(currentStage.getNextStageName());
                if(nextStage == null) {
//                    messenger.send().textMessage(recipient, "Сообщение не распознано, попробуйте еще раз");
                    sendQuickReplyMessage(messenger, recipient, currentStage);
                }
                else {
                    sendQuickReplyMessage(messenger, recipient, nextStage);
                    currentState.put(senderId, nextStage);
                }

            } else {
                sendQuickReplyMessage(messenger, recipient, transitionMap.get("start"));
                currentState.put(senderId, transitionMap.get("start"));
            }

        } else {

            if (message.getAttachments() != null) {
                for (MessagingAttachment attachment : message.getAttachments()) {
                    String type = attachment.getType();
                    if ("location".equals(type)) {
                        // Echo the received location as text message(s)
//                        messenger.send().textMessage(recipient, transitionMap.get("autoDetectMyPlace").getMessage());
                        sendQuickReplyMessage(messenger, recipient, transitionMap.get("autoDetectMyPlace"));
                    } else {
                        // Echo the attachment
                        String url = attachment.getPayload().getUrl();
//                        messenger.send().attachment(recipient,
//                                MediaAttachment.Type.valueOf(type.toUpperCase()), url);
                    }
                }
            }
        }

        messenger.send().typingOff(recipient);
    }

    private void sendMessage(Messenger messenger, IdMessageRecipient recipient, TransitionItem transitionItem) {
        switch (transitionItem.getType()) {
            case "quickReply": {
                sendQuickReplyMessage(messenger, recipient, transitionItem);
                break;
            }
            case "callReply": {
                sendCallReplyMessage(messenger, recipient, transitionItem);
                break;
            }

            case "location": {
                sendLocation(messenger, recipient, transitionItem);
                break;
            }
            default: {
                sendQuickReplyMessage(messenger, recipient, transitionItem);
                break;
            }
        }
    }

    private void sendQuickReplyMessage(Messenger messenger, IdMessageRecipient recipient, TransitionItem transitionItem) {
        KeyboardButtonItem[] keyboardButtonItems = transitionItem.getKeyboardButtonItems();
        List<QuickReply> quickReplyList = new ArrayList<>();
        for (KeyboardButtonItem item : keyboardButtonItems) {
            String name = item.getName();
            String title = item.getTitle();
            quickReplyList.add(new QuickReply(name, title));
        }

        List<CallToAction> callToActions = new ArrayList<>();
        callToActions.add(new CallToAction("test1"));
        callToActions.add(new CallToAction("test2"));
        callToActions.add(new CallToAction("test3"));
//        messenger.setPersistentMenu(callToActions);
//        messenger.send().quickReplies(recipient, transitionItem.getMessage(), quickReplyList);
    }

    private void sendLocation(Messenger messenger, IdMessageRecipient recipient, TransitionItem transitionItem) {
        QuickReply quickReply = new QuickReply();
        List<QuickReply> quickReplies = new ArrayList<>();
        quickReplies.add(quickReply);
//        messenger.send().quickReplies(recipient, "Отправка координат", quickReplies);
    }

    private void sendCallReplyMessage(Messenger messenger, IdMessageRecipient recipient, TransitionItem transitionItem) {
        KeyboardButtonItem[] keyboardButtonItems = transitionItem.getKeyboardButtonItems();
        ButtonTemplatePayload buttonTemplatePayload = new ButtonTemplatePayload(transitionItem.getMessage());
        for (KeyboardButtonItem item : keyboardButtonItems) {
            String name = item.getName();
            String title = item.getTitle();
            buttonTemplatePayload.addButton(new CallButton(name, title));
        }

//        messenger.send().buttonTemplate(recipient, buttonTemplatePayload);
    }

    private void getMainKeyboard(Messenger messenger, IdMessageRecipient recipient) {
        List<QuickReply> quickReplyList = new ArrayList<>();
        QuickReply departments = new QuickReply("Отделения", "departments");
        QuickReply calculation = new QuickReply("Расчет", "calculation");
        QuickReply faq = new QuickReply("FAQ", "faq");
        QuickReply proposal = new QuickReply("Заявка", "proposal");
        QuickReply phone = new QuickReply("Позвонить", "phone");
        quickReplyList.add(departments);
        quickReplyList.add(calculation);
        quickReplyList.add(faq);
        quickReplyList.add(proposal);
        quickReplyList.add(phone);
//        messenger.send().quickReplies(recipient, HELLO_MESSAGE, quickReplyList);
    }

    private void getDepartmentKeyboard(Messenger messenger, IdMessageRecipient recipient) {
        List<QuickReply> quickReplyList = new ArrayList<>();
        QuickReply departments = new QuickReply("Определить мое местоположение", "autoDetectMyPlace");
        QuickReply calculation = new QuickReply("Отмена", "cancel");
        quickReplyList.add(departments);
        quickReplyList.add(calculation);
//        messenger.send().quickReplies(recipient, HELLO_MESSAGE, quickReplyList);
    }

    @Override
    public void onPayment(Messenger messenger, MessagingItem messaging) {
        super.onPayment(messenger, messaging);

        logger.warning("payment!!!!!!!!!!!!!!!!!!!!111");
    }

    @Override
    public void onPostback(Messenger messenger, MessagingItem messaging) {
        super.onPostback(messenger, messaging);

        List<CallToAction> callToActions = new ArrayList<>();
//        callToActions.add(new CallToAction("test1"));
//        callToActions.add(new CallToAction("test2"));
//        callToActions.add(new CallToAction("test3"));
//        messenger.setPersistentMenu(callToActions);

        logger.warning("postback!!!!!!!!!!!!!!!!!!!!2222");
    }

    private void sleep(TimeUnit timeUnit, long duration) {
        try {
            timeUnit.sleep(duration);
        } catch (InterruptedException ignore) {
        }
    }
}
