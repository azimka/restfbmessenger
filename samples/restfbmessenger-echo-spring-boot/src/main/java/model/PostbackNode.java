package model;

import com.github.marsbits.restfbmessenger.Messenger;
import com.restfb.types.send.ButtonTemplatePayload;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.MessagingType;
import com.restfb.types.send.PostbackButton;

public class PostbackNode extends TransitionItem {

    public PostbackNode() {
    }

    @Override
    public void sendMessage(Messenger messenger, IdMessageRecipient recipient) {
        KeyboardButtonItem[] keyboardButtonItems = getKeyboardButtonItems();
        ButtonTemplatePayload buttonTemplatePayload = new ButtonTemplatePayload(getMessage());
        for (KeyboardButtonItem item : keyboardButtonItems) {
            String name = item.getName();
            String title = item.getTitle();
            buttonTemplatePayload.addButton(new PostbackButton(title, name));
        }

        messenger.send().buttonTemplate(MessagingType.RESPONSE, recipient, buttonTemplatePayload);
    }
}
