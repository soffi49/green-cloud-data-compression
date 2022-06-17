package domain.job;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Object storing the data describing job and the cost of its execution
 * <p>
 * jobId - unique identifier of the given job
 * <p>
 * priceForJob - cost of execution of the given job
 */
@JsonDeserialize(as = ImmutablePricedJob.class)
@JsonSerialize(as = ImmutablePricedJob.class)
@Value.Immutable
public interface PricedJob {

    String getJobId();

    double getPriceForJob();
}
