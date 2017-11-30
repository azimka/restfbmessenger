package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PostbackNode.class, name = "postbackButtons"),
        @JsonSubTypes.Type(value = TextNode.class, name = "text"),
        @JsonSubTypes.Type(value = QuickReplyNode.class, name = "quickReply"),
        @JsonSubTypes.Type(value = CallNode.class, name = "callReply"),
        @JsonSubTypes.Type(value = LocationNode.class, name = "location")
})
public abstract class TransitionItem implements SendMessage {
    private String name;
    private String message;
    private String keyboardTemplate;
    private String nextStageName;
    private KeyboardButtonItem[] keyboardButtonItems = new KeyboardButtonItem[0];

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKeyboardTemplate() {
        return keyboardTemplate;
    }

    public void setKeyboardTemplate(String keyboardTemplate) {
        this.keyboardTemplate = keyboardTemplate;
    }

    public KeyboardButtonItem[] getKeyboardButtonItems() {
        return keyboardButtonItems;
    }

    public void setKeyboardButtonItems(KeyboardButtonItem[] keyboardButtonItems) {
        this.keyboardButtonItems = keyboardButtonItems;
    }

    public String getNextStageName() {
        return nextStageName;
    }

    public void setNextStageName(String nextStageName) {
        this.nextStageName = nextStageName;
    }
}
