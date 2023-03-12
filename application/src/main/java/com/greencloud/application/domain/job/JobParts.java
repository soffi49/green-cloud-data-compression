package com.greencloud.application.domain.job;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greencloud.commons.domain.ImmutableConfig;
import com.greencloud.commons.domain.job.ClientJob;

/**
 * Object stores the data containing all split parts of the given client job
 */
@JsonSerialize(as = ImmutableJobParts.class)
@JsonDeserialize(as = ImmutableJobParts.class)
@Value.Immutable
@ImmutableConfig
public interface JobParts {

	/**
	 * @return parts of the split job
	 */
	List<ClientJob> getJobParts();
}
