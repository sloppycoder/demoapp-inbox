package demoapp.trx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/transactions")
public class TransactionController {

  @Autowired TransactionRepository repository;

  @GetMapping
  public List<Transaction> findTransanctions() {
    return repository.findAll();
  }
}
