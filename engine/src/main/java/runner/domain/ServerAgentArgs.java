package runner.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableServerAgentArgs.class)
@JsonDeserialize(as = ImmutableServerAgentArgs.class)
@Immutable
public interface ServerAgentArgs extends AgentArgs {

    String getOwnerCloudNetwork();

    String getPower();

    String getPrice();

}
