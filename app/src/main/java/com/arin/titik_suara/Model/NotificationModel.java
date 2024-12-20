package com.arin.titik_suara.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationModel {
    private String category;
    private String message;
    private boolean isRead;
    private long timestamp;

    // Constructor
    public NotificationModel(String category, String message, long timestamp) {
        this.category = category;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = false; // Default to unread
    }

    // Getter dan setter
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
}
