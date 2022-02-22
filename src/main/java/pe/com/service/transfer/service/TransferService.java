package pe.com.service.transfer.service;


import pe.com.service.transfer.model.Transfer;
import pe.com.service.transfer.model.dao.AccountDao;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransferService {
	Mono<Transfer> createTransfer(Transfer transfer);
	
	Mono<Transfer> findByTransferId(String id);

	Flux<Transfer> findAllTransfer();

	Mono<Transfer> updateTransfer(Transfer transfer);

	Mono<Void> deleteTransfer(String id);

	Flux<AccountDao> getClientByAccountDaoId(String idAccountDao);
}
