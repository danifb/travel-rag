package com.danifb.travel_rag;

import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@org.springframework.boot.autoconfigure.EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
class TravelRagApplicationTests {

	@Test
	void contextLoads() {
	}

}
