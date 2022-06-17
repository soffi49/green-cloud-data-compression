package domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import domain.job.Job;
import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableMonitoringData.class)
@JsonDeserialize(as = ImmutableMonitoringData.class)
@Immutable
public interface MonitoringData {

    Job getJob();

    Double getTemperature();

    Double getWindSpeed();

    Double getCloudCover();
}
