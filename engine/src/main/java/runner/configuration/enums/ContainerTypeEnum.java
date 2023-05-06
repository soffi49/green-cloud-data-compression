package runner.configuration.enums;

/**
 * Enumerable describing available container types
 */
public enum ContainerTypeEnum {
	CLIENTS_CONTAINER_ID("Clients");

	private final String name;

	ContainerTypeEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
