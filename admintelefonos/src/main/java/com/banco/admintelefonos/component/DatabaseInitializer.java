package com.banco.admintelefonos.component;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    public DatabaseInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verificar si la colección "Telefono" existe, si no, créala
        if (!mongoTemplate.collectionExists("telefonos")) {
            mongoTemplate.createCollection("telefonos");
        }

    }
}
