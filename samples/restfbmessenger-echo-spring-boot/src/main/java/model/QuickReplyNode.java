package model;

import com.github.marsbits.restfbmessenger.Messenger;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.MessagingType;
import com.restfb.types.send.QuickReply;

import java.util.ArrayList;
import java.util.List;

public class QuickReplyNode extends TransitionItem {

    public QuickReplyNode() {
    }

    @Override
    public void sendMessage(Messenger messenger, IdMessageRecipient recipient) {
        KeyboardButtonItem[] keyboardButtonItems = getKeyboardButtonItems();
        List<QuickReply> quickReplyList = new ArrayList<>();
        for (KeyboardButtonItem item : keyboardButtonItems) {
            String name = item.getName();
            String title = item.getTitle();
            quickReplyList.add(new QuickReply(title, name));
        }

        messenger.send().quickReplies(MessagingType.RESPONSE, recipient, getMessage(), quickReplyList);
    }
}
