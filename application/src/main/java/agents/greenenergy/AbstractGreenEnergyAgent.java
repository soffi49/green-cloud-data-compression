package agents.greenenergy;

import static domain.job.JobStatusEnum.ACCEPTED;
import static domain.job.JobStatusEnum.IN_PROGRESS;

import agents.greenenergy.domain.EnergyTypeEnum;
import agents.greenenergy.domain.GreenPower;
import domain.MonitoringData;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import domain.location.Location;
import jade.core.AID;
import jade.core.Agent;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract agent class storing data of the Green Source Energy Agent
 */
public abstract class AbstractGreenEnergyAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger(AbstractGreenEnergyAgent.class);

    /**
     * greenPower        defines maximum power of the green source and holds algorithms to compute available power
     * location          geographical location (longitude and latitude) of the green source
     * pricePerPowerUnit price for the 1 power unit (1 kWh)
     * powerJobs         list of power orders together with their statuses
     * monitoringAgent   address of the corresponding monitoring agent
     * ownerServer       address of the server which owns the given green source
     * energyType        allows to differentiate between SOLAR and WIND energy sources
     */

    protected GreenPower greenPower;
    protected Location location;
    protected double pricePerPowerUnit;
    protected Map<PowerJob, JobStatusEnum> powerJobs;
    protected AID monitoringAgent;
    protected AID ownerServer;
    protected EnergyTypeEnum energyType;

    public double getAvailablePower(final OffsetDateTime startTime, final OffsetDateTime endTime,
                                    final MonitoringData weather) {
        final int powerInUse = powerJobs.keySet().stream()
            .filter(job -> job.getStartTime().isBefore(endTime) && job.getEndTime().isAfter(startTime))
            .filter(job -> powerJobs.get(job).equals(ACCEPTED) || powerJobs.get(job).equals(IN_PROGRESS))
            .mapToInt(PowerJob::getPower).sum();
        double availablePower = getCapacity(weather, startTime.toZonedDateTime()) - powerInUse;
        logger.info("[{}] Calculated available {} power {} at {} for {}", ((Agent) this).getName(), energyType,
            String.format("%.2f", availablePower), startTime, weather);
        return availablePower;
    }

    public PowerJob getJobById(final String jobId) {
        return powerJobs.keySet().stream().filter(job -> job.getJobId().equals(jobId)).findFirst().orElse(null);
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

    public double getCapacity(MonitoringData weather, ZonedDateTime startTime) {
        return greenPower.getAvailablePower(weather, startTime, location);
    }

    public void setMaximumCapacity(int maximumCapacity) {
        this.greenPower.setMaximumCapacity(maximumCapacity);
    }

    public Map<PowerJob, JobStatusEnum> getPowerJobs() {
        return powerJobs;
    }

    public void setPowerJobs(Map<PowerJob, JobStatusEnum> powerJobs) {
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
