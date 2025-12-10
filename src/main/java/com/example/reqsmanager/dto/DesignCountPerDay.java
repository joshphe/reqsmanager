package com.example.reqsmanager.dto;

import java.time.LocalDate;

public class DesignCountPerDay {
    private LocalDate date;
    private long count;

    public DesignCountPerDay(LocalDate date, long count) {
        this.date = date;
        this.count = count;
    }

    // Getters
    public LocalDate getDate() {
        return date;
    }

    public long getCount() {
        return count;
    }
}
