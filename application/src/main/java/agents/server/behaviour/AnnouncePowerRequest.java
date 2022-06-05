package agents.server.behaviour;

import static common.MessagingUtils.rejectJobOffers;
import static java.time.temporal.ChronoUnit.HOURS;
import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import agents.server.message.SendJobVolunteerProposalMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import common.message.SendRefuseProposalMessage;
import domain.GreenSourceData;
import exception.IncorrectGreenSourceOfferException;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 * Behaviours responsible for passing the job/power request to green sources and choosing one to provide power
 */
public class AnnouncePowerRequest extends ContractNetInitiator {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncePowerRequest.class);

    private ServerAgent myServerAgent;

    public AnnouncePowerRequest(final Agent a, final ACLMessage powerRequest, final DataStore dataStore) {
        super(a, powerRequest, dataStore);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.myServerAgent = (ServerAgent) myAgent;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        final List<ACLMessage> proposals = ((Vector<ACLMessage>) responses).stream()
                .filter(response -> response.getPerformative() == ACLMessage.PROPOSE)
                .toList();

        if (responses.isEmpty()) {
            logger.info("[{}] No responses were retrieved", myAgent);
        } else if (proposals.isEmpty()) {
            logger.info("[{}] No Green Sources available - sending refuse message to Cloud Network Agent", myAgent);
            final ACLMessage replyMessage = (ACLMessage) getDataStore().get(myServerAgent.getOwnerCloudNetworkAgent());
            myAgent.send(SendRefuseProposalMessage.create(replyMessage).getMessage());
        } else {
            logger.info("[{}] Sending job volunteering offer to Cloud Network Agent", myAgent);
            final ACLMessage chosenGreenSourceOffer = chooseGreenSourceToExecuteJob(proposals);
            getDataStore().put(chosenGreenSourceOffer.getSender(), chosenGreenSourceOffer);
            final ACLMessage replyMessage = (ACLMessage) getDataStore().get(myServerAgent.getOwnerCloudNetworkAgent());

            GreenSourceData chosenGreenSourceData;
            try {
                chosenGreenSourceData = getMapper().readValue(chosenGreenSourceOffer.getContent(), GreenSourceData.class);
            } catch (JsonProcessingException e) {
                throw new IncorrectGreenSourceOfferException();
            }
            final double servicePrice = calculateServicePrice(chosenGreenSourceData);
            final ACLMessage proposalMessage = SendJobVolunteerProposalMessage.create(myServerAgent,
                                                                                      servicePrice,
                                                                                      chosenGreenSourceData.getJob(),
                                                                                      replyMessage).getMessage();
            myServerAgent.getGreenSourceForJobMap().put(chosenGreenSourceData.getJob(), chosenGreenSourceOffer.getSender());

            myAgent.addBehaviour(new VolunteerForJob(myAgent, proposalMessage, getDataStore()));
            rejectJobOffers(myAgent, chosenGreenSourceData.getJob(), chosenGreenSourceOffer, proposals);
        }
    }

    private double calculateServicePrice(final GreenSourceData greenSourceData) {
        var powerCost = greenSourceData.getJob().getPower() * greenSourceData.getPricePerPowerUnit();
        var computingCost =
                HOURS.between(greenSourceData.getJob().getEndTime(), greenSourceData.getJob().getStartTime())
                        * myServerAgent.getPricePerHour();
        return powerCost + computingCost;
    }

    private ACLMessage chooseGreenSourceToExecuteJob(final List<ACLMessage> greenSourceOffers) {
        final Comparator<ACLMessage> compareGreenSources =
                Comparator.comparingInt(greenSource -> {
                    try {
                        return getMapper().readValue(greenSource.getContent(), GreenSourceData.class).getAvailablePowerInTime();
                    } catch (final JsonProcessingException e) {
                        throw new IncorrectGreenSourceOfferException();
                    }
                });
        return greenSourceOffers.stream().min(compareGreenSources).orElseThrow();
    }
}
