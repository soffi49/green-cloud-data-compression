package com.greencloud.application.agents.greenenergy;

import static com.greencloud.application.yellowpages.YellowPagesService.register;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.GS_SERVICE_NAME;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.GS_SERVICE_TYPE;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.application.agents.greenenergy.behaviour.cancellation.ListenForGreenEnergyJobCancellation;
import com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.ListenForServerPowerInformation;
import com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.ListenForPowerSupplyRequest;
import com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.ListenForPowerSupplyStatus;
import com.greencloud.application.agents.greenenergy.behaviour.sensor.SenseGreenSourceEvent;
import com.greencloud.application.agents.greenenergy.behaviour.weathercheck.monitor.ReportWeatherShortages;
import com.greencloud.application.agents.greenenergy.behaviour.weathercheck.request.RequestWeatherPeriodically;
import com.greencloud.application.agents.greenenergy.management.GreenEnergyAdaptationManagement;
import com.greencloud.application.agents.greenenergy.management.GreenEnergyStateManagement;
import com.greencloud.application.agents.greenenergy.management.GreenPowerManagement;
import com.greencloud.commons.agent.greenenergy.GreenEnergySourceTypeEnum;
import com.greencloud.commons.domain.location.ImmutableLocation;

import jade.core.AID;
import jade.core.behaviours.Behaviour;

/**
 * Agent representing the Green Energy Source that produces the power for the Servers
 */
public class GreenEnergyAgent extends AbstractGreenEnergyAgent {

	private static final Logger logger = LoggerFactory.getLogger(GreenEnergyAgent.class);

	private int scenarioMaximumCapacity;

	@Override
	protected void initializeAgent(final Object[] args) {
		if (Objects.nonNull(args) && args.length == 8) {
			final AID ownerServer = new AID(args[1].toString(), AID.ISLOCALNAME);
			register(this, GS_SERVICE_TYPE, GS_SERVICE_NAME, ownerServer.getName());

			this.stateManagement = new GreenEnergyStateManagement(this);
			this.adaptationManagement = new GreenEnergyAdaptationManagement(this);
			this.monitoringAgent = new AID(args[0].toString(), AID.ISLOCALNAME);
			try {
				final double latitude = parseDouble(args[4].toString());
				final double longitude = parseDouble(args[5].toString());

				this.scenarioMaximumCapacity = parseInt(args[2].toString());
				this.greenPowerManagement = new GreenPowerManagement(scenarioMaximumCapacity, this);
				this.pricePerPowerUnit = parseDouble(args[3].toString());
				this.location = ImmutableLocation.of(latitude, longitude);

				if (args[6] instanceof String energyType) {
					this.energyType = GreenEnergySourceTypeEnum.valueOf(energyType);
				} else {
					this.energyType = (GreenEnergySourceTypeEnum) args[6];
				}
				this.weatherPredictionError = parseDouble(args[7].toString());

			} catch (NumberFormatException e) {
				logger.info("Couldn't parse one of the numerical arguments");
				doDelete();
			}
		} else {
			logger.info("Incorrect arguments: some parameters for Green Source Agent are missing.");
			doDelete();
		}
	}

	@Override
	protected List<Behaviour> prepareStartingBehaviours() {
		return List.of(
				new ListenForPowerSupplyRequest(this),
				new ListenForPowerSupplyStatus(this),
				new SenseGreenSourceEvent(this),
				new ListenForServerPowerInformation(this),
				new RequestWeatherPeriodically(this),
				new ListenForGreenEnergyJobCancellation(),
				new ReportWeatherShortages(this)
		);
	}

	@Override
	protected void afterMove() {
		super.afterMove();
		this.greenPowerManagement = new GreenPowerManagement(scenarioMaximumCapacity, this);
		this.adaptationManagement = new GreenEnergyAdaptationManagement(this);
		this.stateManagement = new GreenEnergyStateManagement(this);
	}
}
