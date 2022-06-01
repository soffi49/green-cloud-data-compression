package agents.cloudnetwork;

import static common.GroupConstants.CNA_SERVICE_TYPE;
import static yellowpages.YellowPagesService.register;

import agents.cloudnetwork.behaviour.HandleClientAcceptJobProposal;
import agents.cloudnetwork.behaviour.HandleClientJobCallForProposal;
import agents.cloudnetwork.behaviour.HandleClientRejectJobProposal;
import agents.cloudnetwork.behaviour.HandleServerCNAInformJobDone;
import agents.cloudnetwork.behaviour.HandleServerCallForProposalResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class CloudNetworkAgent extends AbstractCloudNetworkAgent {

    @Override
    protected void setup() {
        super.setup();
        this.serverForJobMap = new HashMap<>();
        this.currentJobs = new ArrayList<>();
        this.futureJobs = new ArrayList<>();
        register(this, CNA_SERVICE_TYPE, getName());

        addBehaviour(HandleClientJobCallForProposal.createFor(this));
        addBehaviour(HandleClientAcceptJobProposal.createFor(this));
        addBehaviour(HandleClientRejectJobProposal.createFor(this));
        addBehaviour(HandleServerCallForProposalResponse.createFor(this));
        addBehaviour(HandleServerCNAInformJobDone.createFor(this));
    }
}
