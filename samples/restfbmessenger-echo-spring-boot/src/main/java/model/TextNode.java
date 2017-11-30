package model;

import com.github.marsbits.restfbmessenger.Messenger;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.MessagingType;

public class TextNode extends TransitionItem {

    public TextNode() {
    }

    @Override
    public void sendMessage(Messenger messenger, IdMessageRecipient recipient) {
        messenger.send().textMessage(MessagingType.RESPONSE, recipient, getMessage());
    }
}
