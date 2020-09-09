package demoapp.users.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Message {
  @MongoId String id;
  String subject;
  String body;
  boolean isRead;

  public static Message create(String line) {
    String[] str = line.split("\\|");
    return new Message(UUID.randomUUID().toString(), str[0], str[1], false);
  }

}
