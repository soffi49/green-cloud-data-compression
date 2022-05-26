package runner.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutableMonitoringAgentArgs.class)
@JsonDeserialize(as = ImmutableMonitoringAgentArgs.class)
@Value.Immutable
public interface MonitoringAgentArgs extends AgentArgs{
}
