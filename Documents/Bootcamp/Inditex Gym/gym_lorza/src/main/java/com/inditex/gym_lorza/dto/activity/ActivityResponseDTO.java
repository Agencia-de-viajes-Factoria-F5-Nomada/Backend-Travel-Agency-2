package com.inditex.gym_lorza.dto.activity;

import java.time.LocalTime;

public class ActivityResponseDTO {
    private Long id;
    private String title;
    private String price;
    private int week_day;
    private String image;
    private LocalTime start_hour;
    private LocalTime end_hour;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public int getWeek_day() { return week_day; }
    public void setWeek_day(int week_day) { this.week_day = week_day; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public LocalTime getStart_hour() { return start_hour; }
    public void setStart_hour(LocalTime start_hour) { this.start_hour = start_hour; }
    public LocalTime getEnd_hour() { return end_hour; }
    public void setEnd_hour(LocalTime end_hour) { this.end_hour = end_hour; }
}