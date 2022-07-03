package agents.greenenergy.behaviour.errorhandling;

import static common.AlgorithmUtils.findJobsWithinPower;
import static common.TimeUtils.isWithinTimeStamp;
import static common.constant.MessageProtocolConstants.POWER_UNEXPECTED_CHANGE;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.ImmutableJobTransfer;
import domain.job.PowerJob;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Behaviour sends the information to the server that the green source will have unexpected power shortage
 * at given time
 */
public class InformAboutPowerShortage extends OneShotBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(InformAboutPowerShortage.class);

    private final OffsetDateTime shortageStartTime;
    private final double recalculatedAvailablePower;
    private final GreenEnergyAgent myGreenAgent;

    /**
     * Behaviour constructor
     *
     * @param myAgent                    agent executing the behaviour
     * @param shortageStartTime          start time when the power shortage will happen
     * @param recalculatedAvailablePower power available during the power shortage
     */
    public InformAboutPowerShortage(Agent myAgent, OffsetDateTime shortageStartTime, double recalculatedAvailablePower) {
        super(myAgent);
        this.shortageStartTime = shortageStartTime;
        this.recalculatedAvailablePower = recalculatedAvailablePower;
        this.myGreenAgent = (GreenEnergyAgent) myAgent;
    }

    /**
     * Method which is responsible for sending the power shortage information to the parent server.
     * In the message, the list of jobs that cannot be executed by the green source is passed along with the
     * start time of the power shortage
     */
    @Override
    public void action() {
        final List<PowerJob> affectedJobs = getAffectedPowerJobs();
        final List<PowerJob> jobsToKeep = findJobsWithinPower(affectedJobs, recalculatedAvailablePower);
        final List<PowerJob> jobsToTransfer = affectedJobs.stream().filter(job -> !jobsToKeep.contains(job)).toList();
        final ImmutableJobTransfer content = ImmutableJobTransfer.builder().jobList(jobsToTransfer).startTime(shortageStartTime).build();

        final ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        try {
            message.setContent(getMapper().writeValueAsString(content));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        message.setProtocol(POWER_UNEXPECTED_CHANGE);
        message.addReceiver(myGreenAgent.getOwnerServer());
        myGreenAgent.send(message);
    }

    private List<PowerJob> getAffectedPowerJobs() {
        return myGreenAgent.getPowerJobs().keySet().stream()
                .filter(job -> isWithinTimeStamp(job.getStartTime(), job.getEndTime(), shortageStartTime))
                .toList();
    }
}
