package runner.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutableGreenEnergyAgentArgs.class)
@JsonDeserialize(as = ImmutableGreenEnergyAgentArgs.class)
@Value.Immutable
public interface GreenEnergyAgentArgs extends AgentArgs{

    String getMonitoringAgent();

    String getOwnerSever();

    String getLatitude();

    String getLongitude();
}
