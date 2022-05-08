package domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ServerAgentArgs implements Serializable {

    @JsonProperty("name")
    private String name;
    @JsonProperty("ownerCloudNetwork")
    private String ownerCloudNetwork;
    @JsonProperty("power")
    private String power;
    @JsonProperty("price")
    private String price;

    public ServerAgentArgs() {
    }

    public ServerAgentArgs(String ownerCloudNetwork, String power, String price, String name) {
        this.ownerCloudNetwork = ownerCloudNetwork;
        this.power = power;
        this.price = price;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerCloudNetwork() {
        return ownerCloudNetwork;
    }

    public void setOwnerCloudNetwork(String ownerCloudNetwork) {
        this.ownerCloudNetwork = ownerCloudNetwork;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
