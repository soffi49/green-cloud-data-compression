package agents.server;

import static common.GroupConstants.SA_SERVICE_TYPE;
import static yellowpages.YellowPagesService.register;

import agents.server.behaviour.HandleCNAAcceptProposal;
import agents.server.behaviour.HandleCNAJobCallForProposal;
import agents.server.behaviour.HandleCNARejectProposal;
import agents.server.behaviour.HandleGreenSourceCallForProposalResponse;
import agents.server.behaviour.HandleGreenSourceJobInform;
import jade.core.AID;
import jade.core.behaviours.ParallelBehaviour;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerAgent extends AbstractServerAgent {

    private static final Logger logger = LoggerFactory.getLogger(ServerAgent.class);

    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();

        greenSourceForJobMap = new HashMap<>();
        currentJobs = new HashSet<>();

        if (Objects.nonNull(args) && args.length == 3) {
            ownerCloudNetworkAgent = new AID(args[0].toString(), AID.ISLOCALNAME);
            register(this, SA_SERVICE_TYPE, getName());
            try {
                pricePerHour = Double.parseDouble(args[1].toString());
                availableCapacity = Integer.parseInt(args[2].toString());
            } catch (NumberFormatException e) {
                logger.info("The given price is not a number!");
                doDelete();
            }
            ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
            parallelBehaviour.addSubBehaviour(HandleGreenSourceCallForProposalResponse.createFor(this));
            parallelBehaviour.addSubBehaviour(HandleCNAJobCallForProposal.createFor(this));
            parallelBehaviour.addSubBehaviour(HandleCNAAcceptProposal.createFor(this));
            parallelBehaviour.addSubBehaviour(HandleGreenSourceJobInform.createFor(this));
            parallelBehaviour.addSubBehaviour(HandleCNARejectProposal.createFor(this));
            addBehaviour(parallelBehaviour);
        } else {
            logger.info("I don't have the corresponding Cloud Network Agent");
            doDelete();
        }

    }
}
