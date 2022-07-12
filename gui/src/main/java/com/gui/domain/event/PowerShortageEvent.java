package com.gui.domain.event;

import com.gui.domain.types.EventTypeEnum;

import java.time.OffsetDateTime;

public class PowerShortageEvent extends AbstractEvent{

    private int newMaximumPower;

    public PowerShortageEvent(EventTypeEnum eventTypeEnum, OffsetDateTime occurrenceTime, int newMaximumPower) {
        super(eventTypeEnum, occurrenceTime);
        this.newMaximumPower = newMaximumPower;
    }

    public int getNewMaximumPower() {
        return newMaximumPower;
    }

    public void setNewMaximumPower(int newMaximumPower) {
        this.newMaximumPower = newMaximumPower;
    }
}
