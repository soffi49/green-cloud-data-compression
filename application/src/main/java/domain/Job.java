package domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jade.core.AID;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Job implements Serializable {

    @JsonProperty(value = "AID")
    private AID clientIdentifier;
    @JsonProperty(value = "startTime")
    private OffsetDateTime startTime;
    @JsonProperty(value = "endTime")
    private OffsetDateTime endTime;
    @JsonProperty(value = "power")
    private int power;


    public Job(AID clientIdentifier, OffsetDateTime startTime, OffsetDateTime endTime, int power) {
        this.clientIdentifier = clientIdentifier;
        this.startTime = startTime;
        this.endTime = endTime;
        this.power = power;
    }

    public AID getClientIdentifier() {
        return clientIdentifier;
    }

    public void setClientIdentifier(AID clientIdentifier) {
        this.clientIdentifier = clientIdentifier;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return power == job.power && startTime.equals(job.startTime) && endTime.equals(job.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, power);
    }
}
