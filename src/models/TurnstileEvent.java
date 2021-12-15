package models;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;

/**
 * Класс содержит список событий происходивших на турникете, в последствии из него получаются данные для
 * окна событий.
 */
public class TurnstileEvent {
    private final StringProperty username;
    private final LocalDateTime dateTime;
    private final EventType eventType;

    public TurnstileEvent(String username, EventType eventType) {
        this.username = new SimpleStringProperty(username);
        this.dateTime = LocalDateTime.now();
        this.eventType = eventType;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public EventType getEventType() {
        return eventType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

}
