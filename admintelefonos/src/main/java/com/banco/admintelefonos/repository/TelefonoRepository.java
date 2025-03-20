package com.banco.admintelefonos.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.banco.admintelefonos.model.Telefono;

@Repository
public interface TelefonoRepository extends MongoRepository<Telefono, String> {
    Optional<Telefono> findByImei(String imei);
}
