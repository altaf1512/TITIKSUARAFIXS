package com.arin.titik_suara.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationModel {
    private int id;
    private int userId;

    private String message;
    private long timestamp;
    private boolean isRead;
    private String category;

    public NotificationModel(int id, int userId,  String message, long timestamp) {
        this.id = id;
        this.userId = userId;

        this.message = message;
        this.timestamp = timestamp;
        this.isRead = false;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }



    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Helper method to format timestamp
    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}