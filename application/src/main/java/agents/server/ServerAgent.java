package agents.server;

import static common.constant.DFServiceConstants.SA_SERVICE_NAME;
import static common.constant.DFServiceConstants.SA_SERVICE_TYPE;
import static yellowpages.YellowPagesService.register;

import agents.server.behaviour.*;
import jade.core.AID;
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
            register(this, SA_SERVICE_TYPE, SA_SERVICE_NAME, ownerCloudNetworkAgent.getName());
            try {
                pricePerHour = Double.parseDouble(args[1].toString());
                availableCapacity = Integer.parseInt(args[2].toString());
            } catch (NumberFormatException e) {
                logger.info("The given price is not a number!");
                doDelete();
            }

            addBehaviour(HandleGreenSourceCallForProposalResponse.createFor(this));
            addBehaviour(HandleCNAJobCallForProposal.createFor(this));
            addBehaviour(HandleCNAAcceptProposal.createFor(this));
            addBehaviour(HandleGreenSourceJobInform.createFor(this));
            addBehaviour(HandleCNARejectProposal.createFor(this));
        } else {
            logger.info("I don't have the corresponding Cloud Network Agent");
            doDelete();
        }

    }
}
