package demoapp.users;

import demoapp.users.data.Inbox;
import demoapp.users.data.Message;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

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
		StepVerifier
			.create(client.get().exchange())
			.assertNext( response -> {
				assertEquals(response.statusCode(), HttpStatus.OK);
				assertEquals(response.bodyToMono(Integer.class), 2);
			});
	}

	@Test
	@Order(3)
	void can_get_unread_count_stream() {
		WebClient client = WebClient.builder().baseUrl(getUrl("/2222/unread-sse?iter=1")).build();
		StepVerifier
				.create(client.get().exchange())
				.assertNext( response -> {
					assertEquals(response.statusCode(), HttpStatus.OK);
					String body = response.bodyToMono(String.class).block();
					assertTrue(body.contains("event:unread-count-event"));
					assertTrue(body.contains("data:1"));
				});
	}

	@Test
	@Order(4)
	void can_add_new_message() {
		WebClient client = WebClient.builder().baseUrl(getUrl("/33/messages")).build();
		StepVerifier
				.create(client.post()
						.contentType(MediaType.APPLICATION_JSON)
						.body(Mono.just("{\"message\":\"subject|body\"}"), String.class)
						.exchange())
				.assertNext( response -> {
					assertEquals(response.statusCode(), HttpStatus.ACCEPTED);
				});
	}

	private List<Inbox> testData() {
		Inbox inbox = new Inbox(null, "1111", 2,
				List.of(
						Message.create("subject 1|this is not how it works"),
						Message.create("subject 2|this is absolutely how it works")
						));
		Inbox inbox2 = new Inbox(UUID.randomUUID().toString(), "2222", 1,
				List.of(
						Message.create("on subject|are you hungry?")
				));
		return List.of(inbox, inbox2);
	}

	private String getUrl(String contextPath) {
		return "http://127.0.0.1:" + port + "/inbox" + contextPath;
	}
}
