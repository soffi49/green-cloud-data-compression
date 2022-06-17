package domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;

/**
 * Object storing the data passed by the Server Agent
 */
@JsonSerialize(as = ImmutableServerData.class)
@JsonDeserialize(as = ImmutableServerData.class)
@Immutable
public interface ServerData {

    /**
     * @return price for executing the given job
     */
    double getServicePrice();

    /**
     * @return power available in the server
     */
    int getAvailablePower();

    /**
     * @return unique identifier of the given job of interest
     */
    String getJobId();
}
