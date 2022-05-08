package agents.server;

import common.GroupConstants;
import domain.GreenSourceData;
import domain.Job;
import domain.ServerData;
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

import java.util.*;

import static common.CommonUtils.getAgentsFromDF;
import static common.CommonUtils.sendJobRequestToAgents;
import static jade.lang.acl.ACLMessage.*;

public class ServerAgent extends Agent {

    private AID ownerCNA;

    private List<AID> gsAgentList;
    private Map<AID, GreenSourceData> gsAcceptingJob;
    private AID chosenGS;

    private int messagesSentCount;
    private int responsesReceivedCount;

    private ServerData serverData;

    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();

        if (Objects.nonNull(args) && args.length == 3) {
            ownerCNA = new AID(args[0].toString(), AID.ISLOCALNAME);
            registerSAInDF();
            gsAgentList = getGSAgentList(this);
            messagesSentCount = gsAgentList.size();
            try {
                serverData = new ServerData(Double.parseDouble(args[1].toString()), Integer.parseInt(args[2].toString()));
            } catch (NumberFormatException e) {
                System.out.printf("The given price is not a number!");
                doDelete();
            }

            addBehaviour(getMessages);
        } else {
            System.out.print("I don't have the corresponding Cloud Network Agent");
            doDelete();
        }

    }

    private final Behaviour getMessages = new CyclicBehaviour() {
        @Override
        public void action() {
            final ACLMessage message = receive();
            if (Objects.nonNull(message)) {
                switch (message.getPerformative()) {
                    case REQUEST:
                        try {
                            final Job receivedJob = (Job) message.getContentObject();
                            if (receivedJob.getPower() + serverData.getPowerInUse() <= serverData.getAvailableCapacity()) {
                                sendJobRequestToAgents(myAgent, gsAgentList, receivedJob);
                                gsAcceptingJob = new HashMap<>();
                                responsesReceivedCount = 0;
                            } else {
                                final ACLMessage respond = new ACLMessage(REFUSE);
                                respond.setContent("Refuse");
                                respond.addReceiver(ownerCNA);
                                send(respond);
                            }
                        } catch (UnreadableException e) {
                            e.printStackTrace();
                        }
                        break;
                    case REFUSE:
                    case AGREE:
                        if (responsesReceivedCount < messagesSentCount) {
                            try {
                                gsAcceptingJob.put(message.getSender(), (GreenSourceData) message.getContentObject());
                            } catch (UnreadableException e) {
                                e.printStackTrace();
                            }
                        } else {
                            final int messageType = gsAcceptingJob.isEmpty() ? REFUSE : AGREE;
                            final String messageContent = gsAcceptingJob.isEmpty() ? "Refuse" : "Agree";
                            final ACLMessage respond = new ACLMessage(messageType);
                            respond.setContent(messageContent);
                            respond.addReceiver(ownerCNA);
                            send(respond);
                        }
                        break;
                    case PROPOSE:
                }
            } else {
                block();
            }
        }
    };

    private AID chooseGreenSourceForTheJob() {
        final Comparator<Map.Entry<AID, GreenSourceData>> compareGreenSources =
                Comparator.comparingInt(cna -> cna.getValue().getAvailablePowerInTime());
        return gsAcceptingJob.entrySet().stream().min(compareGreenSources).orElseThrow().getKey();
    }

    private void registerSAInDF() {

        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());

        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.SA_SERVICE_TYPE);
        serviceDescription.setOwnership(ownerCNA.getLocalName());
        serviceDescription.setName(getName());

        dfAgentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, dfAgentDescription);

        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private List<AID> getGSAgentList(final Agent agent) {

        final DFAgentDescription template = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.GS_SERVICE_TYPE);
        serviceDescription.setOwnership(agent.getAID().getLocalName());
        template.addServices(serviceDescription);

        return getAgentsFromDF(agent, template);
    }
}
