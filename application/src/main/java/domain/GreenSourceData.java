package domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import domain.job.Job;
import org.immutables.value.Value.Immutable;


@JsonSerialize(as = ImmutableGreenSourceData.class)
@JsonDeserialize(as = ImmutableGreenSourceData.class)
@Immutable
public interface GreenSourceData {

    int getAvailablePowerInTime();

    double getPricePerPowerUnit();

    Job getJob();
}