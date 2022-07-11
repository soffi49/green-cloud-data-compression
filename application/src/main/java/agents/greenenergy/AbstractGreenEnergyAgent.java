package agents.greenenergy;

import agents.AbstractAgent;
import agents.greenenergy.domain.EnergyTypeEnum;
import agents.greenenergy.domain.GreenEnergyStateManagement;
import agents.greenenergy.domain.GreenPower;
import domain.MonitoringData;
import domain.WeatherData;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import domain.location.Location;
import jade.core.AID;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Abstract agent class storing data of the Green Source Energy Agent
 */
public abstract class AbstractGreenEnergyAgent extends AbstractAgent {

    /**
     * greenPower        defines maximum power of the green source and holds algorithms to compute available power
     * stateManagement   defines the class holding set of utilities used to manage the state of the green source
     * location          geographical location (longitude and latitude) of the green source
     * pricePerPowerUnit price for the 1 power unit (1 kWh)
     * powerJobs         list of power orders together with their statuses
     * monitoringAgent   address of the corresponding monitoring agent
     * ownerServer       address of the server which owns the given green source
     * energyType        allows to differentiate between SOLAR and WIND energy sources
     */

    protected transient GreenPower greenPower;
    protected transient GreenEnergyStateManagement stateManagement;
    protected transient Location location;
    protected double pricePerPowerUnit;
    protected transient Map<PowerJob, JobStatusEnum> powerJobs;
    protected AID monitoringAgent;
    protected AID ownerServer;
    protected EnergyTypeEnum energyType;

    AbstractGreenEnergyAgent() {
        super.setup();
    }

    public AID getOwnerServer() {
        return ownerServer;
    }

    public double getPricePerPowerUnit() {
        return pricePerPowerUnit;
    }

    public void setPricePerPowerUnit(double pricePerPowerUnit) {
        this.pricePerPowerUnit = pricePerPowerUnit;
    }

    public Double getCapacity(WeatherData weather, ZonedDateTime startTime) {
        return greenPower.getAvailablePower(weather, startTime, location);
    }

    public Double getCapacity(MonitoringData weather, Instant startTime) {
        return greenPower.getAvailablePower(weather, startTime, location);
    }

    public int getMaximumCapacity() {
        return this.greenPower.getMaximumCapacity();
    }

    public void setMaximumCapacity(int maximumCapacity) {
        this.greenPower.setMaximumCapacity(maximumCapacity);
    }

    public Map<PowerJob, JobStatusEnum> getPowerJobs() {
        return powerJobs;
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

    public GreenEnergyStateManagement manage() {
        return stateManagement;
    }
}
