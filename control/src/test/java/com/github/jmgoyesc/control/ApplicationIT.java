package com.github.jmgoyesc.control;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("IT. Control")
@SpringBootTest
@Disabled
class ApplicationIT {

	@DisplayName("GIVEN application context loaded WHEN run application THEN mongodb connection, questdb connection")
	@Test
	void contextLoads() {
	}

}
