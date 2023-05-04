package runner.domain.enums;

/**
 * Enumerable describing available container types
 */
public enum ContainerTypeEnum {
	CLIENTS_CONTAINER_ID("Clients");

	private String name;

	ContainerTypeEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
