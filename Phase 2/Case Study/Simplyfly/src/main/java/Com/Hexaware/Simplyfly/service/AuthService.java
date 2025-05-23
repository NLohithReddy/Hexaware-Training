package Com.Hexaware.Simplyfly.service;

import Com.Hexaware.Simplyfly.model.User;

public interface AuthService {
    String login(String email, String password);
    User findByEmail(String email);
}