package projekt.zespolowy.controller;

import org.springframework.beans.factory.annotation.Autowired;

import projekt.zespolowy.service.UserService;

@RestController
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public List<Autor> getAutors() {

        return autorService.getAllAutors();
    }