package yellowpages;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;

public class YellowPagesService {

    private static final Logger logger = LoggerFactory.getLogger(YellowPagesService.class);

    private YellowPagesService() {
    }

    public static void register(Agent agent, String serviceType, String serviceName, String ownership) {
        try {
            DFService.register(agent, prepareAgentDescription(agent.getAID(), serviceType, serviceName, ownership));
        } catch (FIPAException e) {
            logger.info("Couldn't register {} in the directory facilitator", agent);
        }
    }

    public static void register(Agent agent, String serviceType, String serviceName) {
        try {
            DFService.register(agent, prepareAgentDescription(agent.getAID(), serviceType, serviceName));
        } catch (FIPAException e) {
            logger.info("Couldn't register {} in the directory facilitator", agent);
        }
    }

    public static List<AID> search(Agent agent, String serviceType, String ownership) {
        try {
            return Arrays.stream(DFService.search(agent, prepareAgentDescriptionTemplate(serviceType, ownership)))
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

    private static DFAgentDescription prepareAgentDescription(AID aid, String serviceType, String serviceName, String ownership) {
        var agentDescription = new DFAgentDescription();
        agentDescription.setName(aid);
        agentDescription.addServices(prepareDescription(serviceType, serviceName, ownership));
        return agentDescription;
    }

    private static DFAgentDescription prepareAgentDescription(AID aid, String serviceType, String serviceName) {
        var agentDescription = new DFAgentDescription();
        agentDescription.setName(aid);
        agentDescription.addServices(prepareDescription(serviceType, serviceName));
        return agentDescription;
    }

    private static ServiceDescription prepareDescription(String serviceType, String serviceName, String ownership) {
        var serviceDescription = new ServiceDescription();
        serviceDescription.setType(serviceType);
        serviceDescription.setName(serviceName);
        serviceDescription.setOwnership(ownership);
        return serviceDescription;
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

    private static ServiceDescription prepareDescriptionOwnership(String serviceType, String ownership) {
        var serviceDescription = new ServiceDescription();
        serviceDescription.setType(serviceType);
        serviceDescription.setOwnership(ownership);
        return serviceDescription;
    }

    private static DFAgentDescription prepareAgentDescriptionTemplate(String serviceType, String ownership) {
        var template = new DFAgentDescription();
        template.addServices(prepareDescriptionOwnership(serviceType, ownership));
        return template;
    }

    private static DFAgentDescription prepareAgentDescriptionTemplate(String serviceType) {
        var template = new DFAgentDescription();
        template.addServices(prepareDescription(serviceType));
        return template;
    }
}
