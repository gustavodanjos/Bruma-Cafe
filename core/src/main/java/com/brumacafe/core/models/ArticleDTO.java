package com.brumacafe.core.models;

import java.util.Date;

public class ArticleDTO {
    private final String title;
    private final String description;
    private final String path;
    private final Date date;
    private final String imagePath;

    public ArticleDTO(String title, String description, String path, Date date, String imagePath) {
        this.title = title;
        this.description = description;
        this.path = path;
        this.date = date;
        this.imagePath = imagePath;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPath() { return path; }
    public Date getDate() { return date; }
    public String getImagePath() { return imagePath; }
}