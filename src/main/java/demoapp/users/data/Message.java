package demoapp.users.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    String id;
    String subject;
    String body;
    boolean isUnread;
}

