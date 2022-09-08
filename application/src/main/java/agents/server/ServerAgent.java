package agents.server;

import static common.constant.DFServiceConstants.GS_SERVICE_TYPE;
import static common.constant.DFServiceConstants.SA_SERVICE_NAME;
import static common.constant.DFServiceConstants.SA_SERVICE_TYPE;
import static yellowpages.YellowPagesService.register;
import static yellowpages.YellowPagesService.search;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agents.server.behaviour.jobexecution.listener.ListenForJobStartCheckRequest;
import agents.server.behaviour.jobexecution.listener.ListenForNewJob;
import agents.server.behaviour.jobexecution.listener.ListenForPowerSupplyUpdate;
import agents.server.behaviour.powershortage.listener.ListenForSourceJobTransferRequest;
import agents.server.behaviour.powershortage.listener.ListenForSourcePowerShortageFinish;
import agents.server.behaviour.sensor.SenseServerEvent;
import agents.server.management.ServerStateManagement;
import common.behaviours.ReceiveGUIController;
import jade.core.AID;
import jade.core.behaviours.Behaviour;

/**
 * Agent representing the Server Agent which executes the clients' jobs
 */
public class ServerAgent extends AbstractServerAgent {

	private static final Logger logger = LoggerFactory.getLogger(ServerAgent.class);

	/**
	 * Method run at the agent's start. In initialize the Server Agent based on the given by the user arguments,
	 * registers it in the DF and then runs the starting behaviours - listening for the job requests
	 */
	@Override
	protected void setup() {
		super.setup();
		final Object[] args = getArguments();
		initializeAgent(args);
		register(this, SA_SERVICE_TYPE, SA_SERVICE_NAME, this.getOwnerCloudNetworkAgent().getName());
		addBehaviour(new ReceiveGUIController(this, behavioursRunAtStart()));
	}

	@Override
	protected void takeDown() {
		logger.info("I'm finished. Bye!");
		super.takeDown();
	}

	private void initializeAgent(final Object[] args) {
		if (Objects.nonNull(args) && args.length == 3) {
			this.stateManagement = new ServerStateManagement(this);
			this.ownedGreenSources = search(this, GS_SERVICE_TYPE, getName());
			if (ownedGreenSources.isEmpty()) {
				logger.info("I have no corresponding green sources!");
				doDelete();
			}
			this.ownerCloudNetworkAgent = new AID(args[0].toString(), AID.ISLOCALNAME);
			try {
				this.pricePerHour = Double.parseDouble(args[1].toString());
				this.currentMaximumCapacity = Integer.parseInt(args[2].toString());
				this.initialMaximumCapacity = Integer.parseInt(args[2].toString());
			} catch (final NumberFormatException e) {
				logger.info("The given price is not a number!");
				doDelete();
			}
		} else {
			logger.info(
					"Incorrect arguments: some parameters for server agent are missing - check the parameters in the documentation");
			doDelete();
		}
	}

	private List<Behaviour> behavioursRunAtStart() {
		return List.of(
				new ListenForNewJob(),
				new ListenForPowerSupplyUpdate(),
				new ListenForSourceJobTransferRequest(),
				new SenseServerEvent(this),
				new ListenForJobStartCheckRequest(),
				new ListenForSourcePowerShortageFinish()
		);
	}
}
