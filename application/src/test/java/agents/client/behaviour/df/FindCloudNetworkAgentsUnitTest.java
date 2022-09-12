package agents.client.behaviour.df;

import static yellowpages.domain.DFServiceConstants.CNA_SERVICE_TYPE;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gui.controller.GUIController;

import agents.client.ClientAgent;
import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;
import yellowpages.YellowPagesService;

@ExtendWith(MockitoExtension.class)
class FindCloudNetworkAgentsUnitTest {

	@Mock
	private ClientAgent clientAgent;
	@Mock
	private GUIController guiController;
	private FindCloudNetworkAgents testedBehaviour;

	@BeforeEach
	void init() {
		testedBehaviour = new FindCloudNetworkAgents();
		SequentialBehaviour parentBehaviour = new SequentialBehaviour();
		parentBehaviour.addSubBehaviour(testedBehaviour);
	}

	@Test
	void shouldDeleteAgentWhenNoCNAgentsFound() {
		// given
		testedBehaviour.setAgent(clientAgent);
		when(clientAgent.getName()).thenReturn("testName");

		// when
		try (var yellowPagesService = mockStatic(YellowPagesService.class)) {
			yellowPagesService.when(() -> YellowPagesService.search(clientAgent, CNA_SERVICE_TYPE))
					.thenReturn(emptyList());

			testedBehaviour.onStart();
			testedBehaviour.action();
		}

		// then
		verify(clientAgent).doDelete();
	}

	@Test
	void shouldAnnounceWhenCNAgentsWereFound() {
		// given
		testedBehaviour.setAgent(clientAgent);
		when(clientAgent.getGuiController()).thenReturn(guiController);

		// when
		try (var yellowPagesService = mockStatic(YellowPagesService.class)) {
			yellowPagesService.when(() -> YellowPagesService.search(clientAgent, CNA_SERVICE_TYPE))
					.thenReturn(List.of(new AID("test", true)));

			testedBehaviour.onStart();
			testedBehaviour.action();
		}

		// then
		verify(clientAgent).announce();
	}
}
