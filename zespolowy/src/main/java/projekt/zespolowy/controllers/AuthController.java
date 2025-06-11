package projekt.zespolowy.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import projekt.zespolowy.gameService.RankingService;
import projekt.zespolowy.models.ERole;
import projekt.zespolowy.models.Role;
import projekt.zespolowy.models.User;
import projekt.zespolowy.payload.request.CredentialChangeRequest;
import projekt.zespolowy.payload.request.LoginRequest;
import projekt.zespolowy.payload.request.SignupRequest;
import projekt.zespolowy.payload.response.JwtResponse;
import projekt.zespolowy.payload.response.MessageResponse;
import projekt.zespolowy.repository.RoleRepository;
import projekt.zespolowy.repository.UserRepository;
import projekt.zespolowy.security.jwt.JwtUtils;
import projekt.zespolowy.security.services.UserDetailsImpl;
import projekt.zespolowy.security.services.UserDetailsServiceImpl;
import projekt.zespolowy.security.services.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  RankingService rankingService;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  UserService userService;

  @Autowired
  JwtUtils jwtUtils;
  @Autowired 
  UserDetailsServiceImpl userDetailsServiceImpl;

  /*
   * "username" : "",
     "password" : ""
   * 
   */
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);
    
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt, 
                         userDetails.getId(), 
                         userDetails.getUsername(), 
                         userDetails.getEmail(), 
                         roles));
  }
 /*
  * Sprawdza token - jesli jest ok, zwraca 200 
  */
  @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok().build(); 
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }
    }

   /*
     Moga byc null, jesli zmieniamy np. tylko jedna wartosc czyli tylko haslo, czy nazwe uzytkownika
    "newPassword": "",
    "confirmPassword": "",
    "newUsername": ""
    */
   @PutMapping("/credentials")
   public ResponseEntity<?> changeUserCredentials(@RequestBody CredentialChangeRequest request) {
    try {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        userService.changeCredentials(userId, request);
        UserDetailsImpl updatedUserDetails = (UserDetailsImpl) userDetailsServiceImpl.loadUserByUsername(request.getNewUsername());
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                                           updatedUserDetails, 
                                           authentication.getCredentials(), 
                                           updatedUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        String newToken = jwtUtils.generateJwtToken(newAuthentication);
        return ResponseEntity.ok(new JwtResponse(newToken,  
                                updatedUserDetails.getId(), 
                                updatedUserDetails.getUsername(), 
                           null,
                           null));

    } catch (Exception e) {
        return ResponseEntity.badRequest().body(new MessageResponse("Błąd zmiany danych : " + e.getMessage()));
    }
  }

 /*
  "username" : "",
  "email" : "",
  "password" : "",
  "role" : ["role"] - moze byc null, wtedy przypisuje ROLE_USER
}
  */
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Login jest już w użyciu!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Email jest już w użyciu!"));
    }

    User user = new User(signUpRequest.getUsername(), 
               signUpRequest.getEmail(),
               encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Nie znaleziono roli"));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
              .orElseThrow(() -> new RuntimeException("Nie znaleziono roli"));
          roles.add(adminRole);

          break;
        case "mod":
          Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
              .orElseThrow(() -> new RuntimeException("Nie znaleziono roli"));
          roles.add(modRole);

          break;
        default:
          Role userRole = roleRepository.findByName(ERole.ROLE_USER)
              .orElseThrow(() -> new RuntimeException("Nie znaleziono roli"));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);
    rankingService.initializeUserRanking(user);
    return ResponseEntity.ok(new MessageResponse("Zarejestrowano!"));
  }
 


}
