package com.productEvee.productEvee.controller;

import java.time.LocalDate;
import java.util.UUID;

public class ProductDto {
    private UUID userId;
    private String name;
    private double value;
    private int estimateWasteDays;
    private LocalDate lastPurchaseDate;
    private String calendarEventId;

    // Getters e Setters
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getEstimateWasteDays() {
        return estimateWasteDays;
    }

    public void setEstimateWasteDays(int estimateWasteDays) {
        this.estimateWasteDays = estimateWasteDays;
    }

    public LocalDate getLastPurchaseDate() {
        return lastPurchaseDate;
    }

    public void setLastPurchaseDate(LocalDate lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }

    public String getCalendarEventId() {
        return calendarEventId;
    }

    public void setCalendarEventId(String calendarEventId) {
        this.calendarEventId = calendarEventId;
    }
    
}
