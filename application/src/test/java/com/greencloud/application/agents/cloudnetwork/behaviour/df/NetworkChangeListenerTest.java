package com.greencloud.application.agents.cloudnetwork.behaviour.df;

import static com.greencloud.application.agents.cloudnetwork.behaviour.df.listener.templates.DFCloudNetworkMessageTemplates.ANNOUNCE_NETWORK_CHANGE_TEMPLATE;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.SA_SERVICE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent;
import com.greencloud.application.agents.cloudnetwork.behaviour.df.listener.ListenForNetworkChange;
import com.greencloud.application.agents.cloudnetwork.management.CloudNetworkConfigManagement;
import com.greencloud.application.yellowpages.YellowPagesService;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

@ExtendWith(MockitoExtension.class)
class NetworkChangeListenerTest {

	@Mock
	ACLMessage message;
	@Mock
	CloudNetworkAgent cloudNetworkAgent;
	@Mock
	CloudNetworkConfigManagement cloudNetworkConfigManagement;
	@InjectMocks
	ListenForNetworkChange networkChangeListener;

	MockedStatic<YellowPagesService> yellowPagesService;

	@BeforeEach
	void init() {
		yellowPagesService = mockStatic(YellowPagesService.class);

		when(cloudNetworkAgent.receive(ANNOUNCE_NETWORK_CHANGE_TEMPLATE)).thenReturn(message);
		when(cloudNetworkAgent.manageConfig()).thenReturn(cloudNetworkConfigManagement);
	}

	@AfterEach
	void cleanUp() {
		yellowPagesService.close();
	}

	@Test
	void shouldCorrectlyHandleNewlyAddedServers() {
		// given
		var ownedServers = new HashMap<>(Map.of(aid("server1"), true, aid("server2"), true));
		var servers = Set.of(aid("server1"), aid("server2"), aid("server3"));
		when(cloudNetworkAgent.getOwnedServers()).thenReturn(ownedServers);
		yellowPagesService.when(() -> YellowPagesService.search(any(), eq(SA_SERVICE_TYPE), any())).
				thenReturn(servers);

		// when
		networkChangeListener.action();

		// then
		verify(cloudNetworkAgent, times(2)).getOwnedServers();
		verify(cloudNetworkConfigManagement).getWeightsForServersMap();
		assertThat(ownedServers.keySet())
				.hasSize(3)
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(servers);
	}

	@Test
	void shouldCorrectlyHandleRemovedServers() {
		// given
		var ownedServers = new HashMap<>(Map.of(aid("server1"), true, aid("server2"), true, aid("server3"), true));
		var servers = Set.of(aid("server1"));
		when(cloudNetworkAgent.getOwnedServers()).thenReturn(ownedServers);
		yellowPagesService.when(() -> YellowPagesService.search(any(), eq(SA_SERVICE_TYPE), any())).
				thenReturn(servers);

		// when
		networkChangeListener.action();

		// then
		verify(cloudNetworkAgent, times(2)).getOwnedServers();
		verify(cloudNetworkConfigManagement).getWeightsForServersMap();
		assertThat(ownedServers.keySet())
				.hasSize(1)
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrderElementsOf(servers);
	}

	private static AID aid(String serverName) {
		return new AID(serverName, AID.ISGUID);
	}
}
