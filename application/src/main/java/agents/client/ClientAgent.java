package agents.client;

import agents.client.behaviour.SendJobCallForProposal;
import agents.client.behaviour.HandleCallForProposalResponse;
import common.TimeUtils;
import domain.job.ImmutableJob;
import domain.job.Job;
import exception.IncorrectTaskDateException;
import jade.core.AID;
import jade.core.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ClientAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger(ClientAgent.class);

    private AID chosenCloudNetworkAgent;
    private int messagesSentCount;

    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();

        if (Objects.nonNull(args) && args.length == 3) {

            initializeAgent();
            final Job jobToBeExecuted = initializeAgentJob(args);

            addBehaviour(SendJobCallForProposal.createFor(this, jobToBeExecuted));
            addBehaviour(HandleCallForProposalResponse.createFor(this, jobToBeExecuted));

        } else {
            logger.info("Incorrect arguments: the given task to specified according to the documentation");
            doDelete();
        }
    }

    @Override
    protected void takeDown() {
        logger.info("I'm finished. Bye!");
        super.takeDown();
    }

    private void initializeAgent() {
        this.chosenCloudNetworkAgent = null;
        this.messagesSentCount = 0;
    }

    private Job initializeAgentJob(final Object[] arguments) {
        try {
            return ImmutableJob.builder()
                    .clientIdentifier(getAID().getName())
                    .startTime(TimeUtils.convertToOffsetDateTime(arguments[0].toString()))
                    .endTime(TimeUtils.convertToOffsetDateTime(arguments[1].toString()))
                    .power(Integer.parseInt(arguments[2].toString()))
                    .build();
        } catch (IncorrectTaskDateException e) {
            logger.error(e.getMessage());
            doDelete();
        } catch (NumberFormatException e) {
            logger.error("The given power is not a number!");
            doDelete();
        }
        return null;
    }

    public void setMessagesSentCount(int messagesSentCount) {
        this.messagesSentCount = messagesSentCount;
    }

    public void setChosenCloudNetworkAgent(AID chosenCloudNetworkAgent) {
        this.chosenCloudNetworkAgent = chosenCloudNetworkAgent;
    }

    public int getMessagesSentCount() {
        return messagesSentCount;
    }


}
