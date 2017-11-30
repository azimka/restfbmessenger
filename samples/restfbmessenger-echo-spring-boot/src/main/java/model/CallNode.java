package model;

import com.github.marsbits.restfbmessenger.Messenger;
import com.restfb.types.send.ButtonTemplatePayload;
import com.restfb.types.send.CallButton;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.MessagingType;

public class CallNode extends TransitionItem {

    public CallNode() {
    }

    @Override
    public void sendMessage(Messenger messenger, IdMessageRecipient recipient) {
        KeyboardButtonItem[] keyboardButtonItems = getKeyboardButtonItems();
        ButtonTemplatePayload buttonTemplatePayload = new ButtonTemplatePayload(getMessage());
        for (KeyboardButtonItem item : keyboardButtonItems) {
            String name = item.getName();
            String title = item.getTitle();
            buttonTemplatePayload.addButton(new CallButton(title, name));
        }

        messenger.send().buttonTemplate(MessagingType.RESPONSE, recipient, buttonTemplatePayload);
    }
}
