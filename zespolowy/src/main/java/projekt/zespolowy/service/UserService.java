package projekt.zespolowy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import projekt.zespolowy.model.User;
import projekt.zespolowy.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
	PasswordEncoder passwordEncoder;
    
    //zwraca wszystkich uzytkownikow w bazie, ich loginy
    public List<User> getAllUsernames() {

        return userRepo.findAll();
    }

    public Boolean registerUser(String username, String password, String email)
    {


        return true;
    }


    public Boolean loginUser(String username, String password)
    {
        
        return true;
    }



}
