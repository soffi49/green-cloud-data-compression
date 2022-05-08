package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GreenSourceData implements Serializable {

    @JsonIgnore
    private int availableCapacity;
    @JsonProperty("availablePowerInTime")
    private int availablePowerInTime;
    @JsonProperty("pricePerPowerUnit")
    private double pricePerPowerUnit;

    public GreenSourceData(int availableCapacity, int availablePowerInTime, double pricePerPowerUnit) {
        this.availableCapacity = availableCapacity;
        this.availablePowerInTime = availablePowerInTime;
        this.pricePerPowerUnit = pricePerPowerUnit;
    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(int availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public int getAvailablePowerInTime() {
        return availablePowerInTime;
    }

    public void setAvailablePowerInTime(int availablePowerInTime) {
        this.availablePowerInTime = availablePowerInTime;
    }

    public double getPricePerPowerUnit() {
        return pricePerPowerUnit;
    }

    public void setPricePerPowerUnit(double pricePerPowerUnit) {
        this.pricePerPowerUnit = pricePerPowerUnit;
    }
}
