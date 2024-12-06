package com.example.projet_grpc.controllers;

import com.example.project_grpc.stubs.*;
import com.example.projet_grpc.service.CompteService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import com.example.projet_grpc.entities.Compte;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@GrpcService
public class CompteServiceImpl extends CompteServiceGrpc.CompteServiceImplBase {

    private final Map<String, Compte> compteDB = new ConcurrentHashMap<>();

    private final CompteService compteService;

    public CompteServiceImpl(CompteService compteService) {
        this.compteService = compteService;
    }

    @Override
    public void allComptes(GetAllComptesRequest request,
                           StreamObserver<GetAllComptesResponse> responseObserver) {
        var comptes = compteService.findAllComptes().stream()
                .map(compte -> com.example.project_grpc.stubs.Compte.newBuilder()
                        .setId(compte.getId())
                        .setSolde(compte.getSolde())
                        .setDateCreation(compte.getDateCreation())
                        .setType(TypeCompte.valueOf(compte.getType()))
                        .build())
                .collect(Collectors.toList());

        responseObserver.onNext(GetAllComptesResponse.newBuilder()
                .addAllComptes(comptes).build());
        responseObserver.onCompleted();
    }

    @Override
    public void saveCompte(SaveCompteRequest request,
                           StreamObserver<SaveCompteResponse> responseObserver) {
        var compteReq = request.getCompte();
        var compte = new com.example.projet_grpc.entities.Compte();
        compte.setSolde(compteReq.getSolde());
        compte.setDateCreation(compteReq.getDateCreation());
        compte.setType(compteReq.getType().name());

        var savedCompte = compteService.saveCompte(compte);

        var grpcCompte = com.example.project_grpc.stubs.Compte.newBuilder()
                .setId(savedCompte.getId())
                .setSolde(savedCompte.getSolde())
                .setDateCreation(savedCompte.getDateCreation())
                .setType(TypeCompte.valueOf(savedCompte.getType()))
                .build();

        responseObserver.onNext(SaveCompteResponse.newBuilder()
                .setCompte(grpcCompte).build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteCompteById(GetCompteByIdRequest request, StreamObserver<DeleteCompteResponse> responseObserver) {
        String compteId = request.getId();
        compteService.deleteComptebyId(compteId);
        DeleteCompteResponse response = DeleteCompteResponse.newBuilder()
                .setMessage("Compte supprimé.")
                .setSuccess(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void compteByType(GetCompteTypeRequest request, StreamObserver<GetCompteByTypeResponse> responseObserver) {
        List<Object> comptes = compteService.findAllByType(request.getType()).stream()
                .map(compte -> com.example.project_grpc.stubs.Compte.newBuilder()
                        .setId(compte.getId())
                        .setSolde(compte.getSolde())
                        .setDateCreation(compte.getDateCreation())
                        .setType(TypeCompte.valueOf(compte.getType()))
                        .build())
                .collect(Collectors.toList());
        responseObserver.onNext(GetCompteByTypeResponse.newBuilder()
                .addAllComptes(comptes).build());
        responseObserver.onCompleted();
    }
}