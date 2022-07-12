package messages.domain;

import static common.constant.MessageProtocolConstants.SERVER_JOB_START_CHECK_PROTOCOL;
import static jade.lang.acl.ACLMessage.REQUEST;
import static mapper.JsonMapper.getMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import domain.job.CheckedPowerJob;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class PowerCheckMessageFactory {

    /**
     * Method prepares the message to initiate the power checking processes before the job execution
     *
     * @param job           job for which available power have to be verified before the actual job execution
     * @param greenSourceId green source global name originally assigned to provide power for the given job
     * @param serverId      server which is requesting the additional power check
     * @return {@link ACLMessage} with REQUEST performative
     */
    public static ACLMessage preparePowerCheckMessage(final CheckedPowerJob job, final String greenSourceId,
            String serverId) {
        final ACLMessage request = new ACLMessage(REQUEST);
        request.setProtocol(SERVER_JOB_START_CHECK_PROTOCOL);
        request.setConversationId(greenSourceId + serverId);
        try {
            request.setContent(getMapper().writeValueAsString(job));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        request.addReceiver(new AID(greenSourceId, AID.ISGUID));
        return request;
    }
}