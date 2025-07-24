package com.productEvee.productEvee.controller;

import org.springframework.web.bind.annotation.*;

import com.productEvee.productEvee.dto.FollowRequest;
import com.productEvee.productEvee.entity.User;
import com.productEvee.productEvee.service.FollowService;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/v1/follows")
@CrossOrigin(origins = "*")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping
    public ResponseEntity<String> follow(@RequestBody FollowRequest request) {
        try {
            boolean success = followService.followUser(request.getFollowerId(), request.getFollowedId());

            if (success) {
                return ResponseEntity.ok("Agora você está seguindo esse usuário.");
            } else {
                return ResponseEntity.badRequest().body("Não foi possível seguir o usuário (já está seguindo ou IDs inválidos).");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).body("Operação interrompida.");
        } catch (ExecutionException e) {
            return ResponseEntity.status(500).body("Erro ao acessar dados.");
        }
    }

    @GetMapping("/followers/{id}")
    public ResponseEntity<List<User>> getFollowers(@PathVariable String id) {
        try {
            List<User> followers = followService.getFollowers(id);
            return ResponseEntity.ok(followers);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).build();
        } catch (ExecutionException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
