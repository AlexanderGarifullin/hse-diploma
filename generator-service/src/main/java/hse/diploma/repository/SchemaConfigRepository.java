package hse.diploma.repository;

import hse.diploma.entity.SchemaConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SchemaConfigRepository
        extends MongoRepository<SchemaConfig, String> {

}