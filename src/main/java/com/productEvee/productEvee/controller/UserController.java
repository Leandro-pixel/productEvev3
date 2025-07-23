package com.productEvee.productEvee.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

import com.productEvee.productEvee.entity.LoginRequest;
import com.productEvee.productEvee.entity.LoginResponse;
import com.productEvee.productEvee.entity.User;
import com.productEvee.productEvee.service.UserService;

//import app.com.producteve.entity.LoginResponse;


import java.net.URI;
//import java.util.List;
//import java.util.List;
//import java.util.Optional;
import java.util.List;

@RestController //com isso aqui o springboot entende que tudo que eu definir aqui é um endpoint da API
@RequestMapping("/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

   @PostMapping
public CompletableFuture<ResponseEntity<?>> createUser(@RequestBody CreateUserDto createUserDto) {
    return userService.authenticateUser(createUserDto.email())
        .thenCompose(existingUser -> {
            if (existingUser.isPresent()) {
                return CompletableFuture.completedFuture(ResponseEntity.status(409).body("E-mail já cadastrado"));
            } else {
                return userService.createUser(createUserDto)
                    .thenApply(user -> ResponseEntity.created(URI.create("/v1/users/" + user.getUserId())).body(user));
            }
        });
}

    @GetMapping("/{userId}")
public CompletableFuture<ResponseEntity<User>> getUserById(@PathVariable("userId") String userId) {
    return userService.getUserById(userId)
        .thenApply(userOpt -> userOpt.map(ResponseEntity::ok)
                                     .orElse(ResponseEntity.notFound().build()));
}


    @PutMapping("/{userId}")
public CompletableFuture<ResponseEntity<Void>> updateUserById(@PathVariable String userId,
                                                              @RequestBody UpdateUserDto dto) {
    return userService.updateUserById(userId, dto)
            .thenApply(result -> ResponseEntity.noContent().build());
}


    @GetMapping
public CompletableFuture<ResponseEntity<List<User>>> listUsers() {
    return userService.listUsers()
        .thenApply(users -> ResponseEntity.ok(users));
}


    @PostMapping("/login")
public CompletableFuture<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
    return userService.authenticateUser(loginRequest.email())
        .thenApply(userOpt -> {
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(new LoginResponse(userOpt.get().getUserId().toString()));
            } else {
                return ResponseEntity.status(401).body(new LoginResponse("Credenciais inválidas"));
            }
        });
}


    @GetMapping("/search/userName")
public CompletableFuture<ResponseEntity<List<User>>> searchUsersByUsername(@RequestParam String username) {
    return userService.searchUsersByUsername(username)
        .thenApply(users -> ResponseEntity.ok(users));
}



    
 /* 
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody CreateUserDto createUserDto) {
        // Verifica se o usuário existe e as credenciais estão corretas
        Optional<User> user = userService.authenticateUser(createUserDto.email(), createUserDto.password());
    
        if (user.isPresent()) {
            // Cria o objeto de resposta com o UUID
            LoginResponse loginResponse = new LoginResponse(user.get().getUserId().toString());
            return ResponseEntity.ok(loginResponse); // Retorna a resposta no formato JSON
        } else {
            return ResponseEntity.status(401).body(new LoginResponse("Credenciais inválidas"));
        }
    }
    

    @GetMapping
    public ResponseEntity<List<User>> listUsers() {
        var users = userService.listUsers();

        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateUserById(@PathVariable("userId") String userId,
                                               @RequestBody UpdateUserDto updateUserDto) {
        userService.updateUserById(userId, updateUserDto);
        return ResponseEntity.noContent().build();
    }

    
        */
       @DeleteMapping("/{userId}")
public CompletableFuture<ResponseEntity<Void>> deleteById(@PathVariable String userId) {
    return userService.deleteById(userId)
            .thenApply(result -> ResponseEntity.noContent().build());
}

}