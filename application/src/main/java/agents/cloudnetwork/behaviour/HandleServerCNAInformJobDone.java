package agents.cloudnetwork.behaviour;

import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.Job;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static jade.lang.acl.ACLMessage.INFORM;
import static mapper.JsonMapper.getMapper;

public class HandleServerCNAInformJobDone extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(HandleServerCNAInformJobDone.class);
    private static final MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(INFORM);

    private CloudNetworkAgent myCloudAgent;

    private HandleServerCNAInformJobDone(final CloudNetworkAgent cloudNetworkAgent) {
        super(cloudNetworkAgent);
    }

    public static HandleServerCNAInformJobDone createFor(final CloudNetworkAgent cloudNetworkAgent) {
        return new HandleServerCNAInformJobDone(cloudNetworkAgent);
    }

    @Override
    public void onStart() {
        super.onStart();
        myCloudAgent = (CloudNetworkAgent) myAgent;
    }

    @Override
    public void action() {
        final ACLMessage message = myAgent.receive(messageTemplate);

        if (Objects.nonNull(message)) {
            myCloudAgent.setJobsCount(myCloudAgent.getJobsCount() + 1);
            try {
                final Job job = getMapper().readValue(message.getContent(), Job.class);
                logger.info("[{}] Sending information to {} that the job is finished", myAgent, job.getClientIdentifier());
                myCloudAgent.getCurrentJobs().remove(job);
                myCloudAgent.getServerForJobMap().remove(job);
                myCloudAgent.setInUsePower(myCloudAgent.getInUsePower() - job.getPower());

                final ACLMessage informationMessage = new ACLMessage(INFORM);
                informationMessage.setContent(String.format("The job %s is finished!", job.getJobId()));
                informationMessage.setConversationId("FINISHED");
                informationMessage.addReceiver(new AID(job.getClientIdentifier(), AID.ISGUID));
                myAgent.send(informationMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
