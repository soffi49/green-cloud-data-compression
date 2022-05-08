package runner.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CloudNetworkArgs implements Serializable {

    @JsonProperty("name")
    private String name;

    public CloudNetworkArgs() {
    }

    public CloudNetworkArgs(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
