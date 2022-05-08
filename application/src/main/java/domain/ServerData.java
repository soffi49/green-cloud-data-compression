package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import domain.job.Job;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerData implements Serializable {

    @JsonIgnore
    private List<Job> currentJobs;
    @JsonProperty("pricePerHour")
    private double pricePerHour;
    @JsonProperty("powerInUse")
    private int powerInUse;
    @JsonProperty("availableCapacity")
    private int availableCapacity;

    public ServerData(double pricePerHour, int availableCapacity) {
        this.pricePerHour = pricePerHour;
        this.availableCapacity = availableCapacity;
        currentJobs = new ArrayList<>();
        powerInUse = 0;
    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(int availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public int getPowerInUse() {
        return powerInUse;
    }

    public void setPowerInUse(int powerInUse) {
        this.powerInUse = powerInUse;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public List<Job> getCurrentJobs() {
        return currentJobs;
    }

    public void setCurrentJobs(List<Job> currentJobs) {
        this.currentJobs = currentJobs;
    }
}
