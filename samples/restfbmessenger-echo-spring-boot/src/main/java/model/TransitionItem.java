package model;

import java.util.Arrays;

public class TransitionItem {
    private String name;
    private String message;
    private String keyboardTemplate;
    private String nextStageName;
    private String actionName;
    private String type;
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

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TransitionItem{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", keyboardTemplate='" + keyboardTemplate + '\'' +
                ", nextStageName='" + nextStageName + '\'' +
                ", actionName='" + actionName + '\'' +
                ", keyboardButtonItems=" + Arrays.toString(keyboardButtonItems) +
                '}';
    }
}
