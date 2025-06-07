package domain;

import java.time.LocalDateTime;

public class EventLog {
    private LocalDateTime dateTime;
    private String user, description;

    public EventLog() {
    }

    public EventLog(LocalDateTime dateTime, String user, String description) {
        this.dateTime = dateTime;
        this.user = user;
        this.description = description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "EventLog{" +
                "dateTime=" + dateTime +
                ", user='" + user + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
