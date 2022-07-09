package agents.server;

import static common.constant.DFServiceConstants.*;
import static yellowpages.YellowPagesService.register;
import static yellowpages.YellowPagesService.search;

import agents.server.behaviour.ReceiveJobRequest;
import agents.server.behaviour.listener.ListenForJobStarStatusRequest;
import agents.server.behaviour.listener.ListenForPowerConfirmation;
import agents.server.behaviour.listener.ListenForServerEvent;
import agents.server.behaviour.listener.ListenForUnfinishedJobInformation;
import agents.server.behaviour.powershortage.listener.network.ListenForJobTransferCancellation;
import agents.server.behaviour.powershortage.listener.network.ListenForJobTransferConfirmation;
import agents.server.behaviour.powershortage.listener.network.ListenForJobTransferRefusal;
import agents.server.behaviour.powershortage.listener.source.ListenForSourcePowerShortage;
import agents.server.behaviour.powershortage.listener.source.ListenForSourceTransferConfirmation;
import agents.server.domain.ServerStateManagement;
import behaviours.ReceiveGUIController;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Agent representing the Server Agent which executes the clients' jobs
 */
public class ServerAgent extends AbstractServerAgent {

    private static final Logger logger = LoggerFactory.getLogger(ServerAgent.class);

    /**
     * Method run at the agent's start. In initialize the Server Agent based on the given by the user arguments,
     * registers it in the DF and then runs the starting behaviours - listening for the job requests
     */
    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();
        initializeAgent(args);
        register(this, SA_SERVICE_TYPE, SA_SERVICE_NAME, ownerCloudNetworkAgent.getName());
        addBehaviour(new ReceiveGUIController(this, behavioursRunAtStart()));
    }

    @Override
    protected void takeDown() {
        logger.info("I'm finished. Bye!");
        getGuiController().removeAgentNodeFromGraph(getAgentNode());
        super.takeDown();
    }

    private void initializeAgent(final Object[] args) {
        if (Objects.nonNull(args) && args.length == 3) {
            this.stateManagement = new ServerStateManagement(this);
            this.ownedGreenSources = search(this, GS_SERVICE_TYPE, getName());
            if (ownedGreenSources.isEmpty()) {
                logger.info("I have no corresponding green sources!");
                doDelete();
            }
            this.ownerCloudNetworkAgent = new AID(args[0].toString(), AID.ISLOCALNAME);
            try {
                this.pricePerHour = Double.parseDouble(args[1].toString());
                this.maximumCapacity = Integer.parseInt(args[2].toString());
            } catch (final NumberFormatException e) {
                logger.info("The given price is not a number!");
                doDelete();
            }
        } else {
            logger.info("Incorrect arguments: some parameters for server agent are missing - check the parameters in the documentation");
            doDelete();
        }
    }

    private List<Behaviour> behavioursRunAtStart() {
        final ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        parallelBehaviour.addSubBehaviour(new ReceiveJobRequest());
        parallelBehaviour.addSubBehaviour(new ListenForPowerConfirmation());
        parallelBehaviour.addSubBehaviour(new ListenForUnfinishedJobInformation());
        parallelBehaviour.addSubBehaviour(new ListenForJobTransferConfirmation(this));
        parallelBehaviour.addSubBehaviour(new ListenForSourcePowerShortage());
        parallelBehaviour.addSubBehaviour(new ListenForSourceTransferConfirmation());
        parallelBehaviour.addSubBehaviour(new ListenForServerEvent(this));
        parallelBehaviour.addSubBehaviour(new ListenForJobTransferCancellation(this));
        parallelBehaviour.addSubBehaviour(new ListenForJobStarStatusRequest());
        parallelBehaviour.addSubBehaviour(new ListenForJobTransferRefusal(this));
        return Collections.singletonList(parallelBehaviour);
    }
}
