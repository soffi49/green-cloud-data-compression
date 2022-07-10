package agents.server.behaviour.powershortage.listener.source;

import static common.TimeUtils.getCurrentTime;
import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_FINISH_ALERT_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import domain.job.Job;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour is responsible for listening for the information that the power shortage has finsihed
 */
public class ListenForSourcePowerShortageFinish extends CyclicBehaviour {

    private static final Logger logger =
            LoggerFactory.getLogger(ListenForSourcePowerShortageFinish.class);
    private static final MessageTemplate messageTemplate =
            and(MatchPerformative(INFORM), MatchProtocol(POWER_SHORTAGE_FINISH_ALERT_PROTOCOL));

    private ServerAgent myServerAgent;

    /**
     * Method runs at the start of the behaviour. It casts the abstract agent to agent of type Server
     * Agent
     */
    @Override
    public void onStart() {
        super.onStart();
        myServerAgent = (ServerAgent) myAgent;
    }

    /**
     * Method listens for the message coming from the Green Source agent informing that the power
     * shortage has finished and that the methods which were on hold can now be supplied with the
     * green source power.
     */
    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);

        if (Objects.nonNull(inform)) {
            try {
                logger.info("[{}] Received the information that the power shortage is finished. Using green energy to power up the job", myAgent.getName());
                final JobInstanceIdentifier jobInstanceIdentifier = getMapper().readValue(inform.getContent(), JobInstanceIdentifier.class);
                final Job job = myServerAgent.manage().getJobByIdAndStartDate(jobInstanceIdentifier);
                if (Objects.nonNull(job) && myServerAgent.getServerJobs().get(job).equals(JobStatusEnum.IN_PROGRESS_BACKUP_ENERGY)) {
                    logger.info("[{}] Supplying job {} with green energy", myAgent.getName(), job.getJobId());
                    if (job.getStartTime().isAfter(getCurrentTime())) {
                        myServerAgent.getServerJobs().replace(job, JobStatusEnum.ACCEPTED);
                    } else {
                        myServerAgent.getServerJobs().replace(job, JobStatusEnum.IN_PROGRESS);
                    }
                    myServerAgent.manage().updateServerGUI();
                    logger.info("[{}] Passing information to CNA that job {} is supplied back using green energy", myAgent.getName(), job.getJobId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
