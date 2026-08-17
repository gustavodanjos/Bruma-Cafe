package com.brumacafe.core.models;

import java.util.Calendar;

public class ContactMessage {
    private String id;
    private String name;
    private String email;
    private String subject;
    private String message;
    private String status;
    private Calendar date;
    private Calendar trashedDate;

    public ContactMessage(String id, String name, String email, String subject, String message, String status, Calendar date, Calendar trashedDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.date = date;
        this.trashedDate = trashedDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public Calendar getDate() {
        return date;
    }

    public Calendar getTrashedDate() {
        return trashedDate;
    }
}
