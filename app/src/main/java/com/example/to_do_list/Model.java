package com.example.to_do_list;

public class Model {
    private int id;
    private String title;
    private String description;
    private int priority;
    private String list;
    private String tag;
    private int ifRemind;
    private int ifAllDay;
    private String date;
    private String time;
    private int remindBefore;

    public Model(int id, String title, String description, int priority, String list, String tag, int ifRemind, int ifAllDay, String date, String time, int remindBefore) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.list = list;
        this.tag = tag;
        this.ifRemind = ifRemind;
        this.ifAllDay = ifAllDay;
        this.date = date;
        this.time = time;
        this.remindBefore = remindBefore;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", list='" + list + '\'' +
                ", tag='" + tag + '\'' +
                ", ifRemind=" + ifRemind +
                ", ifAllDay=" + ifAllDay +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", remindBefore=" + remindBefore +
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getIfRemind() {
        return ifRemind;
    }

    public void setIfRemind(int ifRemind) {
        this.ifRemind = ifRemind;
    }

    public int getIfAllDay() {
        return ifAllDay;
    }

    public void setIfAllDay(int ifAllDay) {
        this.ifAllDay = ifAllDay;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRemindBefore() {
        return remindBefore;
    }

    public void setRemindBefore(int remindBefore) {
        this.remindBefore = remindBefore;
    }
}
