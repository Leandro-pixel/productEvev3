package com.productEvee.productEvee.service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import com.google.api.core.ApiFuture;

import org.springframework.stereotype.Service;

import com.google.firebase.database.*;
import com.productEvee.productEvee.controller.CommentDto;
import com.productEvee.productEvee.controller.ProductDto;
import com.productEvee.productEvee.controller.ProductListResponseDTO;
import com.productEvee.productEvee.entity.Comment;
import com.productEvee.productEvee.entity.Product;
import com.productEvee.productEvee.entity.User;

@Service
public class ProductService {
    private final DatabaseReference database;

    private final NotificationService notificationService;
    private final FollowService followService;

    public ProductService(DatabaseReference database,
                                  NotificationService notificationService,
                                  FollowService followService) {
        this.database = database;
        this.notificationService = notificationService;
        this.followService = followService;
    }

    // --- Salvar Produto
    public void saveProduct(Product product) throws InterruptedException, ExecutionException {
        DatabaseReference productsRef = database.child("products");
        String productId = product.getId() != null ? product.getId().toString() : UUID.randomUUID().toString();
        product.setUserId(UUID.fromString(productId));
        ApiFuture<Void> future = productsRef.child(productId).setValueAsync(product);
        future.get(); // Espera operação completar
    }

    public Product copyGlobalProductToUser(String globalProductId, UUID userId) throws InterruptedException {
    // 1. Buscar o produto global
    try {
        Product globalProduct = getProductById(globalProductId);
    if (globalProduct == null) {
        throw new RuntimeException("Produto global não encontrado");
    }

    if (!globalProduct.isGlobalStock()) {
        throw new RuntimeException("Produto não é global");
    }

    // 2. Criar cópia para o usuário
    Product personalProduct = new Product();
    personalProduct.setUserId(userId);
    personalProduct.setName(globalProduct.getName());
    personalProduct.setValue(globalProduct.getValue());
    personalProduct.setEstimateWasteDays(globalProduct.getEstimateWasteDays());
    personalProduct.setLastPurchaseDate(globalProduct.getLastPurchaseDate());
    personalProduct.setReminderDate(globalProduct.getReminderDate());
    personalProduct.setCalendarEventId(null);
    personalProduct.setGlobalStock(false);
    personalProduct.setActiveStatus(true);

    // 3. Gerar novo ID e salvar no Firebase
    String newProductId = UUID.randomUUID().toString();
    DatabaseReference newProductRef = database.child("products").child(newProductId);
    newProductRef.setValueAsync(personalProduct).get();

    // 4. Notificar o dono do produto global (se possível)
    User followedUser = getUserById(globalProduct.getUserId());
    User followerUser = getUserById(userId);

    if (followedUser != null && followerUser != null) {
        String message = followerUser.getUsername() + " adicionou um novo produto: " + globalProduct.getName();
        String title = "Produto copiado!";

        String playerId = followedUser.getOnesignalId();
        if (playerId != null && !playerId.isBlank()) {
            notificationService.sendNotification(List.of(playerId), title, message);
        }
    }

    return personalProduct;
    } catch (ExecutionException e) {
        throw new RuntimeException("Erro ao buscar produto global", e);
    }
}


    public Product addComment(String productId, CommentDto dto) throws InterruptedException, ExecutionException {
    // 1. Buscar o produto no Firebase
    Product product = getProductById(productId);
    if (product == null) {
        throw new RuntimeException("Produto não encontrado");
    }

    // 2. Criar comentário
    Comment comment = new Comment();
    comment.setUserId(dto.getUserId());
    comment.setText(dto.getText());

    // 3. Buscar usuário autor do comentário
    User user = getUserById(dto.getUserId());
    String userName = (user != null) ? user.getUsername() : "Desconhecido";
    comment.setUserName(userName);
    comment.setProduct(product); // se necessário

    // 4. Adicionar comentário à lista existente
    List<Comment> currentComments = product.getComments();
    if (currentComments == null) {
        currentComments = new ArrayList<>();
    }
    currentComments.add(comment);
    product.setComments(currentComments);

    // 5. Atualizar o produto com os comentários no Firebase
    database.child("products").child(productId).setValueAsync(product).get();

    // 6. Notificar o dono do produto
    User owner = getUserById(product.getUserId());
    if (owner != null && user != null) {
        String title = "Novo comentário!";
        String message = user.getUsername() + " comentou: " + dto.getText();
        String playerId = owner.getOnesignalId();

        if (playerId != null && !playerId.isBlank()) {
            notificationService.sendNotification(List.of(playerId), title, message);
        }
    }

    return product;
}


    // --- Criar Produto
    public Product create(ProductDto dto) throws InterruptedException, ExecutionException {
        // Buscar usuário pelo ID no Firebase
        User owner = getUserById(dto.getUserId());
        if (owner == null) throw new RuntimeException("Usuário não encontrado");

        String userName = owner.getUsername();

        Product product = new Product(
                dto.getUserId(),
                dto.getName(),
                dto.getValue(),
                dto.getEstimateWasteDays(),
                dto.getLastPurchaseDate(),
                dto.getCalendarEventId()
        );

        product.setAddedBy(userName);
        product.setGlobalStock(true);
        product.setUsers(1);

        calculateReminder(product);

        // Salvar produto no Firebase com novo ID gerado
        DatabaseReference productsRef = database.child("products");
        String productId = UUID.randomUUID().toString();
        product.setUserId(UUID.fromString(productId));
        productsRef.child(productId).setValueAsync(product).get();

        // Buscar seguidores (isso deve continuar com seu FollowService)
        List<User> followers = followService.getFollowers(owner.getUserId());

        // Enviar notificação para seguidores
        List<String> oneSignalIds = followers.stream()
                .map(User::getOnesignalId)
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toList());

        notificationService.sendNotification(
                oneSignalIds,
                "Novo produto de " + owner.getUsername(),
                owner.getUsername() + " acabou de adicionar um novo produto: " + product.getName()
        );

        return product;
    }

    // --- Atualizar Produto
    public Product update(String productId, ProductDto dto) throws InterruptedException, ExecutionException {
        DatabaseReference productRef = database.child("products").child(productId);

        Product existing = getProductById(productId);
        if (existing == null) throw new RuntimeException("Produto não encontrado");

        existing.setUserId(dto.getUserId());
        existing.setName(dto.getName());
        existing.setValue(dto.getValue());
        existing.setEstimateWasteDays(dto.getEstimateWasteDays());
        existing.setLastPurchaseDate(dto.getLastPurchaseDate());
        existing.setCalendarEventId(dto.getCalendarEventId());

        productRef.setValueAsync(existing).get();

        return existing;
    }

    // --- Buscar Produto por ID
    public Product getProductById(String productId) throws InterruptedException, ExecutionException {
    final Product[] result = new Product[1];
    final Object lock = new Object();

    DatabaseReference productRef = database.child("products").child(productId);
    productRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            if (snapshot.exists()) {
                result[0] = snapshot.getValue(Product.class);
            }
            synchronized (lock) {
                lock.notify(); // Libera a thread
            }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            synchronized (lock) {
                lock.notify();
            }
        }
    });

    synchronized (lock) {
        lock.wait(); // Espera o callback completar
    }

    return result[0];
}


    public User getUserById(UUID userId) throws InterruptedException {
    final User[] result = new User[1];
    final Object lock = new Object();

    DatabaseReference userRef = database.child("users").child(userId.toString());
    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            if (snapshot.exists()) {
                result[0] = snapshot.getValue(User.class);
            }
            synchronized (lock) {
                lock.notify();
            }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            System.err.println("Erro ao buscar usuário: " + error.getMessage());
            synchronized (lock) {
                lock.notify();
            }
        }
    });

    synchronized (lock) {
        lock.wait(); // Espera o callback completar
    }

    return result[0];
}


    // --- Deletar Produto
    public void delete(String productId) throws InterruptedException, ExecutionException {
        DatabaseReference productRef = database.child("products").child(productId);
        productRef.removeValueAsync().get();
    }

    public ProductListResponseDTO listAll() throws InterruptedException {
    final List<Product> products = new ArrayList<>();
    final Object lock = new Object();

    DatabaseReference productsRef = database.child("products");

    productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            if (snapshot.exists()) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    products.add(product);
                }
            }
            synchronized (lock) {
                lock.notify();
            }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            System.err.println("Erro ao listar produtos: " + error.getMessage());
            synchronized (lock) {
                lock.notify();
            }
        }
    });

    synchronized (lock) {
        lock.wait(); // Aguarda retorno do Firebase
    }

    return new ProductListResponseDTO(products.size(), products);
}

public ProductListResponseDTO listByUserId(UUID userId) throws InterruptedException {
    final List<Product> products = new ArrayList<>();
    final Object lock = new Object();

    DatabaseReference productsRef = database.child("products");
    Query query = productsRef.orderByChild("userId").equalTo(userId.toString());

    query.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            if (snapshot.exists()) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    products.add(product);
                }
            }
            synchronized (lock) {
                lock.notify();
            }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            System.err.println("Erro ao listar produtos por usuário: " + error.getMessage());
            synchronized (lock) {
                lock.notify();
            }
        }
    });

    synchronized (lock) {
        lock.wait(); // Espera o Firebase responder
    }

    return new ProductListResponseDTO(products.size(), products);
}


    private void calculateReminder(Product product) {
        LocalDate lastPurchaseDate = product.getLastPurchaseDate();
        int estimateWasteDays = product.getEstimateWasteDays();
        LocalDate reminderDate = lastPurchaseDate.plusDays(estimateWasteDays);
        product.setReminderDate(reminderDate);
    }

    // Outros métodos podem ser adaptados de forma similar,
    // transformando chamadas JPA em operações com DatabaseReference e ApiFuture

}
