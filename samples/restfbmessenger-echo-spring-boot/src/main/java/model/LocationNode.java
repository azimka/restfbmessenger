package model;

import com.github.marsbits.restfbmessenger.Messenger;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.MessagingType;
import com.restfb.types.send.QuickReply;

import java.util.ArrayList;
import java.util.List;

public class LocationNode extends TransitionItem {

    public LocationNode() {
    }

    @Override
    public void sendMessage(Messenger messenger, IdMessageRecipient recipient) {
        QuickReply quickReply = new QuickReply();
        List<QuickReply> quickReplies = new ArrayList<>();
        quickReplies.add(quickReply);
        messenger.send().quickReplies(MessagingType.RESPONSE, recipient, getMessage(), quickReplies);
    }
}
