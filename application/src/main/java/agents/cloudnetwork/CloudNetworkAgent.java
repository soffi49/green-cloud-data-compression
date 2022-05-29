package agents.cloudnetwork;

import static common.constant.DFServiceConstants.CNA_SERVICE_NAME;
import static common.constant.DFServiceConstants.CNA_SERVICE_TYPE;
import static yellowpages.YellowPagesService.register;

import agents.cloudnetwork.behaviour.*;
import jade.core.behaviours.SequentialBehaviour;

import java.util.ArrayList;
import java.util.HashMap;

public class CloudNetworkAgent extends AbstractCloudNetworkAgent {

    @Override
    protected void setup() {
        super.setup();
        initializeAgent();
        addBehaviour(prepareStartingBehaviour());
        addBehaviour(new ReturnCompletedJob());
    }

    private void initializeAgent() {
        this.serverForJobMap = new HashMap<>();
        this.currentJobs = new ArrayList<>();
        this.futureJobs = new ArrayList<>();
        register(this, CNA_SERVICE_TYPE, CNA_SERVICE_NAME);
    }

    private SequentialBehaviour prepareStartingBehaviour() {
        var startingBehaviour = new SequentialBehaviour(this);
        startingBehaviour.addSubBehaviour(new FindServerAgents());
        startingBehaviour.addSubBehaviour(new ReceiveJobRequests());
        return startingBehaviour;
    }
}
