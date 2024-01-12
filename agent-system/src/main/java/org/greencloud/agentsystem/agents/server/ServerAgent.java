package org.greencloud.agentsystem.agents.server;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static org.greencloud.commons.constants.DFServiceConstants.SA_SERVICE_NAME;
import static org.greencloud.commons.constants.DFServiceConstants.SA_SERVICE_TYPE;
import static org.greencloud.commons.utils.yellowpages.YellowPagesRegister.deregister;
import static org.greencloud.commons.utils.yellowpages.YellowPagesRegister.register;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;

import org.greencloud.commons.args.agent.server.agent.ServerAgentProps;
import org.greencloud.commons.domain.resources.Resource;
import org.greencloud.enums.CompressionMethodEnum;
import org.slf4j.Logger;

import jade.core.AID;

/**
 * Agent representing the Server which executes the clients' jobs
 */
public class ServerAgent extends AbstractServerAgent {

	private static final Logger logger = getLogger(ServerAgent.class);

	@Override
	protected void initializeAgent(final Object[] args) {
		if (args.length >= 10) {
			final AID ownerRegionalManagerAgentAgent = new AID(args[0].toString(), AID.ISLOCALNAME);

			try {
				final double pricePerHour = parseDouble(args[1].toString());
				final int maxPowerConsumption = parseInt(args[2].toString());
				final int idlePowerConsumption = parseInt(args[3].toString());
				final int jobProcessingLimit = parseInt(args[4].toString());
				final Map<String, Resource> resources = (Map<String, Resource>) (args[5]);
				final CompressionMethodEnum compressionMethod = (CompressionMethodEnum) (args[6]);
				this.properties = new ServerAgentProps(getName(), ownerRegionalManagerAgentAgent, resources,
						maxPowerConsumption, idlePowerConsumption, pricePerHour, jobProcessingLimit, compressionMethod);

				// Additional argument indicates if the ServerAgent is going to be moved to another container
				// In such case, its service should be registered after moving
				if (args.length != 10 || !parseBoolean(args[7].toString())) {
					register(this, getDefaultDF(), SA_SERVICE_TYPE, SA_SERVICE_NAME,
							properties.getOwnerRegionalManagerAgent().getName());
				}

			} catch (final NumberFormatException e) {
				logger.info("Some of the arguments are not a number!");
				doDelete();
			}
		} else {
			logger.info("Incorrect arguments: some parameters for server agent are missing");
			doDelete();
		}
	}

	@Override
	protected void takeDown() {
		deregister(this, getDefaultDF(), SA_SERVICE_TYPE, SA_SERVICE_NAME,
				properties.getOwnerRegionalManagerAgent().getName());
		super.takeDown();
	}

	@Override
	protected void afterMove() {
		super.afterMove();
		register(this, getDefaultDF(), SA_SERVICE_TYPE, SA_SERVICE_NAME,
				properties.getOwnerRegionalManagerAgent().getName());

		// restoring default values
		properties.setPricePerHour(20);
		properties.setJobProcessingLimit(20);
	}
}
