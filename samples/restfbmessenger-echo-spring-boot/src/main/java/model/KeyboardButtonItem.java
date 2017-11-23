package model;

/**
 * Created by azimka on 08.11.17.
 */
public class KeyboardButtonItem {
    private String name;
    private String title;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "KeyboardButtonItem{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
