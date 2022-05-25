package agents.greenenergy;

import static common.GroupConstants.GS_SERVICE_TYPE;
import static yellowpages.YellowPagesService.register;

import agents.greenenergy.behaviour.HandleMonitoringRequestResponse;
import agents.greenenergy.behaviour.HandleServerCallForProposal;
import common.GroupConstants;
import domain.location.ImmutableLocation;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.util.HashSet;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreenEnergyAgent extends AbstractGreenEnergyAgent {

    private static final Logger logger = LoggerFactory.getLogger(GreenEnergyAgent.class);

    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();
        this.setPricePerPowerUnit(Math.random() * 100);
        this.setAvailableCapacity(100);
        this.currentJobs = new HashSet<>();

        if (Objects.nonNull(args) && args.length == 3) {
            monitoringAgent = new AID(args[0].toString(), AID.ISLOCALNAME);

            try {
                location = ImmutableLocation.builder()
                    .latitude(Double.parseDouble(args[1].toString()))
                    .longitude(Double.parseDouble(args[2].toString()))
                    .build();
            } catch (NumberFormatException e) {
                logger.info("Incorrect argument: latitude and longitude must be doubles");
                doDelete();
            }
        }
        register(this, GS_SERVICE_TYPE, getName());
        addBehaviour(HandleServerCallForProposal.createFor(this));
        addBehaviour(HandleMonitoringRequestResponse.createFor(this));
    }
}
