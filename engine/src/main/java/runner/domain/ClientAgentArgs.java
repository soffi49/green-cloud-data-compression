package runner.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

/**
 * Arguments of Client Agent
 */
@JsonSerialize(as = ImmutableClientAgentArgs.class)
@JsonDeserialize(as = ImmutableClientAgentArgs.class)
@Immutable
public interface ClientAgentArgs extends AgentArgs {

    /**
     * @return unique job identifier
     */
    String getJobId();

    /**
     * @return job execution start time
     */
    String getStartDate();

    /**
     * @return job execution finish time
     */
    String getEndDate();

    /**
     * @return power required for the job
     */
    String getPower();

}