package agents.server;

import static common.CommonUtils.getAgentsFromDF;

import agents.server.behaviour.ServerAgentReadMessages;
import common.GroupConstants;
import domain.GreenSourceData;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerAgent extends AbstractServerAgent {

    private static final Logger logger = LoggerFactory.getLogger(ServerAgent.class);

    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();

        if (Objects.nonNull(args) && args.length == 3) {
            ownerCloudNetworkAgent = new AID(args[0].toString(), AID.ISLOCALNAME);
            registerSAInDF();
            greenSourceAgentsList = getGSAgentList(this);
            messagesSentCount = greenSourceAgentsList.size();
            try {
                pricePerHour = Double.parseDouble(args[1].toString());
                availableCapacity = Integer.parseInt(args[2].toString());
            } catch (NumberFormatException e) {
                logger.info("The given price is not a number!");
                doDelete();
            }

            addBehaviour(new ServerAgentReadMessages(this));
        } else {
            logger.info("I don't have the corresponding Cloud Network Agent");
            doDelete();
        }

    }

    private AID chooseGreenSourceForTheJob() {
        final Comparator<Entry<AID, GreenSourceData>> compareGreenSources =
            Comparator.comparingInt(cna -> cna.getValue().getAvailablePowerInTime());
        return acceptingGreenSources.entrySet().stream().min(compareGreenSources).orElseThrow().getKey();
    }

    private void registerSAInDF() {

        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());

        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.SA_SERVICE_TYPE);
        serviceDescription.setOwnership(ownerCloudNetworkAgent.getLocalName());
        serviceDescription.setName(getName());

        dfAgentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, dfAgentDescription);

        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private List<AID> getGSAgentList(final Agent agent) {

        final DFAgentDescription template = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.GS_SERVICE_TYPE);
        serviceDescription.setOwnership(agent.getAID().getLocalName());
        template.addServices(serviceDescription);

        return getAgentsFromDF(agent, template);
    }
}
