package projekt.zespolowy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import projekt.zespolowy.model.User;


public interface UserRepository extends JpaRepository<User,Long> {
//zwraca loginy uzytkownikow
    Optional<User> findAllByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Integer id);
    


}
