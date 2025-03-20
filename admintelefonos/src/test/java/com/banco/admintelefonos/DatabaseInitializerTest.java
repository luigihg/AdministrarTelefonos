package com.banco.admintelefonos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.banco.admintelefonos.component.DatabaseInitializer;

import static org.mockito.Mockito.*;

class DatabaseInitializerTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private DatabaseInitializer databaseInitializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void run_deberiaCrearColeccionSiNoExiste() throws Exception {
        when(mongoTemplate.collectionExists("telefonos")).thenReturn(false);

        databaseInitializer.run();

        verify(mongoTemplate, times(1)).createCollection("telefonos");
    }

    @Test
    void run_noDeberiaCrearColeccionSiYaExiste() throws Exception {
        when(mongoTemplate.collectionExists("telefonos")).thenReturn(true);

        databaseInitializer.run();

        verify(mongoTemplate, never()).createCollection("telefonos");
    }
}
