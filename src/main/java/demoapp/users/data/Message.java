package demoapp.users.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@AllArgsConstructor
public class Message {
    @MongoId String id;
    String subject;
    String body;
    boolean isRead;
}

