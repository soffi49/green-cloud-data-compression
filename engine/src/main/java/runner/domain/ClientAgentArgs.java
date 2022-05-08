package runner.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;


@JsonSerialize(as = ImmutableClientAgentArgs.class)
@JsonDeserialize(as = ImmutableClientAgentArgs.class)
@Immutable
public interface ClientAgentArgs extends AgentArgs {

    String getStartDate();

    String getEndDate();

    String getPower();

}