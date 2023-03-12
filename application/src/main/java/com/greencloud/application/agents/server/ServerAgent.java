package com.greencloud.application.agents.server;

import static com.greencloud.application.yellowpages.YellowPagesService.register;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.SA_SERVICE_NAME;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.SA_SERVICE_TYPE;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.application.agents.server.behaviour.df.SubscribeGreenSourceService;
import com.greencloud.application.agents.server.behaviour.df.listener.ListenForCloudNetworkInformationRequest;
import com.greencloud.application.agents.server.behaviour.df.listener.ListenForGreenSourceServiceUpdate;
import com.greencloud.application.agents.server.behaviour.jobexecution.listener.ListenForJobStartCheckRequest;
import com.greencloud.application.agents.server.behaviour.jobexecution.listener.ListenForNewJob;
import com.greencloud.application.agents.server.behaviour.jobexecution.listener.ListenForPowerSupplyUpdate;
import com.greencloud.application.agents.server.behaviour.jobexecution.listener.ListenForServerJobCancellation;
import com.greencloud.application.agents.server.behaviour.powershortage.handler.HandleSourcePowerShortageJobs;
import com.greencloud.application.agents.server.behaviour.powershortage.listener.ListenForSourceJobTransferRequest;
import com.greencloud.application.agents.server.behaviour.powershortage.listener.ListenForSourcePowerShortageFinish;
import com.greencloud.application.agents.server.behaviour.sensor.SenseServerEvent;
import com.greencloud.application.agents.server.management.ServerAdaptationManagement;
import com.greencloud.application.agents.server.management.ServerConfigManagement;
import com.greencloud.application.agents.server.management.ServerStateManagement;
import com.greencloud.application.behaviours.ListenForAdaptationAction;

import jade.core.AID;
import jade.core.behaviours.Behaviour;

/**
 * Agent representing the Server which executes the clients' jobs
 */
public class ServerAgent extends AbstractServerAgent {

	private static final Logger logger = LoggerFactory.getLogger(ServerAgent.class);

	@Override
	protected void initializeAgent(final Object[] args) {
		if (Objects.nonNull(args) && args.length == 4) {
			this.stateManagement = new ServerStateManagement(this);
			this.configManagement = new ServerConfigManagement(this);
			this.adaptationManagement = new ServerAdaptationManagement(this);
			this.ownerCloudNetworkAgent = new AID(args[0].toString(), AID.ISLOCALNAME);

			try {
				this.manageConfig().setPricePerHour(parseDouble(args[1].toString()));
				this.currentMaximumCapacity = parseInt(args[2].toString());
				this.initialMaximumCapacity = parseInt(args[2].toString());
				this.manageConfig().setJobProcessingLimit(parseInt(args[3].toString()));
			} catch (final NumberFormatException e) {
				logger.info("Some of the arguments are not a number!");
				doDelete();
			}

			register(this, SA_SERVICE_TYPE, SA_SERVICE_NAME, this.getOwnerCloudNetworkAgent().getName());
		} else {
			logger.info("Incorrect arguments: some parameters for server agent are missing");
			doDelete();
		}
	}

	@Override
	protected List<Behaviour> prepareStartingBehaviours() {
		return List.of(
				SubscribeGreenSourceService.create(this),
				new ListenForNewJob(),
				new ListenForPowerSupplyUpdate(),
				new ListenForSourceJobTransferRequest(),
				new SenseServerEvent(this),
				new ListenForJobStartCheckRequest(),
				new ListenForSourcePowerShortageFinish(),
				new HandleSourcePowerShortageJobs(this),
				new ListenForServerJobCancellation(),
				new ListenForCloudNetworkInformationRequest(this),
				new ListenForGreenSourceServiceUpdate(this),
				new ListenForAdaptationAction(this)
		);
	}

	@Override
	protected void afterMove() {
		super.afterMove();
		this.stateManagement = new ServerStateManagement(this);
		this.configManagement = new ServerConfigManagement(this);
		this.adaptationManagement = new ServerAdaptationManagement(this);
		// restoring default values
		configManagement.setJobProcessingLimit(20);
		configManagement.setPricePerHour(20);
	}
}
