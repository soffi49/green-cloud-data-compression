package agents;

import common.GroupConstants;
import domain.Job;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jade.lang.acl.ACLMessage.REQUEST;

public class CloudNetworkAgent extends Agent {

    private List<Job> currentJobs;
    private List<Job> futureJobs;
    private int availablePower;
    private int inUsePower;

    @Override
    protected void setup() {
        super.setup();

        final Object[] args = getArguments();

        currentJobs = new ArrayList<>();
        futureJobs = new ArrayList<>();
        inUsePower = 0;

        try {
            availablePower = (args.length == 1) ? Integer.parseInt(args[0].toString()) : 0;
        } catch (NumberFormatException e) {
            System.out.printf("The given available power is not a number!");
            doDelete();
        }
        registerCNAInDF();

        final Behaviour getMessages = new CyclicBehaviour() {
            @Override
            public void action() {
                final ACLMessage message = receive();

                if (Objects.nonNull(message)) {

                    switch (message.getPerformative()) {
                        case REQUEST:
                            try {
                                if (inUsePower + ((Job) message.getContentObject()).getPower() > availablePower) {
                                    final ACLMessage respond = new ACLMessage(ACLMessage.REFUSE);
                                    respond.setContent("refuse");
                                    respond.addReceiver(message.getSender());
                                    send(respond);
                                } else {
                                    final ACLMessage respond = new ACLMessage(ACLMessage.AGREE);
                                    respond.setContent("agree");
                                    respond.addReceiver(message.getSender());
                                    send(respond);
                                }
                                System.out.println("Message got!");
                            } catch (UnreadableException e) {
                                e.printStackTrace();
                            }
                    }
                } else {
                    block();
                }
            }
        };

        addBehaviour(getMessages);
    }

    private void registerCNAInDF() {

        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());

        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.CNA_SERVICE_TYPE);
        serviceDescription.setName(getName());

        dfAgentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, dfAgentDescription);

        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
