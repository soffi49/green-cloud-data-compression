package agents.greenenergy.behaviour.powershortage.listener;

import static common.constant.MessageProtocolConstants.POWER_SHORTAGE_SERVER_TRANSFER_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;
import domain.job.PowerJob;
import domain.job.PowerShortageJob;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListenForParentServerPowerShortage extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForParentServerPowerShortage.class);
    private static final MessageTemplate messageTemplate = and(MatchPerformative(INFORM), MatchProtocol(POWER_SHORTAGE_SERVER_TRANSFER_PROTOCOL));

    private final GreenEnergyAgent myGreenEnergyAgent;

    /**
     * Behaviour constructor
     *
     * @param myAgent agent executing the behaviour
     */
    public ListenForParentServerPowerShortage(final Agent myAgent) {
        super(myAgent);
        this.myGreenEnergyAgent = (GreenEnergyAgent) myAgent;
    }

    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);

        if (Objects.nonNull(inform)) {
            try {
                final PowerShortageJob powerShortageJob = getMapper().readValue(inform.getContent(), PowerShortageJob.class);
                if(Objects.nonNull(myGreenEnergyAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId()))) {
                    logger.info("[{}] Received information about job {} power shortage in server. Updating green source state",
                                myGreenEnergyAgent.getLocalName(), powerShortageJob.getJobInstanceId().getJobId());
                    final PowerJob jobToDivide = myGreenEnergyAgent.manage().getJobByIdAndStartDate(powerShortageJob.getJobInstanceId());
                    myGreenEnergyAgent.manage().divideJobForPowerShortage(jobToDivide, powerShortageJob.getPowerShortageStart());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
