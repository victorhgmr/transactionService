package victor.picpaysimplificado.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import victor.picpaysimplificado.domain.transaction.Transaction;
import victor.picpaysimplificado.domain.transaction.TransactionDTO;
import victor.picpaysimplificado.repositories.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {
    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private NotificationService notificationService;


    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
        var payer = this.userService.findUserById(transaction.payerId());
        var payee = this.userService.findUserById(transaction.payeeId());

        userService.validateUser(payer, transaction.amount());

        boolean isAuthorized = authorizeTransaction();

        if(!isAuthorized){
            throw new Exception("Transação não autorizada");
        }

        if(transaction.amount().compareTo(BigDecimal.valueOf(0)) < 0){
            throw new Exception("Não é possível transacionar com valores negativos");

        }

        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transaction.amount());
        newTransaction.setPayer(payer);
        newTransaction.setPayee(payee);
        newTransaction.setTransactionTime(LocalDateTime.now());

        payer.setBalance(payer.getBalance().subtract(transaction.amount()));
        payee.setBalance(payee.getBalance().add(transaction.amount()));

        this.repository.save(newTransaction);
        this.userService.saveUser(payer);
        this.userService.saveUser(payee);

        notificationService.sendNotification(payer, "Transação realizada com sucesso");
        notificationService.sendNotification(payee, "Transação recebida com sucesso");

        return newTransaction;

    }

    public boolean authorizeTransaction(){
        var response = new ResponseEntity<>(HttpStatus.OK); //autorizador externo
        return response.getStatusCode() == HttpStatus.OK; //validador do autorizador da transação

    }
}
