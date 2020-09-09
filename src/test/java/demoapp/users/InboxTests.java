package demoapp.users;

import demoapp.users.data.Inbox;
import demoapp.users.data.Message;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InboxTests {

	@Autowired
	ReactiveMongoTemplate mongo;

	@LocalServerPort
	String port;

	@Test
	@Order(1)
	void populate_test_data() {
		mongo.insertAll(testData()).blockFirst();
	}

	@Test
	@Order(2)
	void can_get_unread_count() {
		WebClient client = WebClient.builder().baseUrl(getUrl("/1111/unread")).build();
		ClientResponse response = client.get().exchange().block();
		Integer count = response.bodyToMono(Integer.class).block();

		assertEquals(response.statusCode(), HttpStatus.OK);
		assertEquals(count, 2);
	}


	@Test
	@Order(3)
	void can_get_unread_count_stream() {
		WebClient client = WebClient.builder().baseUrl(getUrl("/2222/unread-sse?iter=1")).build();
		ClientResponse response = client.get().exchange().block();
		String body = response.bodyToMono(String.class).block();

		assertEquals(response.statusCode(), HttpStatus.OK);
		assertTrue(body.contains("event:unread-count-event"));
		assertTrue(body.contains("data:1"));
	}

	private List<Inbox> testData() {
		Inbox inbox = new Inbox(null, "1111", 2,
				List.of(
						newMessage("1|subject 1|this is not how it works"),
						newMessage("2|subject 2|this is absolutely how it works")
						));
		Inbox inbox2 = new Inbox(null, "2222", 1,
				List.of(
						newMessage("1|on subject|are you hungry?")
				));
		return List.of(inbox, inbox2);
	}

	private Message newMessage(String line) {
		String[] str = line.split("\\|");
		return new Message(str[0], str[1], str[2], false);
	}

	private String getUrl(String contextPath) {
		return "http://127.0.0.1:" + port + "/inbox" + contextPath;
	}
}
