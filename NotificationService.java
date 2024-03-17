package victor.picpaysimplificado.services;

import org.springframework.stereotype.Service;
import victor.picpaysimplificado.domain.user.User;

@Service
public class NotificationService {
    public void sendNotification(User user, String message){

        String email = user.getEmail();
        System.out.println(email+message);
    }
}
