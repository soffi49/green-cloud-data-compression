package com.greencloud.application.utils;

import static com.greencloud.commons.time.TimeConstants.SECONDS_PER_HOUR;
import static java.time.Duration.between;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.shredzone.commons.suncalc.SunTimes;
import org.slf4j.Logger;

import com.greencloud.application.exception.IncorrectTaskDateException;
import com.greencloud.commons.domain.job.PowerJob;
import com.greencloud.commons.domain.location.Location;

/**
 * Class defines set of utilities used to perform operations on date and time structures.
 */
public class TimeUtils {

	private static final Logger logger = getLogger(TimeUtils.class);
	private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

	private static final int MILLISECOND_MULTIPLIER = 1000;
	private static final int SECONDS_IN_HOUR = 3600;
	private static final int MINUTES_IN_HOUR = 60;
	private static final Long TIME_ERROR = 5L;

	public static Instant SYSTEM_START_TIME;
	private static Clock CLOCK = Clock.systemDefaultZone();

	/**
	 * Mapper used to convert the date written as string to the instant date
	 *
	 * @param date string date to be converted to offset date type
	 * @return Instant date
	 */
	public static Instant convertToInstantTime(final String date) {
		try {
			final LocalDateTime datetime = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
			final ZonedDateTime zoned = datetime.atZone(ZoneId.of("UTC"));
			return zoned.toInstant();
		} catch (DateTimeParseException e) {
			logger.info("The provided date format is incorrect");
			e.printStackTrace();
			throw new IncorrectTaskDateException();
		}
	}

	/**
	 * @return current time with possible error delay
	 */
	public static Instant getCurrentTimeMinusError() {
		return getCurrentTime().minus(TIME_ERROR, MINUTES);
	}

	/**
	 * @return current time
	 */
	public static Instant getCurrentTime() {
		return now(CLOCK).toInstant();
	}

	/**
	 * Method returns sun set and sun rise times
	 *
	 * @param dateTime zone used in retrieving sun time stamps
	 * @param location location for which data is retrieved
	 * @return SunTimes
	 */
	public static SunTimes getSunTimes(final ZonedDateTime dateTime, final Location location) {
		return SunTimes.compute().on(dateTime).at(location.getLatitude(), location.getLongitude()).execute();
	}

	/**
	 * Method converts number of seconds to milliseconds in simulation time
	 *
	 * @param seconds number of seconds
	 * @return time in milliseconds
	 */
	public static long convertToSimulationTime(final long seconds) {
		return (long) (((double) seconds / SECONDS_IN_HOUR) * SECONDS_PER_HOUR * MILLISECOND_MULTIPLIER);
	}

	/**
	 * Method converts the current simulation time into the real time
	 *
	 * @param time time which is representing simulation time
	 * @return Instant being a time representing a real time
	 */
	public static Instant convertToRealTime(final Instant time) {
		final long simulationTimeDifference = between(SYSTEM_START_TIME, time).toMillis();
		final double realTimeMultiplier = (double) SECONDS_IN_HOUR / SECONDS_PER_HOUR;
		final double realTimeDifference = simulationTimeDifference * realTimeMultiplier;
		return SYSTEM_START_TIME.plusMillis((long) realTimeDifference);
	}

	/**
	 * Method converts the current simulation time into the real time
	 *
	 * @param millis time in milliseconds
	 * @return time in minutes of real time
	 */
	public static long convertToRealTime(final long millis) {
		final double realTimeMultiplier = (double) MINUTES_IN_HOUR / (SECONDS_PER_HOUR * MILLISECOND_MULTIPLIER);
		final double realTimeDifference = millis * realTimeMultiplier;
		return (long) realTimeDifference;
	}

	/**
	 * Method checks if the given time is within given timestamp
	 *
	 * @param timeStampStart start of the time stamp
	 * @param timeStampEnd   end of the time stamp
	 * @param timeToCheck    time which has to be checked
	 * @return true or false value
	 */
	public static boolean isWithinTimeStamp(final Instant timeStampStart,
			final Instant timeStampEnd,
			final Instant timeToCheck) {
		return !timeToCheck.isBefore(timeStampStart) && timeToCheck.isBefore(timeStampEnd);
	}

	/**
	 * Method checks if the given time (in real time) is within job timestamp
	 *
	 * @param job         job for which time will be checked
	 * @param timeToCheck time which has to be checked
	 * @return true or false value
	 */
	public static boolean isWithinTimeStamp(final PowerJob job, final Instant timeToCheck) {
		return !timeToCheck.isBefore(convertToRealTime(job.getStartTime()))
				&& timeToCheck.isBefore(convertToRealTime(job.getEndTime()));
	}

	/**
	 * Method aligns a given start time by comparing it to the current time
	 * (i.e. if the start time has already passed then it substitutes it with current time)
	 *
	 * @param startTime initial start time
	 * @return Instant being an aligned start time
	 */
	public static Instant alignStartTimeToCurrentTime(final Instant startTime) {
		return alignStartTimeToGivenTime(startTime, getCurrentTime());
	}

	/**
	 * Method aligns a given start time by comparing it to the relevant time instant
	 * (i.e. if the given time instant has passed the start time then it substitutes it with it)
	 *
	 * @param startTime initial start time
	 * @return Instant being an aligned start time
	 */
	public static Instant alignStartTimeToGivenTime(final Instant startTime, final Instant relevantTime) {
		return relevantTime.isAfter(startTime) ? relevantTime : startTime;
	}

	/**
	 * Method computes difference in hours between two dates
	 *
	 * @param startTime time interval start time
	 * @param endTime   time interval end time
	 * @return time in hours
	 */
	public static double differenceInHours(final Instant startTime, final Instant endTime) {
		return (double) SECONDS.between(startTime, endTime) / SECONDS_IN_HOUR;
	}

	/**
	 * Method divides the given interval into sub-intervals of specified size
	 *
	 * @param startTime time interval start time
	 * @param endTime   time interval end time
	 * @param length    length of sub-interval
	 * @return list of sub-intervals represented by their start times
	 */
	public static Set<Instant> divideIntoSubIntervals(final Instant startTime, final Instant endTime,
			final Long length) {
		final AtomicReference<Instant> currentTime = new AtomicReference<>(startTime);
		final Set<Instant> subIntervals = new LinkedHashSet<>();

		do {
			subIntervals.add(currentTime.get());
			currentTime.getAndUpdate(time -> time.plusMillis(length));
		} while (currentTime.get().isBefore(endTime) && length != 0);

		subIntervals.add(endTime);

		return subIntervals;
	}

	/**
	 * Method computes new time by postponing the previous one by given (in real time) minutes amount
	 *
	 * @param time    time to be postponed
	 * @param minutes minutes used to postpone the time
	 * @return Instant being the postponed time
	 */
	public static Instant postponeTime(final Instant time, final long minutes) {
		final long simulationAdjustment = convertToSimulationTime(minutes * 60);
		return time.plus(simulationAdjustment, MILLIS);
	}

	/**
	 * Method converts milliseconds to string of the form "x min. y sec. z ms."
	 *
	 * @param ms number of milliseconds
	 * @return formatted string
	 */
	public static String convertMillisecondsToTimeString(final long ms) {
		final long min = TimeUnit.MILLISECONDS.toMinutes(ms);
		final long sec = TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(min);
		final long msRest = ms - (TimeUnit.MINUTES.toMillis(min) + TimeUnit.MINUTES.toMillis(sec));

		return min != 0 ?
				String.format("%02d min. %02d sec. %02d ms.", min, sec, msRest) :
				(sec != 0 ? String.format("%02d sec. %02d ms.", sec, msRest) : String.format("%02d ms.", msRest));
	}

	/**
	 * Method used in testing scenarios to set current time
	 *
	 * @param instant time which is to be set as a current time
	 * @param zone    time zone for current time
	 */
	public static void useMockTime(Instant instant, ZoneId zone) {
		CLOCK = Clock.fixed(instant, zone);
		TimeZone.setDefault(TimeZone.getTimeZone(zone));
	}

	/**
	 * Method resets mocked CLOCK
	 */
	public static void resetMockClock() {
		CLOCK = Clock.systemDefaultZone();
	}

	/**
	 * Method sets the system start time to given value
	 */
	public static void setSystemStartTime(Instant instant) {
		SYSTEM_START_TIME = instant;
	}
}
