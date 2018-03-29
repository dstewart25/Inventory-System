import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Alert {
    private int id;
    private String subject;
    private String body;
    private Timestamp time;

    public Alert() {
        subject = null;
        body = null;
        time = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Timestamp getTime() {
        return time;
    }

    public String getFormattedTime() {
        String year = new SimpleDateFormat("EEEEEEEEEE, MMMMMMMMMM dd, yyyy").format(time);
        String time = new SimpleDateFormat("HH:MM").format(this.time);
        String convertedTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")).format(DateTimeFormatter.ofPattern("hh:mm a"));
        if (convertedTime.startsWith("0"))
            convertedTime = convertedTime.substring(1);
        return year + " at " + convertedTime;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
