package agents;

import common.GroupConstants;
import domain.Job;
import domain.ServerData;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static common.CommonUtils.getAgentsFromDF;
import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REQUEST;

public class CloudNetworkAgent extends Agent {

    private List<AID> agentSAList;
    private ServerData serverData;

    @Override
    protected void setup() {
        super.setup();

        serverData = new ServerData();

        //TODO registration should be renewed after 2h
        registerCNAInDF();
        agentSAList = getSAAgentList(this);

        final Behaviour getMessages = new CyclicBehaviour() {
            @Override
            public void action() {
                final ACLMessage message = receive();
                final Object[] args = getArguments();

                try {
                    final int power = (args.length == 1) ? Integer.parseInt(args[0].toString()) : 0;
                    serverData.setAvailablePower(power);
                } catch (NumberFormatException e) {
                    System.out.printf("The given available power is not a number!");
                    doDelete();
                }

                if (Objects.nonNull(message)) {

                    switch (message.getPerformative()) {
                        case REQUEST:
                            try {
                                if (serverData.getInUsePower() + ((Job) message.getContentObject()).getPower() > serverData.getAvailablePower()) {
                                    final ACLMessage respond = new ACLMessage(ACLMessage.REFUSE);
                                    respond.setContent("I cannot do this job");
                                    respond.addReceiver(message.getSender());
                                    send(respond);
                                } else {
                                    final ACLMessage respond = new ACLMessage(ACLMessage.AGREE);
                                    respond.setContentObject(serverData);
                                    respond.addReceiver(message.getSender());
                                    send(respond);
                                }
                            } catch (UnreadableException | IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case PROPOSE:
                            serverData.setJobsCount(serverData.getJobsCount() + 1);
                            try {
                                final Job newJob = (Job) message.getContentObject();
                                serverData.getCurrentJobs().add(newJob);
                                agentSAList.forEach(agent -> {
                                    final ACLMessage proposal = new ACLMessage(PROPOSE);
                                    try {
                                        proposal.setContentObject(newJob);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    proposal.addReceiver(agent);
                                    send(proposal);
                                });
                            } catch (UnreadableException e) {
                                e.printStackTrace();
                            }
                    }
                } else {
                    block();
                }
            }
        };
        addBehaviour(getMessages);
    }

    private List<AID> getSAAgentList(final Agent agent) {

        final DFAgentDescription template = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.CNA_SERVICE_TYPE);
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
