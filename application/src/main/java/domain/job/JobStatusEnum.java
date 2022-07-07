package domain.job;

import java.util.EnumSet;
import java.util.Set;

/**
 * Enum describing what is the current status of the job in the network
 */
public enum JobStatusEnum {
    PROCESSING, ACCEPTED, IN_PROGRESS, IN_PROGRESS_BACKUP_ENERGY, ON_HOLD;

    public static Set<JobStatusEnum> JOB_IN_PROGRESS = EnumSet.of(IN_PROGRESS, IN_PROGRESS_BACKUP_ENERGY, ON_HOLD);
}
