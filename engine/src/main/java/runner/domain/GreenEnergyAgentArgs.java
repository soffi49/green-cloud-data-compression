package runner.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutableServerAgentArgs.class)
@JsonDeserialize(as = ImmutableServerAgentArgs.class)
@Value.Immutable
public interface GreenEnergyAgentArgs extends AgentArgs{

    String getMonitoringAgent();
}
