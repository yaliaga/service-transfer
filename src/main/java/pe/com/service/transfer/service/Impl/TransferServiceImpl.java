package pe.com.service.transfer.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import pe.com.service.transfer.model.Transfer;
import pe.com.service.transfer.model.dao.AccountDao;
import pe.com.service.transfer.repository.TransferRepository;
import pe.com.service.transfer.service.TransferService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransferServiceImpl implements TransferService {

	@Autowired
	WebClient webClient;
	
	@Autowired
	TransferRepository transferRepository;
	

	@Override
	public Mono<Transfer> createTransfer(Transfer transfer) {
		return transferRepository.save(transfer);
		
	}
	
	@Override
	public Mono<Transfer> findByTransferId(String id) {
		return transferRepository.findById(id);
		
	}

	@Override
	public Flux<Transfer> findAllTransfer() {
		return transferRepository.findAll();
	}

	@Override
	public Mono<Transfer> updateTransfer(Transfer transfer) {
		return transferRepository.save(transfer);
	}

	@Override
	public Mono<Void> deleteTransfer(String id) {
		return transferRepository.deleteById(id);
	}

	@Override
	public Flux<AccountDao> getClientByAccountDaoId(String idAccountDao) {
		Flux<AccountDao> flux = 
				webClient
				.get()
				.uri("http://localhost:8095/}", idAccountDao)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(AccountDao.class);
		return flux;
	}


	
}
