package com.greencloud.application.agents.cloudnetwork.management;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.utils.TimeUtils;
import com.greencloud.commons.domain.job.ClientJob;
import com.greencloud.commons.domain.job.ImmutableClientJob;
import com.greencloud.commons.domain.job.enums.JobExecutionResultEnum;
import com.greencloud.commons.domain.job.enums.JobExecutionStatusEnum;
import com.gui.controller.GuiController;

import jade.core.AID;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class CloudNetworkStateManagementUnitTest {

	// MOCK OBJECTS

	@Mock
	private CloudNetworkAgent mockCloudNetwork;
	@Mock
	private GuiController guiController;
	private CloudNetworkStateManagement cloudNetworkStateManagement;

	// TEST SET-UP

	@BeforeAll
	static void setUpAll() {
	}

	@BeforeEach
	void init() {
		Map<ClientJob, JobExecutionStatusEnum> MOCK_JOBS = setUpCloudNetworkJobs();
		var mockAID = mock(AID.class);
		var mockConfig = mock(CloudNetworkConfigManagement.class);

		when(mockAID.getName()).thenReturn("test_name");
		when(mockCloudNetwork.getAID()).thenReturn(mockAID);
		when(mockCloudNetwork.getGuiController()).thenReturn(guiController);
		when(mockCloudNetwork.manageConfig()).thenReturn(mockConfig);
		cloudNetworkStateManagement = new CloudNetworkStateManagement(mockCloudNetwork);

		doReturn(MOCK_JOBS).when(mockCloudNetwork).getNetworkJobs();
	}

	// TESTS

	@Test
	@DisplayName("Test get current power in use")
	void testGetCurrentPowerInUser() {
		TimeUtils.useMockTime(Instant.parse("2022-01-01T09:00:00.000Z"), ZoneId.of("UTC"));
		assertThat(cloudNetworkStateManagement.getCurrentPowerInUse()).isEqualTo(30);
	}

	@Test
	@DisplayName("Test increment job counter")
	void testIncrementJobCounter() {
		cloudNetworkStateManagement.incrementJobCounter("1", JobExecutionResultEnum.ACCEPTED);
		cloudNetworkStateManagement.incrementJobCounter("1", JobExecutionResultEnum.FAILED);
		cloudNetworkStateManagement.incrementJobCounter("1", JobExecutionResultEnum.FAILED);
		assertThat(cloudNetworkStateManagement.getJobCounters()).containsEntry(JobExecutionResultEnum.FAILED, 2L);
		assertThat(cloudNetworkStateManagement.getJobCounters()).containsEntry(JobExecutionResultEnum.ACCEPTED, 1L);
		assertThat(cloudNetworkStateManagement.getJobCounters().get(JobExecutionResultEnum.FINISH)).isZero();
		assertThat(cloudNetworkStateManagement.getJobCounters().get(JobExecutionResultEnum.STARTED)).isZero();
	}

	// PREPARING TEST DATA

	/**
	 * Class creates mock cloud network jobs used in test scenarios.
	 * The following structure was used:
	 *
	 * Job1 -> power: 10, time: 08:00 - 10:00, status: IN_PROGRESS,
	 * Job2 -> power: 20, time: 07:00 - 11:00, status: IN_PROGRESS
	 * Job3 -> power: 50,  time: 06:00 - 15:00, status: ACCEPTED
	 */
	private Map<ClientJob, JobExecutionStatusEnum> setUpCloudNetworkJobs() {
		final ClientJob mockJob1 = ImmutableClientJob.builder()
				.jobId("1")
				.clientIdentifier("Client1")
				.startTime(Instant.parse("2022-01-01T08:00:00.000Z"))
				.endTime(Instant.parse("2022-01-01T10:00:00.000Z"))
				.deadline(Instant.parse("2022-01-01T20:00:00.000Z"))
				.power(10)
				.build();
		final ClientJob mockJob2 = ImmutableClientJob.builder()
				.jobId("2")
				.clientIdentifier("Client2")
				.startTime(Instant.parse("2022-01-01T07:00:00.000Z"))
				.endTime(Instant.parse("2022-01-01T11:00:00.000Z"))
				.deadline(Instant.parse("2022-01-01T20:00:00.000Z"))
				.power(20)
				.build();
		final ClientJob mockJob3 = ImmutableClientJob.builder()
				.jobId("3")
				.clientIdentifier("Client3")
				.startTime(Instant.parse("2022-01-01T06:00:00.000Z"))
				.endTime(Instant.parse("2022-01-01T15:00:00.000Z"))
				.deadline(Instant.parse("2022-01-01T20:00:00.000Z"))
				.power(50)
				.build();
		final Map<ClientJob, JobExecutionStatusEnum> mockJobMap = new HashMap<>();
		mockJobMap.put(mockJob1, JobExecutionStatusEnum.IN_PROGRESS);
		mockJobMap.put(mockJob2, JobExecutionStatusEnum.IN_PROGRESS);
		mockJobMap.put(mockJob3, JobExecutionStatusEnum.ACCEPTED);
		return mockJobMap;
	}
}
