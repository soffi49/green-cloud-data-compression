package agents.greenenergy;

import agents.greenenergy.behaviour.HandleMonitoringRequestResponse;
import agents.greenenergy.behaviour.HandleServerCallForProposal;
import common.GroupConstants;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Objects;

public class GreenEnergyAgent extends AbstractGreenEnergyAgent {

    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();
        this.setPricePerPowerUnit(Math.random() * 100);
        this.setAvailableCapacity(100);
        if(Objects.nonNull(args) && args.length == 1){
            monitoringAgent = new AID(args[0].toString(), AID.ISLOCALNAME);
        }
        registerInDF();
        addBehaviour(HandleServerCallForProposal.createFor(this));
        addBehaviour(HandleMonitoringRequestResponse.createFor(this));
    }

    private void registerInDF(){
        final DFAgentDescription template = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.GS_SERVICE_TYPE);
        serviceDescription.setName(this.getAID().getName());
        template.addServices(serviceDescription);
        try{
            DFService.register(this, template);
        }
        catch(FIPAException e){
            e.printStackTrace();
            doDelete();
        }
    }
}
