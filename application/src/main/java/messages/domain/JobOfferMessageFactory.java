package messages.domain;

import static jade.lang.acl.ACLMessage.PROPOSE;
import static mapper.JsonMapper.getMapper;

import agents.server.ServerAgent;
import com.fasterxml.jackson.core.JsonProcessingException;
import domain.ImmutableServerData;
import domain.ServerData;
import domain.job.ImmutablePricedJob;
import domain.job.Job;
import domain.job.PricedJob;
import jade.lang.acl.ACLMessage;

import java.io.IOException;

/**
 * Class storing methods used in creating the job offers
 */
public class JobOfferMessageFactory {

    /**
     * Method used in making the proposal message containing the offer made by the Cloud Network Agent
     * for client
     *
     * @param server       data sent by the server which will execute the job
     * @param replyMessage reply message as which the job offer is to be sent
     * @return proposal ACLMessage
     */
    public static ACLMessage makeJobOfferForClient(final ServerData server, final ACLMessage replyMessage) {
        final PricedJob pricedJob = ImmutablePricedJob.builder()
                .jobId(server.getJobId())
                .priceForJob(server.getServicePrice())
                .build();
        replyMessage.setPerformative(PROPOSE);
        try {
            replyMessage.setContent(getMapper().writeValueAsString(pricedJob));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return replyMessage;
    }


    /**
     * Method used in making the proposal message containing the offer made by the Server Agent
     *
     * @param serverAgent  server which is making the job offer proposal
     * @param servicePrice cost of executing the given job
     * @param jobId        unique identifier of the job of interest
     * @param replyMessage reply message as which the job offer is to be sent
     * @return proposal ACLMessage
     */
    public static ACLMessage makeServerJobOffer(final ServerAgent serverAgent,
                                                final double servicePrice,
                                                final String jobId,
                                                final ACLMessage replyMessage) {
        final Job job = serverAgent.getJobById(jobId);
        final int inUsePower = serverAgent.getAvailableCapacity(job.getStartTime(), job.getEndTime());
        final ImmutableServerData jobOffer = ImmutableServerData.builder()
                .servicePrice(servicePrice)
                .availablePower(serverAgent.getMaximumCapacity() - inUsePower)
                .jobId(jobId)
                .build();
        replyMessage.setPerformative(ACLMessage.PROPOSE);
        try {
            replyMessage.setContent(getMapper().writeValueAsString(jobOffer));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return replyMessage;
    }
}
