package domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

/**
 * Object storing the data passed by the Monitoring Agent
 */
@JsonSerialize(as = ImmutableMonitoringData.class)
@JsonDeserialize(as = ImmutableMonitoringData.class)
@Immutable
public interface MonitoringData {

    /**
     * @return temperature at given location for given time period
     */
    int getTemperature();

    /**
     * @return wind speed at given location for given time period
     */
    int getWindSpeed();

    /**
     * @return cloudiness at given location for given time period
     */
    double getCloudCover();
}
