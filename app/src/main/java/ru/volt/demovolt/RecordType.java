package ru.volt.demovolt;

import java.io.Serializable;

/**
 * Created by dave on 07.05.16.
 */
public class RecordType implements Serializable {
    private Integer userId;
    private Integer recordId;
    private String title;
    private String body;
    private boolean favorites; //Флаг избранного

    public RecordType() {
        this.userId = -1;
        this.recordId = -1;
        this.title = new String();
        this.body = new String();
        this.favorites = false;
    }

    public RecordType(int userId, int recordId, String title, String body, boolean favorites) {
        this.userId = userId;
        this.recordId = recordId;
        this.title = title;
        this.body = body;
        this.favorites = favorites;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRecordId() {
        return this.recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean getFavorites() {
        return this.favorites;
    }

    public void setFavorites(boolean favorites) {
        this.favorites = favorites;
    }
}
