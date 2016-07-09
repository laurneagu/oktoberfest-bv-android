package larc.ludicon.ChatUtils;

/**
 * @author greg
 * @since 6/21/13
 */
public class Chat {

    private String message;
    private String author;
    public String date;
    public boolean seen;

    // Required default constructor for DatabaseReference object mapping
    @SuppressWarnings("unused")
    private Chat() {
    }

    public Chat(String message, String author,String date) {
        this.message = message;
        this.author = author;
        this.date = date;
        this.seen = false;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }
}
