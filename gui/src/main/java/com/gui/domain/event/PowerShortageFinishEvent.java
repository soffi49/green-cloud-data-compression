package com.gui.domain.event;

import com.gui.domain.types.EventTypeEnum;

import java.time.OffsetDateTime;

public class PowerShortageFinishEvent extends AbstractEvent{

    public PowerShortageFinishEvent(EventTypeEnum eventTypeEnum, OffsetDateTime occurrenceTime) {
        super(eventTypeEnum, occurrenceTime);
    }
}
