package victor.picpaysimplificado.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import victor.picpaysimplificado.domain.user.User;
import victor.picpaysimplificado.domain.user.UserDTO;
import victor.picpaysimplificado.domain.user.UserType;
import victor.picpaysimplificado.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public void saveUser(User user){
        this.repository.save(user);

    }
    public User createUser(UserDTO user) {
        User newUser = new User(user);
        this.saveUser(newUser);
        return newUser;
    }

    public List<User> getAllUsers(){
        return this.repository.findAll();
    }
    public User findUserById(Long id) throws Exception {
        return this.repository.findUserById(id).orElseThrow(()-> new Exception("Usuário não encontrado"));
    }

    public boolean validateUser(User payer, BigDecimal amount) throws Exception {
        if(payer.getUserType() == UserType.MERCHANT){
            throw new Exception("Lojista não pode realizar transações");
        }
        if(payer.getBalance().compareTo(amount) < 0){
            throw new Exception("Saldo insuficiente");
        }
        return true;


    }
}
