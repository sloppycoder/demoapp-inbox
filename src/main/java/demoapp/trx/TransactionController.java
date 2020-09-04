package demoapp.trx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/transactions")
public class TransactionController {

  @Autowired TransactionRepository repository;

  @GetMapping
  public List<Transaction> findTransanctions() {
    Page<Transaction> page = repository.findAll(PageRequest.of(0, 1));
    return page.get().collect(Collectors.toList());
  }
}
