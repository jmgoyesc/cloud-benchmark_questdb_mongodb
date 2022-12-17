package com.github.jmgoyesc.agent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("IT. Agent")
@SpringBootTest
class ApplicationIT {

	@DisplayName("GIVEN application context loaded WHEN run application THEN mongodb connection, questdb connection")
	@Test
	void contextLoads() {
	}

}
