package ru.volt.demovolt.events;

/**
 * Created by dave on 10.05.16.
 */
public class MessageEvent {
    private int message;
    private int position;

    public MessageEvent() {
    }

    public MessageEvent(int message, int position) {
        this.message = message;
        this.position = position;
    }

    public int getMessage() {
        return this.message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}