package agents.greenenergy;

import jade.core.AID;
import jade.core.Agent;

import java.util.Objects;

public class GreenEnergyAgent extends AbstractGreenEnergyAgent {

    private AID monitoringAgent;
    @Override
    protected void setup() {
        super.setup();
        final Object[] args = getArguments();
        if(Objects.nonNull(args) && args.length == 1){
            monitoringAgent = new AID(args[0].toString(), AID.ISLOCALNAME);
        }
    }
}
