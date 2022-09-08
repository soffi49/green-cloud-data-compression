package jade.junit.jupiter;

import static jade.core.Runtime.instance;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import jade.core.AID;
import jade.core.AgentContainer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class JadeExtension implements TestInstancePostProcessor, BeforeEachCallback {

	private static final String JADE_FRAMEWORK = "JADE_FRAMEWORK";

	@Override
	public void postProcessTestInstance(Object o, ExtensionContext extensionContext)
			throws ExecutionException, InterruptedException {
		injectJadeFramework(extensionContext);

	}

	private void injectJadeFramework(ExtensionContext extensionContext)
			throws ExecutionException, InterruptedException {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		var runtime = instance();
		var profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, "Main-Container");
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		profile.setParameter(Profile.MAIN_PORT, "6996");
		//profile.setParameter("jade_domain_dfservice_searchtimeout", "100");
		var containerFuture = executorService.submit(() -> runtime.createMainContainer(profile));
		while(!containerFuture.isDone()) {
			Thread.sleep(1);
		}
		runRMAAgent(containerFuture.get());
		extensionContext.getStore(getNamespace()).put(JADE_FRAMEWORK, containerFuture.get());
	}

	@Override
	public void beforeEach(ExtensionContext extensionContext) throws Exception {
		var testInstance = extensionContext.getTestInstance().orElseThrow();
		var fields = extensionContext.getTestClass().orElseThrow().getDeclaredFields();
		var controller = (ContainerController) extensionContext.getStore(getNamespace()).get(JADE_FRAMEWORK);
		for (Field field : fields) {
			var annotations = field.getAnnotationsByType(JadeAgent.class);
			if (annotations.length > 1) {
				throw new IllegalStateException("There cannot be multiple @JadeAgent annotations");
			}
			if (annotations.length == 1) {
				var jadeAgent = annotations[0];
				var agentController = controller.createNewAgent(jadeAgent.name(), jadeAgent.type(),
						jadeAgent.arguments());
				agentController.start();
				agentController.activate();
				var containerField = agentController.getClass().getDeclaredField("myContainer");
				containerField.setAccessible(true);
				var container = (AgentContainer) containerField.get(agentController);
				var agent = container.acquireLocalAgent(new AID(agentController.getName(), true));
				field.setAccessible(true);
				field.set(testInstance, agent);
			}
		}
	}

	private Namespace getNamespace() {
		return Namespace.create(JadeExtension.class);
	}

	/**
	 * Method used to run Jade GUI
	 *
	 * @param container controller container
	 */
	private static void runRMAAgent(final ContainerController container) {
		try {
			final AgentController rma = container.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
}
