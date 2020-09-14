package demoapp.users;

import demoapp.users.data.Inbox;
import demoapp.users.data.InboxNotFoundException;
import demoapp.users.data.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@Slf4j
public class InboxController {

  final ReactiveMongoTemplate mongo;

  public InboxController(ReactiveMongoTemplate mongo) {
    this.mongo = mongo;
  }

  @GetMapping(value = "/inbox/{userId}/unread")
  public Mono<Integer> getInboxUnreadCount(@PathVariable("userId") String userId) {
    log.info("retrieving unread count for user {}", userId);

    return getInboxForUser(userId)
            .flatMap(
                inbox -> Mono.just(inbox.getUnreadCount())
            ).switchIfEmpty(
                Mono.error(new InboxNotFoundException())
            );
  }

  @GetMapping(value = "/inbox/{userId}/unread-stream")
  public Flux<ServerSentEvent<Map<String, String>>> getInboxUnreadCountStream(
          @PathVariable("userId") String userId,
          @RequestParam(defaultValue = "2") int iter) {
    log.info("retrieving unread count stream for user {} for {} iterations", userId, iter);

    return Mono.just(1)
            .repeat(iter)
            .delayElements(Duration.ofSeconds(1))
            .concatMap(seq -> getInboxForUser(userId).map(
                    inbox -> ServerSentEvent.<Map<String, String>> builder()
                                .event("unread-count-event")
                                .data(Map.of("userId", userId, "unread", inbox.getUnreadCount().toString()))
                                .build())
            );
  }

  @PostMapping(value = "/inbox/{userId}/messages")
  public Mono<ResponseEntity<Message>> newMessageForUser(
          @PathVariable("userId") String userId,
          @RequestBody Map<String, String> body) {
    log.info("new message for user {}", userId);
    return saveNewMessageForUser(userId, body.get("message"))
            .map( message ->
                    ResponseEntity
                            .created(URI.create("/inbox/" + userId + "/messages/" + message.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(message)
            );
  }

  @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "not found")
  @ExceptionHandler(InboxNotFoundException.class)
  public void notFoundHandler() {
  }

  private Mono<Inbox> getInboxForUser(String userId) {
    return mongo.query(Inbox.class)
            .matching(query(where("userId").is(userId)))
            .first();
  }

  private Mono<Message> saveNewMessageForUser(String userId, String line) {
    Message message = Message.create(line);
    return mongo.updateMulti(
                new Query(where("userId").is(userId)),
                new Update().inc("unreadCount", 1)
                            .push("messages", message),
                Inbox.class)
            .then(Mono.just(message));
  }
}
