package edu.jakub.jureczko.storage.config;

import edu.jakub.jureczko.storage.entity.CategoryEntity;
import edu.jakub.jureczko.storage.entity.ProductEntity;
import edu.jakub.jureczko.storage.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DbInitializer implements CommandLineRunner  {
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        productRepository.saveAll(List.of(
                new ProductEntity(UUID.randomUUID(), "chair", 999, CategoryEntity.HOBBY),
                new ProductEntity(UUID.randomUUID(), "chair1", 999, CategoryEntity.HOBBY),
                new ProductEntity(UUID.randomUUID(), "chair2", 999, CategoryEntity.HOBBY),
                new ProductEntity(UUID.randomUUID(), "chair3", 999, CategoryEntity.HOBBY),
                new ProductEntity(UUID.randomUUID(), "chair4", 999, CategoryEntity.HOBBY),
                new ProductEntity(UUID.randomUUID(), "chair5", 999, CategoryEntity.HOBBY),
                new ProductEntity(UUID.randomUUID(), "chair6", 999, CategoryEntity.HOBBY),
                new ProductEntity(UUID.randomUUID(), "chair7", 999, CategoryEntity.HOBBY)
        ));
    }
}
