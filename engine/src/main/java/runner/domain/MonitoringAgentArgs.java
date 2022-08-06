package runner.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.immutables.value.Value;

/**
 * Arguments of the Monitoring Agent
 */
@JsonSerialize(as = ImmutableMonitoringAgentArgs.class)
@JsonDeserialize(as = ImmutableMonitoringAgentArgs.class)
@Value.Immutable
public interface MonitoringAgentArgs extends AgentArgs {
}
