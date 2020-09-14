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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InboxTests {

	@Autowired
	ReactiveMongoTemplate mongo;

	@LocalServerPort
	String port;

	final WebClient client = WebClient.builder().build();

	@Test
	@Order(1)
	void populate_test_data() {
		mongo.insertAll(testData()).blockFirst();
	}

	@Test
	@Order(2)
	void can_get_unread_count() {

		StepVerifier.create(this.client.get()
				.uri(getUrl("/inbox/1111/unread"))
				.exchange())
			.assertNext(
				response -> {
				  assertEquals(response.statusCode(), HttpStatus.OK);
				})
			.expectComplete()
			.verify();
	}

	@Test
	@Order(3)
	void can_get_unread_count_stream() {
		Flux<Map> sse = this.client.get()
			.uri(getUrl("/inbox/2222/unread-stream?iter=1"))
			.accept(MediaType.TEXT_EVENT_STREAM)
			.exchange()
			.flatMapMany(response -> response.bodyToFlux(Map.class));

		// number of events received is always iter+1, why??
		StepVerifier
				.create(sse)
				.expectNext(Map.of("userId", "2222", "unread", "1"))
				.expectNext(Map.of("userId", "2222", "unread", "1"))
				.expectComplete()
				.verify();
	}

	@Test
	@Order(4)
	void can_add_new_message() {
		StepVerifier
				.create(client.post()
						.uri(getUrl("/inbox/1111/messages"))
						.contentType(MediaType.APPLICATION_JSON)
						.body(Mono.just("{\"message\":\"subject|body\"}"), String.class)
						.exchange())
				.assertNext( response -> assertEquals(HttpStatus.CREATED, response.statusCode()))
		.verifyComplete();
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
		return "http://127.0.0.1:" + port + contextPath;
	}
}
