package agents.client;

import agents.client.behaviour.HandleCNACallForProposalResponse;
import agents.client.behaviour.SendJobCallForProposal;
import common.TimeUtils;
import domain.job.ImmutableJob;
import domain.job.Job;
import exception.IncorrectTaskDateException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientAgent extends AbstractClientAgent {

    private static final Logger logger = LoggerFactory.getLogger(ClientAgent.class);

    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();

        if (Objects.nonNull(args) && args.length == 4) {

            initializeAgent();
            final Job jobToBeExecuted = initializeAgentJob(args);

            addBehaviour(SendJobCallForProposal.createFor(this, jobToBeExecuted));
            addBehaviour(HandleCNACallForProposalResponse.createFor(this, jobToBeExecuted));

        } else {
            logger.info("Incorrect arguments: some parameters for client's job are missing - check the parameters in the documentation");
            doDelete();
        }
        try {
            TimeUnit.SECONDS.sleep(1L);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
                .jobId(arguments[3].toString())
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
}
