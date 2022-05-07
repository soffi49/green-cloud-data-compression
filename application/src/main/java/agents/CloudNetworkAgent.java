package agents;

import common.GroupConstants;
import domain.Job;
import domain.CloudNetworkData;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static common.CommonUtils.getAgentsFromDF;
import static common.CommonUtils.sendJobRequestToAgents;
import static jade.lang.acl.ACLMessage.*;

public class CloudNetworkAgent extends Agent {

    private CloudNetworkData cloudNetworkData;
    private List<AID> saAgentList;

    @Override
    protected void setup() {
        super.setup();

        cloudNetworkData = new CloudNetworkData();

        //TODO registration should be renewed
        registerCNAInDF();
        saAgentList = getSAAgentList(this);

        addBehaviour(getMessages);
    }

    private final Behaviour getMessages = new CyclicBehaviour() {
        @Override
        public void action() {
            final ACLMessage message = receive();
            if (Objects.nonNull(message)) {
                switch (message.getPerformative()) {
                    case REQUEST:
                        try {
                            final ACLMessage respond = new ACLMessage(ACLMessage.AGREE);
                            respond.setContentObject(cloudNetworkData);
                            respond.addReceiver(message.getSender());
                            send(respond);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case PROPOSE:
                        cloudNetworkData.setJobsCount(cloudNetworkData.getJobsCount() + 1);
                        try {
                            final Job newJob = (Job) message.getContentObject();
                            cloudNetworkData.getCurrentJobs().add(newJob);
                            cloudNetworkData.setInUsePower(cloudNetworkData.getInUsePower() + newJob.getPower());
                            sendJobRequestToAgents(myAgent, saAgentList, newJob);
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }

                }
            } else {
                block();
            }
        }
    };

    private List<AID> getSAAgentList(final Agent agent) {

        final DFAgentDescription template = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.SA_SERVICE_TYPE);
        serviceDescription.setOwnership(agent.getAID().getLocalName());
        template.addServices(serviceDescription);

        return getAgentsFromDF(agent, template);
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
}
