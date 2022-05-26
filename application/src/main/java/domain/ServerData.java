package domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import domain.job.Job;
import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableServerData.class)
@JsonDeserialize(as = ImmutableServerData.class)
@Immutable
public interface ServerData {

    double getServicePrice();

    double getPricePerHour();

    int getPowerInUse();

    int getAvailableCapacity();

    Job getJob();
}
