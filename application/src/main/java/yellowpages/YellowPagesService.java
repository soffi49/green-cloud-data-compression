package yellowpages;

import static java.util.Collections.emptyList;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YellowPagesService {

    private static final Logger logger = LoggerFactory.getLogger(YellowPagesService.class);

    private YellowPagesService() {}

    public static void register(Agent agent, String serviceType, String serviceName) {
        try {
            DFService.register(agent, prepareAgentDescription(agent.getAID(), serviceType, serviceName));
        } catch (FIPAException e) {
            logger.info("Couldn't register {} in the directory facilitator", agent);
        }
    }

    public static List<AID> search(Agent agent, String serviceType, String serviceName) {
        try {
            return Arrays.stream(DFService.search(agent, prepareAgentDescriptionTemplate(serviceType, serviceName)))
                .map(DFAgentDescription::getName).toList();
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        return emptyList();
    }

    public static List<AID> search(Agent agent, String serviceType) {
        try {
            return Arrays.stream(DFService.search(agent, prepareAgentDescriptionTemplate(serviceType)))
                .map(DFAgentDescription::getName).toList();
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        return emptyList();
    }

    private static DFAgentDescription prepareAgentDescription(AID aid, String serviceType, String serviceName) {
        var agentDescription = new DFAgentDescription();
        agentDescription.setName(aid);
        agentDescription.addServices(prepareDescription(serviceType, serviceName));
        return agentDescription;
    }

    private static ServiceDescription prepareDescription(String serviceType, String serviceName) {
        var serviceDescription = new ServiceDescription();
        serviceDescription.setType(serviceType);
        serviceDescription.setName(serviceName);
        return serviceDescription;
    }

    private static ServiceDescription prepareDescription(String serviceType) {
        var serviceDescription = new ServiceDescription();
        serviceDescription.setType(serviceType);
        return serviceDescription;
    }

    private static DFAgentDescription prepareAgentDescriptionTemplate(String serviceType, String serviceName) {
        var template = new DFAgentDescription();
        template.addServices(prepareDescription(serviceType, serviceName));
        return template;
    }

    private static DFAgentDescription prepareAgentDescriptionTemplate(String serviceType) {
        var template = new DFAgentDescription();
        template.addServices(prepareDescription(serviceType));
        return template;
    }
}
