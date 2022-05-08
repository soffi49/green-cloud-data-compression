package runner.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ClientAgentArgs implements Serializable {

    @JsonProperty("name")
    private String name;
    @JsonProperty("startDate")
    private String startDate;
    @JsonProperty("endDate")
    private String endDate;
    @JsonProperty("power")
    private String power;

    public ClientAgentArgs() {
    }

    public ClientAgentArgs(String startDate, String endDate, String power, String name) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.power = power;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }
}
