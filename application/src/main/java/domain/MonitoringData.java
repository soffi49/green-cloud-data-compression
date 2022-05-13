package domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutableGreenSourceData.class)
@JsonDeserialize(as = ImmutableGreenSourceData.class)
@Value.Immutable
public interface MonitoringData {

    int getTemperature();

    int getWindSpeed();

    double getCloudCover();
}
