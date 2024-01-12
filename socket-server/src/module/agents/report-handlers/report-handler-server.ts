import { MessageExchangeData } from "../../../types";
import { AGENTS_REPORTS_STATE } from "../agents-state";
import { ServerAgent } from "../types/server-agent";

const calculateAverageFromExchangedMessage = (agent: ServerAgent, field: keyof MessageExchangeData) => {
	const dataLength = agent.exchangedMessagesData.length

	return dataLength === 0 ?
		0.0 :
		agent.exchangedMessagesData.reduce((prev, curr) => curr[field] + prev, 0) / dataLength
}

const reportServerData = (agent: ServerAgent, time) => {
	const reports = AGENTS_REPORTS_STATE.agentsReports.filter((agentReport) => agentReport.name === agent.name)[0]
		.reports;

	reports["trafficReport"].push({ time, value: agent.traffic + agent.backUpTraffic });
	reports["cpuInUseReport"].push({
		time,
		value: (agent.inUseResources["cpu"]?.characteristics["amount"]?.value as number) ?? 0,
	});
	reports["powerConsumptionReport"].push({ time, value: agent.powerConsumption });
	reports["backUpPowerConsumptionReport"].push({ time, value: agent.powerConsumptionBackUp });
	reports["successRatioReport"].push({ time, value: agent.successRatio ?? 0 });
	reports["avgTransmissionTime"].push({ time, value: calculateAverageFromExchangedMessage(agent, 'messageRetrievalDuration') ?? 0 });
	reports["avgBytesSentToBytesReceived"].push({ time, value: calculateAverageFromExchangedMessage(agent, 'bytesSentToBytesReceived') ?? 0 });
	reports["avgCompressionTime"].push({ time, value: calculateAverageFromExchangedMessage(agent, 'compressionTime') ?? 0 });
	reports["avgDecompressionTime"].push({ time, value: calculateAverageFromExchangedMessage(agent, 'decompressionTime') ?? 0 });
	reports["avgTransmissionCost"].push({ time, value: calculateAverageFromExchangedMessage(agent, 'estimatedTransferCost') ?? 0 });
};

export { reportServerData };
