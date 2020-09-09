package demoapp.users;

import demoapp.users.data.Inbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
public class InboxController {

  @Autowired
  MongoTemplate mongo;

  @RequestMapping(value = "/inbox/{userId}/unread")
  public ResponseEntity<Integer> getInboxUnreadCount(@PathVariable("userId") String userId) {
    Optional<Inbox> inbox = mongo
            .query(Inbox.class)
            .matching(query(where("userId").is(userId))).first();

    if (inbox.isPresent()) {
      return new ResponseEntity<>(inbox.get().getUnreadCount(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(-1, HttpStatus.NOT_FOUND);
    }
  }
}
