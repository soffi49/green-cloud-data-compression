package com.gui.domain.event;

import com.gui.domain.types.EventTypeEnum;

import java.time.OffsetDateTime;

public abstract class AbstractEvent {

    protected EventTypeEnum eventTypeEnum;
    protected OffsetDateTime occurrenceTime;

    public AbstractEvent(EventTypeEnum eventTypeEnum, OffsetDateTime occurrenceTime) {
        this.eventTypeEnum = eventTypeEnum;
        this.occurrenceTime = occurrenceTime;
    }

    public EventTypeEnum getEventTypeEnum() {
        return eventTypeEnum;
    }

    public void setEventTypeEnum(EventTypeEnum eventTypeEnum) {
        this.eventTypeEnum = eventTypeEnum;
    }

    public OffsetDateTime getOccurrenceTime() {
        return occurrenceTime;
    }

    public void setOccurrenceTime(OffsetDateTime occurrenceTime) {
        this.occurrenceTime = occurrenceTime;
    }
}
