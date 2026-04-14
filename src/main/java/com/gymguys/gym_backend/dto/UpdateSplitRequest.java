package com.gymguys.gym_backend.dto;

public class UpdateSplitRequest {

    private int splitDays;

    public UpdateSplitRequest() {}

    public int getSplitDays() {
        return splitDays;
    }

    public void setSplitDays(int splitDays) {
        this.splitDays = splitDays;
    }
}
