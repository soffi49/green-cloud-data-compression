package agents.server;

import static common.constant.DFServiceConstants.*;
import static yellowpages.YellowPagesService.register;
import static yellowpages.YellowPagesService.search;

import agents.server.behaviour.ReceiveJobRequest;
import jade.core.AID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class ServerAgent extends AbstractServerAgent {

    private static final Logger logger = LoggerFactory.getLogger(ServerAgent.class);

    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();

        greenSourceForJobMap = new HashMap<>();
        currentJobs = new HashSet<>();

        if (Objects.nonNull(args) && args.length == 3) {
            initializeAgent(args);
            register(this, SA_SERVICE_TYPE, SA_SERVICE_NAME, ownerCloudNetworkAgent.getName());

            addBehaviour(new ReceiveJobRequest());
        } else {
            logger.info("Incorrect arguments: some parameters for server agent are missing - check the parameters in the documentation");
            doDelete();
        }
    }

    @Override
    protected void takeDown() {
        logger.info("I'm finished. Bye!");
        super.takeDown();
    }

    private void initializeAgent(final Object[] args) {
        this.ownedGreenSources = search(this, GS_SERVICE_TYPE, getName());

        if(ownedGreenSources.isEmpty()) {
            logger.info("I have no corresponding green sources!");
            doDelete();
        }

        this.ownerCloudNetworkAgent = new AID(args[0].toString(), AID.ISLOCALNAME);
        try {
            this.pricePerHour = Double.parseDouble(args[1].toString());
            this.availableCapacity = Integer.parseInt(args[2].toString());
        } catch (final NumberFormatException e) {
            logger.info("The given price is not a number!");
            doDelete();
        }

    }
}
