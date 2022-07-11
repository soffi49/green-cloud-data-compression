package agents.server.behaviour.powercheck;

import static agents.server.domain.ServerAgentConstants.PREEMPTIVE_POWER_CHECK_TIME_WINDOW;
import static common.GUIUtils.displayMessageArrow;
import static common.TimeUtils.getCurrentTime;
import static common.mapper.JobMapper.mapJobToPowerJob;
import static common.mapper.JobMapper.mapPowerJobToCheckedPowerJob;
import static java.util.Objects.isNull;
import static messages.domain.PowerCheckMessageFactory.preparePowerCheckMessage;

import agents.server.ServerAgent;
import domain.job.CheckedPowerJob;
import domain.job.JobInstanceIdentifier;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour initiating SERVER_JOB_START_CHECK_PROTOCOL, which checks the weather before job execution
 */
public class CheckWeatherBeforeJobExecution extends WakerBehaviour {

    private static final Logger logger =
        LoggerFactory.getLogger(CheckWeatherBeforeJobExecution.class);
    private final ServerAgent myServerAgent;
    private final CheckedPowerJob checkedPowerJob;

    /**
     * Behaviour constructor.
     *
     * @param agent   agent that is executing the behaviour
     * @param timeOut time after which the job will be executed
     * @param job     job that is to be executed
     */
    private CheckWeatherBeforeJobExecution(Agent agent, Date timeOut, CheckedPowerJob job) {
        super(agent, timeOut);
        this.checkedPowerJob = job;
        myServerAgent = (ServerAgent) agent;
    }

    /**
     * Method which is responsible for creating the behaviour. It calculates the time after which the job checking will
     * start. If the provided time is later than the current time then the job execution will start immediately
     *
     * @param serverAgent   agent that will execute the behaviour
     * @param jobInstanceId id of the job that will be executed
     * @return behaviour to be run
     */
    public static CheckWeatherBeforeJobExecution createFor(ServerAgent serverAgent, JobInstanceIdentifier jobInstanceId,
        boolean informCNAStart, boolean informCNAFinish) {
        var startDate =
            getCurrentTime().isAfter(jobInstanceId.getStartTime().minusSeconds(PREEMPTIVE_POWER_CHECK_TIME_WINDOW))
                ? getCurrentTime()
                : jobInstanceId.getStartTime().minusSeconds(PREEMPTIVE_POWER_CHECK_TIME_WINDOW);
        var jobToExecute = serverAgent.manage().getJobByIdAndStartDate(jobInstanceId);
        if(isNull(jobToExecute)) {
            abortExecution(serverAgent, jobInstanceId.getJobId());
            return null;
        }
        return new CheckWeatherBeforeJobExecution(
            serverAgent,
            Date.from(startDate.toInstant()),
            mapPowerJobToCheckedPowerJob(mapJobToPowerJob(jobToExecute), informCNAStart, informCNAFinish));
    }

    @Override
    protected void onWake() {
        var jobId = checkedPowerJob.getPowerJob().getJobId();
        logger.info("[{}] Checking weather before the job execution with id {}", myAgent.getName(), jobId);
        var greenSource = myServerAgent.getGreenSourceForJobMap().get(checkedPowerJob.getPowerJob().getJobId());
        if(isNull(greenSource)) {
            abortExecution(myServerAgent, jobId);
            return;
        }
        displayMessageArrow(myServerAgent, List.of(greenSource));
        myAgent.send(preparePowerCheckMessage(checkedPowerJob, greenSource.getName(), myServerAgent.getName()));
    }

    private static void abortExecution(ServerAgent serverAgent, String jobId) {
        serverAgent.manage().updateServerGUI();
        logger.error("[{}] Job with id {} must have been moved from the given server in the meantime, won't check weather.",
            serverAgent.getName(), jobId);
    }
}
