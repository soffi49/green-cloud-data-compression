package agents.greenenergy;

import static common.constant.DFServiceConstants.GS_SERVICE_TYPE;
import static yellowpages.YellowPagesService.register;

import agents.greenenergy.behaviour.ListenForFinishedJobs;
import agents.greenenergy.behaviour.ReceivePowerRequest;
import domain.location.ImmutableLocation;
import jade.core.AID;
import java.util.HashSet;
import java.util.Objects;

import jade.core.behaviours.ParallelBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreenEnergyAgent extends AbstractGreenEnergyAgent {

    private static final Logger logger = LoggerFactory.getLogger(GreenEnergyAgent.class);

    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();
        //TODO move that to the args
        this.setPricePerPowerUnit(Math.random() * 100);
        this.setAvailableCapacity(100);
        this.currentJobs = new HashSet<>();

        if (Objects.nonNull(args) && args.length == 4) {
            monitoringAgent = new AID(args[0].toString(), AID.ISLOCALNAME);
            ownerServer = new AID(args[1].toString(), AID.ISLOCALNAME);
            try {
                location = ImmutableLocation.builder()
                        .latitude(Double.parseDouble(args[2].toString()))
                        .longitude(Double.parseDouble(args[3].toString()))
                        .build();
            } catch (NumberFormatException e) {
                logger.info("Incorrect argument: latitude and longitude must be doubles");
                doDelete();
            }
        } else {
            logger.info("Incorrect arguments: some parameters for green source agent are missing - check the parameters in the documentation");
            doDelete();
        }
        register(this, GS_SERVICE_TYPE, getName(), ownerServer.getName());
        addBehaviour(createInitialBehaviour());
    }

    private ParallelBehaviour createInitialBehaviour() {
        final ParallelBehaviour behaviour = new ParallelBehaviour();
        behaviour.addSubBehaviour(ReceivePowerRequest.createFor(this));
        behaviour.addSubBehaviour(ListenForFinishedJobs.createFor(this));
        return behaviour;
    }
}