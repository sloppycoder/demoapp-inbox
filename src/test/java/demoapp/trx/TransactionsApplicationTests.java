package demoapp.trx;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionsApplicationTests {

	@Autowired
	RestTemplateBuilder builder;

	@LocalServerPort
	String port;

	@Test
	void contextLoads() {
	}

	@Test
	void can_get_all_transactions() {
		ResponseEntity<Transaction[]> response = builder.build()
				.getForEntity("http://127.0.0.1:" + port + "/transactions",
				Transaction[].class);
		List<Transaction> results = Arrays.asList(response.getBody());
		assertEquals(results.size(), 1);
	}

}
