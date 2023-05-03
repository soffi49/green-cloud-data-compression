package com.greencloud.application.agents.client.behaviour.df;

import static com.greencloud.application.agents.client.constants.ClientAgentConstants.SCHEDULER_AGENT;
import static com.greencloud.application.yellowpages.YellowPagesService.search;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.SCHEDULER_SERVICE_TYPE;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.quality.Strictness.LENIENT;

import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.greencloud.application.agents.client.ClientAgent;
import com.greencloud.application.yellowpages.YellowPagesService;
import com.gui.controller.GuiController;

import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class FindSchedulerAgentUnitTest {

	@Mock
	private static ClientAgent mockClientAgent;
	@Mock
	private static GuiController mockGuiController;

	private static MockedStatic<YellowPagesService> mockYellowPagesService;

	private FindSchedulerAgent testedBehaviour;
	private SequentialBehaviour parentBehaviour;

	@BeforeEach
	void setUp() {
		testedBehaviour = new FindSchedulerAgent(mockClientAgent);
		mockYellowPagesService = mockStatic(YellowPagesService.class);

		parentBehaviour = new SequentialBehaviour();
		parentBehaviour.addSubBehaviour(testedBehaviour);

		doReturn(mockGuiController).when(mockClientAgent).getGuiController();
	}

	@AfterEach
	void clear() {
		mockYellowPagesService.close();
	}

	@Test
	@DisplayName("Test find scheduler when scheduler is present")
	void testFindSchedulerWhenSchedulerIsPresent() {
		// given
		var scheduler = new AID("test", true);
		mockYellowPagesService.when(() -> search(eq(mockClientAgent), any(), eq(SCHEDULER_SERVICE_TYPE)))
				.thenReturn(Set.of(scheduler));

		// when
		testedBehaviour.action();

		// then
		verify(mockClientAgent).announce();
		assertThat(parentBehaviour.getDataStore().get(SCHEDULER_AGENT)).isEqualTo(scheduler);
	}

	@Test
	@DisplayName("Test find scheduler when scheduler not found")
	void testFindSchedulerWhenSchedulerNotFound() {
		// given
		mockYellowPagesService.when(() -> search(eq(mockClientAgent), any(), eq(SCHEDULER_SERVICE_TYPE)))
				.thenReturn(emptySet());

		// when
		testedBehaviour.action();

		// then
		verify(mockClientAgent).doDelete();
	}
}
