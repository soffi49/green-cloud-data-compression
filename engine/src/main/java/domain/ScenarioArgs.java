package domain;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.io.Serializable;
import java.util.List;

public class ScenarioArgs implements Serializable {

    @JacksonXmlElementWrapper(localName = "clientAgentsArgs")
    private List<ClientAgentArgs> clientAgentsArgs;
    @JacksonXmlElementWrapper(localName = "cloudNetworkAgentsArgs")
    private List<CloudNetworkArgs> cloudNetworkAgentsArgs;
    @JacksonXmlElementWrapper(localName = "serverAgentsArgs")
    private List<ServerAgentArgs> serverAgentsArgs;

    public ScenarioArgs() {
    }

    public ScenarioArgs(List<ClientAgentArgs> clientAgentsArgs, List<CloudNetworkArgs> cloudNetworkAgentsArgs, List<ServerAgentArgs> serverAgentsArgs) {
        this.clientAgentsArgs = clientAgentsArgs;
        this.cloudNetworkAgentsArgs = cloudNetworkAgentsArgs;
        this.serverAgentsArgs = serverAgentsArgs;
    }

    public List<ClientAgentArgs> getClientAgentsArgs() {
        return clientAgentsArgs;
    }

    public void setClientAgentsArgs(List<ClientAgentArgs> clientAgentsArgs) {
        this.clientAgentsArgs = clientAgentsArgs;
    }

    public List<CloudNetworkArgs> getCloudNetworkAgentsArgs() {
        return cloudNetworkAgentsArgs;
    }

    public void setCloudNetworkAgentsArgs(List<CloudNetworkArgs> cloudNetworkAgentsArgs) {
        this.cloudNetworkAgentsArgs = cloudNetworkAgentsArgs;
    }

    public List<ServerAgentArgs> getServerAgentsArgs() {
        return serverAgentsArgs;
    }

    public void setServerAgentsArgs(List<ServerAgentArgs> serverAgentsArgs) {
        this.serverAgentsArgs = serverAgentsArgs;
    }
}
