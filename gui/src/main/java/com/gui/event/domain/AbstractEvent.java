package com.gui.event.domain;

import java.time.OffsetDateTime;

/**
 * Class represents the abstract event which may occur in the environment
 */
public abstract class AbstractEvent {

	protected EventTypeEnum eventTypeEnum;
	protected OffsetDateTime occurrenceTime;

	/**
	 * Default event constructor
	 *
	 * @param eventTypeEnum  type of the event
	 * @param occurrenceTime time when the event occurs
	 */
	protected AbstractEvent(final EventTypeEnum eventTypeEnum, final OffsetDateTime occurrenceTime) {
		this.eventTypeEnum = eventTypeEnum;
		this.occurrenceTime = occurrenceTime;
	}

	/**
	 * @return type of the event
	 */
	public EventTypeEnum getEventTypeEnum() {
		return eventTypeEnum;
	}

	/**
	 * @return time when the event will occur
	 */
	public OffsetDateTime getOccurrenceTime() {
		return occurrenceTime;
	}
}
