package demoapp.trx;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "transactions")
public class Transaction {
    @Id
    String id;
    String memo;
    Float amount;
    String code;
}
