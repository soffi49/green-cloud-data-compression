package domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

@JsonSerialize(as = ImmutableCloudNetworkData.class)
@JsonDeserialize(as = ImmutableCloudNetworkData.class)
@Immutable
public interface CloudNetworkData {

    int getInUsePower();

    int getJobsCount();

}
