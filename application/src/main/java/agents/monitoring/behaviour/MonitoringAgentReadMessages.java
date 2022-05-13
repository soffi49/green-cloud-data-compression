package agents.monitoring.behaviour;

import agents.monitoring.MonitoringAgent;
import domain.ImmutableMonitoringData;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

import static jade.lang.acl.ACLMessage.REQUEST;
import static mapper.JsonMapper.getMapper;

public class MonitoringAgentReadMessages  extends CyclicBehaviour {
    private static final Logger logger = LoggerFactory.getLogger(MonitoringAgent.class);

    private MonitoringAgent monitoringAgent;

    public MonitoringAgentReadMessages(MonitoringAgent monitoringAgent){
        this.monitoringAgent = monitoringAgent;
    }

    public static MonitoringAgentReadMessages createFor(MonitoringAgent monitoringAgent){
        return new MonitoringAgentReadMessages(monitoringAgent);
    }

    @Override
    public void action(){
        final ACLMessage message = monitoringAgent.receive();

        if(Objects.nonNull(message)){
            switch (message.getPerformative()){
                case REQUEST:
                    final ACLMessage response = new ACLMessage(ACLMessage.INFORM);
                    try{
                        var data = monitoringAgent.getWeather();
                        response.setContent(getMapper().writeValueAsString(data));
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    response.addReceiver(message.getSender());
                    monitoringAgent.send(response);
            }
        }
        else{
            block();
        }
    }
}
