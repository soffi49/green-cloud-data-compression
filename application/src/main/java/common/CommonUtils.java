package common;

import static jade.lang.acl.ACLMessage.PROPOSE;
import static jade.lang.acl.ACLMessage.REJECT_PROPOSAL;
import static mapper.JsonMapper.getMapper;

import domain.job.Job;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommonUtils {

    public static List<AID> getAgentsFromDF(final Agent agent, final DFAgentDescription template) {
        final List<AID> agentsDF = new ArrayList<>();
        try {
            final DFAgentDescription[] agents = DFService.search(agent, template);
            Arrays.stream(agents).forEach(agentCNA -> agentsDF.add(agentCNA.getName()));
        } catch (FIPAException e) {
            e.printStackTrace();
            agent.doDelete();
        }
        return agentsDF;
    }
}
