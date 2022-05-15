package agents.cloudnetwork;

import static common.CommonUtils.getAgentsFromDF;

import agents.cloudnetwork.behaviour.HandleAcceptJobProposal;
import agents.cloudnetwork.behaviour.HandleJobCallForProposal;
import agents.cloudnetwork.behaviour.HandleRejectJobProposal;
import common.GroupConstants;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.util.List;

public class CloudNetworkAgent extends AbstractCloudNetworkAgent {

    private List<AID> serviceAgentList;

    @Override
    protected void setup() {
        super.setup();
        registerCNAInDF();
        initializeAgent();

        addBehaviour(HandleJobCallForProposal.createFor(this));
        addBehaviour(HandleAcceptJobProposal.createFor(this));
        addBehaviour(HandleRejectJobProposal.createFor(this));
    }

    private void initializeAgent() {
        this.serviceAgentList = findServerAgents(this);
    }

    private void registerCNAInDF() {

        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());

        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.CNA_SERVICE_TYPE);
        serviceDescription.setName(getName());

        dfAgentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, dfAgentDescription);

        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private List<AID> findServerAgents(final Agent agent) {

        final DFAgentDescription template = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.SA_SERVICE_TYPE);
        serviceDescription.setOwnership(agent.getAID().getLocalName());
        template.addServices(serviceDescription);

        return getAgentsFromDF(agent, template);
    }

    public List<AID> getServiceAgentList() {
        return serviceAgentList;
    }
}
