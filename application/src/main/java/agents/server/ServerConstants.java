package agents.server;

import domain.job.JobStatusEnum;

import java.util.EnumSet;
import java.util.Set;

/**
 * Class storing the constants regarding the server agent
 * IN_PROGRESS_STATE - enum set describing which job status is considered as job in progress
 */
public class ServerConstants {

    public static final Set<JobStatusEnum> IN_PROGRESS_STATE = EnumSet.of(JobStatusEnum.IN_PROGRESS, JobStatusEnum.IN_PROGRESS_BACKUP_ENERGY);
}
