package agents;

import common.GroupConstants;
import common.TimeUtils;
import domain.Job;
import exception.IncorrectTaskDateException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ClientAgent extends Agent {

    private Job jobToBeExecuted;

    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();

        if (Objects.nonNull(args) && args.length == 3) {
            jobToBeExecuted = createAgentJob(args);

            final Behaviour sendJobRequest = new OneShotBehaviour() {
                @Override
                public void action() {
                    final List<AID> agentsCNA = getCNAAgentList(myAgent);

                    agentsCNA.forEach(agent -> {
                        try {
                            final ACLMessage jobRequest = new ACLMessage(ACLMessage.REQUEST);
                            jobRequest.addReceiver(agent);
                            jobRequest.setContentObject(jobToBeExecuted);
                            send(jobRequest);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            };
            addBehaviour(sendJobRequest);

        } else {
            System.out.print("I have no task to be executed");
            doDelete();
        }
    }

    @Override
    protected void takeDown() {
        System.out.print("I'm finished. Bye!");
        super.takeDown();
    }

    private Job createAgentJob(final Object[] arguments) {
        try {
            final OffsetDateTime startTime = TimeUtils.convertToOffsetDateTime(arguments[0].toString());
            final OffsetDateTime endTime = TimeUtils.convertToOffsetDateTime(arguments[1].toString());
            final int power = Integer.parseInt(arguments[2].toString());

            return new Job(getAID(), startTime, endTime, power);
        } catch (IncorrectTaskDateException e) {
            System.out.printf(e.getMessage());
            doDelete();
        } catch (NumberFormatException e) {
            System.out.printf("The given power is not a number!");
            doDelete();
        }

        return null;
    }

    private List<AID> getCNAAgentList(final Agent agent) {

        final DFAgentDescription template = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        final List<AID> agentsCNA = new ArrayList<>();

        serviceDescription.setType(GroupConstants.CNA_SERVICE_TYPE);
        template.addServices(serviceDescription);

        try {
            final DFAgentDescription[] agents = DFService.search(agent, template);
            Arrays.stream(agents).forEach(agentCNA -> agentsCNA.add(agentCNA.getName()));
        } catch (FIPAException e) {
            e.printStackTrace();
            doDelete();
        }
        return agentsCNA;
    }
}
