import java.sql.Timestamp;

public class Alert {
    private String subject;
    private String body;
    private Timestamp time;

    public Alert() {
        subject = null;
        body = null;
        time = null;
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

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
