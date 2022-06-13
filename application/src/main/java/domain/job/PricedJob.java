package domain.job;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@JsonDeserialize(as = ImmutablePricedJob.class)
@JsonSerialize(as = ImmutablePricedJob.class)
@Value.Immutable
public interface PricedJob {

    String getJobId();

    double getPriceForJob();
}
