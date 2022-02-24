package pe.com.service.transfer.service;


import pe.com.service.transfer.model.Transfer;
import pe.com.service.transfer.model.dto.AccountDto;
import pe.com.service.transfer.model.dto.TransferDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransferService {
	Mono<TransferDto> createTransfer(Mono<TransferDto> entityToDto);
	
	Mono<Transfer> findByTransferId(String id);

	Flux<Transfer> findAllTransfer();

	Mono<Transfer> updateTransfer(Transfer transfer);

	Mono<Void> deleteTransfer(String id);

	Flux<AccountDto> getClientByAccountDaoId(String idAccountDao);
	
	
}
