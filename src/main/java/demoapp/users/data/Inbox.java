package demoapp.users.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Document(collection = "inbox")
@Data
@AllArgsConstructor
public class Inbox {
  @MongoId
  String id;
  String userId;
  Integer unreadCount;
  List<Message> messages;
}
