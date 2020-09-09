package demoapp.users.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "inbox")
@Data
@AllArgsConstructor
public class Inbox {
  @Id String id;
  String userId;
  int unreadCount;
  List<Message> messages;
}
