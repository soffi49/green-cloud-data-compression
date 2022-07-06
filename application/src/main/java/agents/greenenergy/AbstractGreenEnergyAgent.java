package agents.greenenergy;

import static domain.job.JobStatusEnum.IN_PROGRESS;

import agents.AbstractAgent;
import agents.greenenergy.domain.EnergyTypeEnum;
import agents.greenenergy.domain.GreenPower;
import domain.WeatherData;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import domain.location.Location;
import jade.core.AID;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Abstract agent class storing data of the Green Source Energy Agent
 */
public abstract class AbstractGreenEnergyAgent extends AbstractAgent {

    /**
     * greenPower        defines maximum power of the green source and holds algorithms to compute available power
     * location          geographical location (longitude and latitude) of the green source
     * pricePerPowerUnit price for the 1 power unit (1 kWh)
     * powerJobs         list of power orders together with their statuses
     * monitoringAgent   address of the corresponding monitoring agent
     * ownerServer       address of the server which owns the given green source
     * energyType        allows to differentiate between SOLAR and WIND energy sources
     */

    protected transient GreenPower greenPower;
    protected transient Location location;
    protected double pricePerPowerUnit;
    protected transient ConcurrentMap<PowerJob, JobStatusEnum> powerJobs;
    protected AID monitoringAgent;
    protected AID ownerServer;
    protected EnergyTypeEnum energyType;

    AbstractGreenEnergyAgent() {
        super.setup();
    }

    /**
     * Method calculates the power in use at the given moment for the green source
     *
     * @return current power in use
     */
    public int getCurrentPowerInUse() {
        return powerJobs.entrySet().stream()
                .filter(job -> job.getValue().equals(IN_PROGRESS))
                .mapToInt(job -> job.getKey().getPower()).sum();
    }

    /**
     * Method retrieves if the given green source is currently active or idle
     *
     * @return green source state
     */
    public boolean getIsActiveState() {
        return !powerJobs.entrySet().stream().filter(entry -> entry.getValue().equals(IN_PROGRESS)).toList().isEmpty();
    }

    /**
     * Method retrieves the number of jobs that are executed by the green source
     *
     * @return jobs count
     */
    public int getJobCount() {
        return powerJobs.keySet().stream().map(PowerJob::getJobId).collect(Collectors.toSet()).size();
    }

    /**
     * Method retrieves the job by the job id from job map
     *
     * @param jobId job identifier
     * @return job
     */
    public PowerJob getJobById(final String jobId) {
        return powerJobs.keySet().stream()
                .filter(job -> job.getJobId().equals(jobId)).findFirst().orElse(null);
    }

    /**
     * Method retrieves the job by the job id and star time from job map
     *
     * @param jobId     job identifier
     * @param startTime job start time
     * @return job
     */
    public PowerJob getJobByIdAndStartDate(final String jobId, final OffsetDateTime startTime) {
        return powerJobs.keySet().stream()
                .filter(job -> job.getJobId().equals(jobId) && job.getStartTime().isEqual(startTime)).findFirst().orElse(null);
    }

    /**
     * Method retrieves the job by the job id and star time from job map
     *
     * @param jobInstanceId unique identifier of the job instance
     * @return job
     */
    public PowerJob getJobByIdAndStartDate(final JobInstanceIdentifier jobInstanceId) {
        return powerJobs.keySet().stream()
                .filter(job -> job.getJobId().equals(jobInstanceId.getJobId()) && job.getStartTime().isEqual(jobInstanceId.getStartTime()))
                .findFirst().orElse(null);
    }

    public AID getOwnerServer() {
        return ownerServer;
    }

    public void setOwnerServer(AID ownerServer) {
        this.ownerServer = ownerServer;
    }

    public double getPricePerPowerUnit() {
        return pricePerPowerUnit;
    }

    public void setPricePerPowerUnit(double pricePerPowerUnit) {
        this.pricePerPowerUnit = pricePerPowerUnit;
    }

    public double getCapacity(WeatherData weather, ZonedDateTime startTime) {
        return greenPower.getAvailablePower(weather, startTime, location);
    }

    public void setMaximumCapacity(int maximumCapacity) {
        this.greenPower.setMaximumCapacity(maximumCapacity);
    }

    public int getMaximumCapacity() {
        return this.greenPower.getMaximumCapacity();
    }

    public Map<PowerJob, JobStatusEnum> getPowerJobs() {
        return powerJobs;
    }

    public void setPowerJobs(ConcurrentMap<PowerJob, JobStatusEnum> powerJobs) {
        this.powerJobs = powerJobs;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public AID getMonitoringAgent() {
        return monitoringAgent;
    }

    public void setMonitoringAgent(AID monitoringAgent) {
        this.monitoringAgent = monitoringAgent;
    }

    public EnergyTypeEnum getEnergyType() {
        return energyType;
    }
}
