package messages.domain;

import static common.constant.MessageProtocolConstants.*;
import static jade.lang.acl.ACLMessage.INFORM;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.util.List;

/**
 * Class storing methods used in creating messages informing that the job execution has finished
 */
public class JobStatusMessageFactory {

    /**
     * Method prepares the information message about the job execution finish which is to be sent
     * to the client
     *
     * @param jobId    unique identifier of the kob of interest
     * @param clientId client global name
     * @return inform ACLMessage
     */
    public static ACLMessage prepareFinishMessageForClient(final String jobId, final String clientId) {
        final ACLMessage informationMessage = new ACLMessage(INFORM);
        informationMessage.setProtocol(FINISH_JOB_PROTOCOL);
        informationMessage.setContent(String.format("The job %s is finished!", jobId));
        informationMessage.addReceiver(new AID(clientId, AID.ISGUID));
        return informationMessage;
    }

    /**
     * Method prepares the information message about the job execution finish which is to be sent
     * to list of receivers
     *
     * @param jobId     unique identifier of the kob of interest
     * @param receivers list of AID addresses of the message receivers
     * @return inform ACLMessage
     */
    public static ACLMessage prepareFinishMessage(final String jobId, final List<AID> receivers) {
        final ACLMessage informationMessage = new ACLMessage(INFORM);
        informationMessage.setProtocol(FINISH_JOB_PROTOCOL);
        informationMessage.setContent(jobId);
        receivers.forEach(informationMessage::addReceiver);
        return informationMessage;
    }

    /**
     * Method prepares the information message stating that the job execution has started
     *
     * @param jobId     unique identifier of the kob of interest
     * @param receivers list of AID addresses of the message receivers
     * @return inform ACLMessage
     */
    public static ACLMessage prepareJobStartedMessage(final String jobId, final List<AID> receivers) {
        final ACLMessage informationMessage = new ACLMessage(INFORM);
        informationMessage.setProtocol(STARTED_JOB_PROTOCOL);
        informationMessage.setContent(jobId);
        receivers.forEach(informationMessage::addReceiver);
        return informationMessage;
    }

    /**
     * Method prepares the information message about the job execution delay which is to be sent
     * to the client
     *
     * @param jobId    unique identifier of the kob of interest
     * @param clientId client global name
     * @return inform ACLMessage
     */
    public static ACLMessage prepareDelayMessageForClient(final String jobId, final String clientId) {
        final ACLMessage informationMessage = new ACLMessage(INFORM);
        informationMessage.setProtocol(DELAYED_JOB_PROTOCOL);
        informationMessage.setContent(String.format("The job %s execution has some delay.", jobId));
        informationMessage.addReceiver(new AID(clientId, AID.ISGUID));
        return informationMessage;
    }

    /**
     * Method prepares the information message about finishing the power delivery by hand by the Green Source.
     *
     * @param jobId         unique identifier of the kob of interest
     * @param serverAddress server address
     * @return inform ACLMessage
     */
    public static ACLMessage prepareManualFinishMessageForServer(final String jobId, final AID serverAddress) {
        final ACLMessage informationMessage = new ACLMessage(INFORM);
        informationMessage.setProtocol(MANUAL_JOB_FINISH_PROTOCOL);
        informationMessage.setContent(String.format("The job power delivery %s was finished manually.", jobId));
        informationMessage.addReceiver(serverAddress);
        return informationMessage;
    }
}
