package Com.Hexaware.Simplyfly.service;
import java.util.List;

import Com.Hexaware.Simplyfly.model.User;

public interface UserService {
    User registerPassenger(User user);
    User registerFlightOwner(User user);
    User getUserById(Long id);
    List<User> getAllUsers();
    void deleteUser(Long id);
}

