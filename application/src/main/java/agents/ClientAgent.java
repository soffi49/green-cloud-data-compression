package agents;

import common.GroupConstants;
import common.TimeUtils;
import domain.Job;
import domain.ServerData;
import exception.IncorrectTaskDateException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;

import static common.CommonUtils.getAgentsFromDF;
import static jade.lang.acl.ACLMessage.AGREE;

public class ClientAgent extends Agent {

    private Job jobToBeExecuted;

    private Map<AID, ServerData> agentCNAList;
    private int responsesReceivedCount;
    private int messagesSentCount;
    private AID chosenCNA;

    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();

        agentCNAList = new HashMap<>();
        chosenCNA = null;
        responsesReceivedCount = 0;

        if (Objects.nonNull(args) && args.length == 3) {
            jobToBeExecuted = createAgentJob(args);

            final Behaviour sendJobRequest = new OneShotBehaviour() {
                @Override
                public void action() {
                    final List<AID> agentsCNA = getCNAAgentList(myAgent);

                    agentsCNA.forEach(agent -> {
                        try {
                            final ACLMessage jobRequest = new ACLMessage(ACLMessage.REQUEST);
                            jobRequest.addReceiver(agent);
                            jobRequest.setContentObject(jobToBeExecuted);
                            send(jobRequest);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            };

            final Behaviour waitForMessages = new CyclicBehaviour() {
                @Override
                public void action() {
                    final ACLMessage message = receive();

                    if (Objects.nonNull(message)) {

                        if(Objects.isNull(chosenCNA)) {
                            responsesReceivedCount++;
                        }

                        switch (message.getPerformative()) {
                            case AGREE:
                                if (responsesReceivedCount <  messagesSentCount) {
                                    try {
                                        agentCNAList.put(message.getSender(), (ServerData) message.getContentObject());
                                    } catch (UnreadableException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    final AID aidCNA = chooseCNAToExecuteJob();
                                    final ACLMessage respond = new ACLMessage(ACLMessage.PROPOSE);
                                    try {
                                        respond.setContentObject(jobToBeExecuted);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    respond.addReceiver(aidCNA);
                                    send(respond);
                                }
                                break;
                        }
                    } else {
                        block();
                    }
                }
            };
            addBehaviour(sendJobRequest);
            addBehaviour(waitForMessages);

        } else {
            System.out.print("I have no task to be executed");
            doDelete();
        }
    }

    @Override
    protected void takeDown() {
        System.out.print("I'm finished. Bye!");
        super.takeDown();
    }

    private AID chooseCNAToExecuteJob() {
        final Comparator<Map.Entry<AID, ServerData>> compareCNA =
                Comparator.comparingInt(cna -> cna.getValue().getJobsCount());
        return agentCNAList.entrySet().stream().min(compareCNA).orElseThrow().getKey();
    }

    private Job createAgentJob(final Object[] arguments) {
        try {
            final OffsetDateTime startTime = TimeUtils.convertToOffsetDateTime(arguments[0].toString());
            final OffsetDateTime endTime = TimeUtils.convertToOffsetDateTime(arguments[1].toString());
            final int power = Integer.parseInt(arguments[2].toString());

            return new Job(getAID(), startTime, endTime, power);
        } catch (IncorrectTaskDateException e) {
            System.out.printf(e.getMessage());
            doDelete();
        } catch (NumberFormatException e) {
            System.out.printf("The given power is not a number!");
            doDelete();
        }

        return null;
    }

    private List<AID> getCNAAgentList(final Agent agent) {

        final DFAgentDescription template = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.CNA_SERVICE_TYPE);
        template.addServices(serviceDescription);

        return getAgentsFromDF(agent, template);
    }
}
