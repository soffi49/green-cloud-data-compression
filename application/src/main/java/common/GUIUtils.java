package common;

import agents.AbstractAgent;

/**
 * Class defines set of utilities used together with GUI Controller
 */
public class GUIUtils {

    /**
     * Method updates the GUI to indicate that the new job has started
     *
     * @param agent agent updating the GUI
     * @param jobId unique identifier of the GUI
     */
    public static void announceStartedJob(final AbstractAgent agent, final String jobId) {
        final String information = String.format("Execution of the job %s has started!", jobId);
        agent.getGuiController().updateActiveJobsCountByValue(1);
        agent.getGuiController().addNewInformation(information);
    }

    /**
     * Method updates the GUI to indicate that the job execution has finished
     *
     * @param agent agent updating the GUI
     * @param jobId unique identifier of the GUI
     */
    public static void announceFinishedJob(final AbstractAgent agent, final String jobId) {
        final String information = String.format("Execution of the job %s has finished!", jobId);
        agent.getGuiController().updateActiveJobsCountByValue(-1);
        agent.getGuiController().updateAllJobsCountByValue(-1);
        agent.getGuiController().addNewInformation(information);
    }

    /**
     * Method updates the GUI to indicate that a new job is planned to be executed
     *
     * @param agent agent updating the GUI
     * @param jobId unique identifier of the GUI
     */
    public static void announceBookedJob(final AbstractAgent agent, final String jobId) {
        final String information = String.format("Job %s is planned to be executed!", jobId);
        agent.getGuiController().updateAllJobsCountByValue(1);
        agent.getGuiController().addNewInformation(information);
    }

    /**
     * Method updates the GUI to indicate that new client is using Cloud Network
     *
     * @param agent agent updating the GUI
     */
    public static void announceNewClient(final AbstractAgent agent) {
        final String information = "New client in Cloud Network!";
        agent.getGuiController().updateClientsCountByValue(1);
        agent.getGuiController().addNewInformation(information);
    }
}
