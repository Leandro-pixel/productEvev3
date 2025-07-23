package com.productEvee.productEvee.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ou use Integer, se preferir

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private int estimateWasteDays;

    @Column(nullable = false)
    private LocalDate lastPurchaseDate; // Alterado para LocalDateTime

    private LocalDate reminderDate; // Novo campo para lembrar a data
    
    private String calendarEventId;

    @CreationTimestamp
    private LocalDateTime creationTimestamp;

    // Se é um produto do estoque global ou pessoal
    @Column(nullable = false)
    private boolean globalStock = false;

    // Nome ou ID de quem criou o produto global (pode ser null para pessoais)
    private String addedBy;

    // Status de ativo/inativo (para o botão "Start/Stop")
    @Column(nullable = false)
    private boolean activeStatus = false;

    // Contador de usuários que usam esse produto global
    private int users = 0;

    // Comentários associados (relação 1-N)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Comment> comments = new ArrayList<>();


    // Construtores
    public Product() {}

    public Product(UUID userId, String name, double value, int estimateWasteDays,
                   LocalDate lastPurchaseDate, String calendarEventId) {
        this.userId = userId;
        this.name = name;
        this.value = value;
        this.estimateWasteDays = estimateWasteDays;
        this.lastPurchaseDate = lastPurchaseDate;
        this.calendarEventId = calendarEventId;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getEstimateWasteDays() {
        return estimateWasteDays;
    }

    public void setEstimateWasteDays(int estimateWasteDays) {
        this.estimateWasteDays = estimateWasteDays;
    }

    public LocalDate getLastPurchaseDate() {
        return lastPurchaseDate;
    }

    public void setLastPurchaseDate(LocalDate lastPurchaseDate) {
        this.lastPurchaseDate = lastPurchaseDate;
    }

    public String getCalendarEventId() {
        return calendarEventId;
    }

    public void setCalendarEventId(String calendarEventId) {
        this.calendarEventId = calendarEventId;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }
    public boolean isGlobalStock() {
        return globalStock;
    }
    
    public void setGlobalStock(boolean globalStock) {
        this.globalStock = globalStock;
    }
    
    public String getAddedBy() {
        return addedBy;
    }
    
    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }
    
    public boolean isActiveStatus() {
        return activeStatus;
    }
    
    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }
    
    public int getUsers() {
        return users;
    }
    
    public void setUsers(int users) {
        this.users = users;
    }
    
    public List<Comment> getComments() {
        return comments;
    }
    
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    
}
