package pe.com.service.transfer.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import pe.com.service.transfer.model.Transfer;

@Repository
public interface TransferRepository extends ReactiveMongoRepository<Transfer, String>{

}
