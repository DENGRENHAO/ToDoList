package com.example.to_do_list;

public class CalendarModel {
    private int id;
    private String title;
    private String description;
    private String time;
    private int date;

    public CalendarModel(int id, String title, String description, String time, int date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.time = time;
        this.date = date;
    }


    @Override
    public String toString() {
        return "CalendarModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", time='" + time + '\'' +
                ", date=" + date +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
