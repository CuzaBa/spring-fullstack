package com.amigoscode.demo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class  TestContainerTest extends AbstractTestcontainers {

	@Test
	void canStartPostgresDB() {
		Assertions.assertThat(postgreSQLContainer.isRunning()).isTrue();
		Assertions.assertThat(postgreSQLContainer.isCreated()).isTrue();
	}
}
