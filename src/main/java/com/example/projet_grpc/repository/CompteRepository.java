package com.example.projet_grpc.repository;

import com.example.projet_grpc.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompteRepository extends JpaRepository<Compte, String> {
    List<Compte> findAllByType(String type);

}