package agents.client;

import agents.client.behaviour.FindCloudNetworkAgents;
import agents.client.behaviour.RequestJobExecution;
import agents.client.behaviour.WaitForJobResult;
import common.TimeUtils;
import domain.job.ImmutableJob;
import domain.job.Job;
import exception.IncorrectTaskDateException;
import jade.core.behaviours.SequentialBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Agent representing the Client that wants to have the job executed by the Cloud Network
 */
public class ClientAgent extends AbstractClientAgent {

    private static final Logger logger = LoggerFactory.getLogger(ClientAgent.class);

    /**
     * Method run at the agent's start. In initialize the Client Agent based on the given by the user arguments and
     * runs the starting behaviours - requesting the job execution and listening for job result information
     */
    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();

        if (Objects.nonNull(args) && args.length == 4) {
            initializeAgent();
            final Job jobToBeExecuted = initializeAgentJob(args);

            //TODO to be removed (added for testing purposes)
            try {
                TimeUnit.SECONDS.sleep(7);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            addBehaviour(prepareStartingBehaviour(jobToBeExecuted));
            addBehaviour(new WaitForJobResult(this));
        } else {
            logger.error("Incorrect arguments: some parameters for client's job are missing - check the parameters in the documentation");
            doDelete();
        }
    }

    /**
     * Method run before the Client is being deleted. It logs the Client's finish information.
     */
    @Override
    protected void takeDown() {
        logger.info("I'm finished. Bye!");
        super.takeDown();
    }

    private void initializeAgent() {
        this.chosenCloudNetworkAgent = null;
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

    private SequentialBehaviour prepareStartingBehaviour(final Job job) {
        var startingBehaviour = new SequentialBehaviour(this);
        startingBehaviour.addSubBehaviour(new FindCloudNetworkAgents());
        startingBehaviour.addSubBehaviour(new RequestJobExecution(this, null, job));
        return startingBehaviour;
    }
}
