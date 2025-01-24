package com.wander.restful.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wander.restful.models.Client;

public interface ClientRepository extends JpaRepository<Client, Long>{
    
    public Client findByEmail(String email);
}
