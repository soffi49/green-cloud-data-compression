package runner.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

/**
 * Arguments for the Cloud Network Agent
 */
@JsonSerialize(as = ImmutableCloudNetworkArgs.class)
@JsonDeserialize(as = ImmutableCloudNetworkArgs.class)
@Immutable
public interface CloudNetworkArgs extends AgentArgs {

}
