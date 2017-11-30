package model;

import com.github.marsbits.restfbmessenger.Messenger;
import com.restfb.types.send.IdMessageRecipient;

public interface SendMessage {
    void sendMessage(Messenger messenger, IdMessageRecipient recipient);
}
