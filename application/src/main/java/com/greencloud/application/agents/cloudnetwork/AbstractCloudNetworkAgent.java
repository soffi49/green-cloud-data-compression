package com.greencloud.application.agents.cloudnetwork;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.greencloud.application.agents.AbstractAgent;
import com.greencloud.application.agents.cloudnetwork.management.CloudNetworkConfigManagement;
import com.greencloud.application.agents.cloudnetwork.management.CloudNetworkStateManagement;
import com.greencloud.commons.agent.AgentType;
import com.greencloud.commons.domain.job.ClientJob;
import com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum;

import jade.core.AID;

/**
 * Abstract agent class storing the data regarding Cloud Network Agent
 */
public abstract class AbstractCloudNetworkAgent extends AbstractAgent {

	protected transient CloudNetworkStateManagement stateManagement;
	protected transient CloudNetworkConfigManagement configManagement;

	protected double maximumCapacity;
	protected Map<ClientJob, JobExecutionStatusEnum> networkJobs;
	protected Map<String, AID> serverForJobMap;
	protected AtomicLong completedJobs;
	protected Map<AID, Boolean> ownedServers;
	protected AID scheduler;

	AbstractCloudNetworkAgent() {
		super();
		agentType = AgentType.CNA;
	}

	/**
	 * Method run on agent start. It initializes the Cloud Network Agent data with default values
	 */
	@Override
	protected void setup() {
		super.setup();

		serverForJobMap = new HashMap<>();
		networkJobs = new HashMap<>();
		completedJobs = new AtomicLong(0L);
		ownedServers = new HashMap<>();
	}

	public Map<String, AID> getServerForJobMap() {
		return serverForJobMap;
	}

	public Map<ClientJob, JobExecutionStatusEnum> getNetworkJobs() {
		return networkJobs;
	}

	public Long completedJob() {
		return completedJobs.incrementAndGet();
	}

	public Map<AID, Boolean> getOwnedServers() {
		return ownedServers;
	}

	public void setOwnedServers(Collection<AID> ownedServers) {
		var serversToAdd = ownedServers.stream().collect(toMap(aid -> aid, aid -> true));

		this.ownedServers.clear();
		this.ownedServers.putAll(serversToAdd);
	}

	/**
	 * Method retrieves list of owned servers that are active
	 *
	 * @return list of server AIDs
	 */
	public List<AID> getOwnedActiveServers() {
		return ownedServers.entrySet().stream()
				.filter(Map.Entry::getValue)
				.map(Map.Entry::getKey)
				.toList();
	}

	public AID getScheduler() {
		return scheduler;
	}

	public void setScheduler(AID scheduler) {
		this.scheduler = scheduler;
	}

	public CloudNetworkStateManagement manage() {
		return stateManagement;
	}

	public CloudNetworkConfigManagement manageConfig() {
		return configManagement;
	}

	public double getMaximumCapacity() {
		return maximumCapacity;
	}

	public void setMaximumCapacity(double maximumCapacity) {
		this.maximumCapacity = maximumCapacity;
	}
}
