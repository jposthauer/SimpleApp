package com.example;

import java.util.UUID;
import org.springframework.data.neo4j.core.schema.IdGenerator;

public class CustomIdGenerator implements IdGenerator<String> {

    @Override
    public String generateId(String primaryLabel, Object entity) {
        return primaryLabel + UUID.randomUUID();
    }
}
