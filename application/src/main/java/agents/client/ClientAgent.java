package agents.client;


import agents.client.behaviour.ClientAgentReadMessages;
import agents.client.behaviour.SendJobProposal;
import common.GroupConstants;
import common.TimeUtils;
import domain.CloudNetworkData;
import domain.Job;
import exception.IncorrectTaskDateException;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.time.OffsetDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static common.CommonUtils.getAgentsFromDF;

public class ClientAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger(ClientAgent.class);

    private Map<AID, CloudNetworkData> cloudNetworkAgentList;
    private AID chosenCloudNetworkAgent;

    private int messagesSentCount;
    private int responsesReceivedCount;

    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();

        if (Objects.nonNull(args) && args.length == 3) {

            cloudNetworkAgentList = new HashMap<>();
            chosenCloudNetworkAgent = null;
            responsesReceivedCount = 0;
            messagesSentCount = 0;
            final Job jobToBeExecuted = initializeAgentJob(args);

            addBehaviour(SendJobProposal.createFor(this, jobToBeExecuted));
            addBehaviour(ClientAgentReadMessages.createFor(this, jobToBeExecuted));

        } else {
            logger.info("I have no task to be executed");
            doDelete();
        }
    }

    @Override
    protected void takeDown() {
        logger.info("I'm finished. Bye!");
        super.takeDown();
    }

    private Job initializeAgentJob(final Object[] arguments) {
        try {
            final OffsetDateTime startTime = TimeUtils.convertToOffsetDateTime(arguments[0].toString());
            final OffsetDateTime endTime = TimeUtils.convertToOffsetDateTime(arguments[1].toString());
            final int power = Integer.parseInt(arguments[2].toString());
            return new Job(getAID(), startTime, endTime, power);
        } catch (IncorrectTaskDateException e) {
            logger.error(e.getMessage());
            doDelete();
        } catch (NumberFormatException e) {
            logger.error("The given power is not a number!");
            doDelete();
        }
        return null;
    }

    public List<AID> initializeCloudNetworkAgentList(final Agent agent) {

        final DFAgentDescription template = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.CNA_SERVICE_TYPE);
        template.addServices(serviceDescription);

        return getAgentsFromDF(agent, template);
    }

    public void setMessagesSentCount(int messagesSentCount) {
        this.messagesSentCount = messagesSentCount;
    }

    public void setChosenCloudNetworkAgent(AID chosenCloudNetworkAgent) {
        this.chosenCloudNetworkAgent = chosenCloudNetworkAgent;
    }

    public void setResponsesReceivedCount(int responsesReceivedCount) {
        this.responsesReceivedCount = responsesReceivedCount;
    }

    public AID getChosenCloudNetworkAgent() {
        return chosenCloudNetworkAgent;
    }

    public int getMessagesSentCount() {
        return messagesSentCount;
    }

    public int getResponsesReceivedCount() {
        return responsesReceivedCount;
    }

    public Map<AID, CloudNetworkData> getCloudNetworkAgentList() {
        return cloudNetworkAgentList;
    }
}
