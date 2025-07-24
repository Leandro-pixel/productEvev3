package com.productEvee.productEvee.dto;


// Não é @Entity! É só um DTO para Firebase
public class FirebaseUserDTO {
    public String email;
    public String username;
    public String userId;

    public Long creationTimestamp;
    //public Map<String, Object> updateTimestamp;


    public FirebaseUserDTO() {
        // Construtor padrão vazio é obrigatório para Firebase
    }
}

