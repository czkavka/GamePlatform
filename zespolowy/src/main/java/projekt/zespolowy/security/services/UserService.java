package projekt.zespolowy.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import projekt.zespolowy.models.User;
import projekt.zespolowy.payload.request.CredentialChangeRequest;
import projekt.zespolowy.repository.UserRepository;

@Service
public class UserService {
    @Autowired 
    UserRepository userRepo;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Transactional
    public void changeCredentials(Long userId, CredentialChangeRequest request) throws Exception {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new Exception("Nie znaleziono użytkownika : " + userId));
       
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (request.getConfirmPassword() == null || !request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new Exception("Wpisane hasła nie są identyczne!");
            }
            if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                throw new Exception("Nowe hasło nie może być takie samo jak obecne hasło!");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        if (request.getNewUsername() != null && !request.getNewUsername().isEmpty()) {
            if (userRepo.existsByUsername(request.getNewUsername())) {
                throw new Exception("Podana nazwa użytkownika jest już zajęta!");
            }
            user.setUsername(request.getNewUsername());
        }

        userRepo.save(user);

        //po to zeby zgadzala sie w sesji pozniej autoryzacja
        if (request.getNewUsername() != null && !request.getNewUsername().isEmpty()) {
            UserDetailsImpl updatedUserDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(user.getUsername());
            Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                updatedUserDetails, null, updatedUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        }
    }

}
