package com.gui.event.domain;

import static com.gui.event.domain.EventTypeEnum.POWER_SHORTAGE;

import java.time.Instant;

/**
 * Event making the given agent exposed to the power shortage
 */
public class PowerShortageEvent extends AbstractEvent {

	private final int newMaximumPower;
	private final boolean indicateFinish;

	/**
	 * Default event constructor
	 *
	 * @param occurrenceTime  time when the power shortage will happen
	 * @param newMaximumPower maximum power during the power shortage
	 * @param indicateFinish  flag indicating whether the event informs of the power shortage finish or start
	 */
	public PowerShortageEvent(final Instant occurrenceTime, final int newMaximumPower,
			final boolean indicateFinish) {
		super(POWER_SHORTAGE, occurrenceTime);
		this.newMaximumPower = newMaximumPower;
		this.indicateFinish = indicateFinish;
	}

	/**
	 * @return new maximum power during power shortage
	 */
	public int getNewMaximumPower() {
		return newMaximumPower;
	}

	/**
	 * @return flag if the power shortage should be finished or started
	 */
	public boolean isIndicateFinish() {
		return indicateFinish;
	}
}
