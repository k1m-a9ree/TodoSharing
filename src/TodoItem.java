import java.io.Serializable;

public class TodoItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;

    public TodoItem(String title) { this.title = title; }

    public String getTitle() { return title; }
    @Override
    public String toString() { return title; }
}