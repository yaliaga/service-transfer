package pe.com.service.transfer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import pe.com.service.transfer.model.Transfer;
import pe.com.service.transfer.model.dao.AccountDao;
import pe.com.service.transfer.service.Impl.TransferServiceImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/trasnfer")
public class TransferController {

	@Autowired
	private TransferServiceImpl transferServiceImpl;
	
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Transfer> create(@RequestBody Transfer transfer) {
		return transferServiceImpl.createTransfer(transfer);
	}
	
	@GetMapping(value = "/all", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public Flux<Transfer> findAll() {
		return transferServiceImpl.findAllTransfer();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Mono<Transfer>> findTransferById(@PathVariable("id") String id) {
		Mono<Transfer> product = transferServiceImpl.findByTransferId(id);
		return new ResponseEntity<Mono<Transfer>>(product, product != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
	@PutMapping
	@ResponseStatus(HttpStatus.OK)
	public Mono<Transfer> update(@RequestBody Transfer product) {
		return transferServiceImpl.updateTransfer(product);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<Void> delete(@PathVariable("id") String id) {
		return transferServiceImpl.deleteTransfer(id);
	}
	
	@GetMapping("/transfer/{idAccount}")
	public ResponseEntity<Flux<AccountDao>> getClientByAccountDaoId(@PathVariable("idAccountDao") String idAccountDao) {
		Flux<AccountDao> accountDao = transferServiceImpl.getClientByAccountDaoId(idAccountDao);
		return new ResponseEntity<Flux<AccountDao>>(accountDao, accountDao != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
	}
}
