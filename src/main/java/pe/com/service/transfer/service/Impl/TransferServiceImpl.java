package pe.com.service.transfer.service.Impl;

import java.time.LocalDateTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import pe.com.service.transfer.model.Transfer;
import pe.com.service.transfer.model.dto.AccountDto;
import pe.com.service.transfer.model.dto.CreditDto;
import pe.com.service.transfer.model.dto.PaymentDto;
import pe.com.service.transfer.model.dto.TransferDto;
import pe.com.service.transfer.repository.TransferRepository;
import pe.com.service.transfer.service.TransferService;
import pe.com.service.transfer.utils.DateUtil;
import pe.com.service.transfer.utils.TransferUtil;
import pe.com.service.transfer.webclient.AccountWebClient;
import pe.com.service.transfer.webclient.CardWebClient;
import pe.com.service.transfer.webclient.CreditWebClient;
import pe.com.service.transfer.webclient.PaymentWebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransferServiceImpl implements TransferService {

	@Autowired
	WebClient webClient;

	@Autowired
	TransferRepository transferRepository;
	@Autowired
	private ReactiveMongoTemplate reactiveMongoTemplate;

	private final static Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);

	@Override
	public Mono<TransferDto> createTransfer(Mono<TransferDto> entityToDto) {
		CardWebClient cardWebClient = new CardWebClient();
		AccountWebClient accountWebClient = new AccountWebClient();
		CreditWebClient creditWebClient = new CreditWebClient();
		logger.info("inside methode create");

		Mono<TransferDto> result = entityToDto.map(p -> {

			var val = cardWebClient.findByNumber(p.getCardNumber()).map(e -> {
				var getAccounts = Flux.fromIterable(e.getConnectTo()).log();
				Mono<AccountDto> accountDtoMono;
				Mono<CreditDto> creditDtoMono;

				if (e.getType().equals("Débito")) {
					logger.info("String data");
					accountDtoMono = getAccounts.flatMap(accountWebClient::details).next().log();
					accountDtoMono.map(a -> {
						logger.info("Print id: " + a.getId());
						/*
						 * Mono<AccountDto> newAccountDtoMono = null; if
						 * (p.getAmount()>=a.getAvailableBalance()){
						 * newAccountDtoMono=getAccounts.flatMap(i ->
						 * Flux.from(accountWebClient.details(i+1))).next(); }
						 */
						// p.setIdAccountOrigin(a.getId());
						if (TransferUtil.idOrigin(p.getIdAccountDestination())) {

							Mono<Void> v = debtToDebt(a.getId(), p.getIdAccountDestination(), p.getAmount());
							v.doOnSuccess(x -> logger.info("update available balance debtToDebt ")).subscribe();

						} else if (TransferUtil.idDestination(p.getIdAccountDestination())) {
							Mono<Void> v = debtToCredit(a.getId(), p.getIdAccountDestination(), p.getAmount());
							v.doOnSuccess(x -> logger.info("update available balance debtToCredit")).subscribe();

						} else {
							Mono<Void> v = debtToATM(a.getId(), p.getAmount());
							v.doOnSuccess(x -> logger.info("update available balance debtToATM")).subscribe();

						}

						return a;

					}).doOnNext(System.out::println).subscribe();
				} else {
					creditDtoMono = getAccounts.flatMap(creditWebClient::details).next().log();
					creditDtoMono.map(c -> {
						if (TransferUtil.idOrigin(p.getIdAccountDestination())) {
							Mono<Void> v = creditToDebt(c.getId(), p.getIdAccountDestination(), p.getAmount());
							v.doOnSuccess(x -> logger.info("update available balance creditToDebt")).subscribe();
						}
						return c;
					}).doOnNext(System.out::println).subscribe();
				}
				return e;
			});

			val.doOnNext(System.out::println).subscribe();

			var today = LocalDateTime.now();
			p.setDate(DateUtil.toDate(today));
			return p;
		});

		return result.map(TransferUtil::dtoToEntity).flatMap(transferRepository::save).map(TransferUtil::entityToDto);

	}

	public Mono<Void> debtToDebt(String idAccountOrigin, String idAccountDestination, double amount) {

		AccountWebClient accountWebClient = new AccountWebClient();
		var retirement = accountWebClient.details(idAccountOrigin)
				.switchIfEmpty(Mono.error(new ClassNotFoundException(".."))).map(document -> {
					document.setAvailableBalanceAccount(document.getAvailableBalanceAccount() - amount);
					return document;
				});

		var deposit = accountWebClient.details(idAccountDestination)
				.switchIfEmpty(Mono.error(new ClassNotFoundException("no exist account"))).map(document -> {
					document.setAvailableBalanceAccount(document.getAvailableBalanceAccount() + amount);
					return document;
				});

		return Mono.when(accountWebClient.update(idAccountOrigin, retirement),
				accountWebClient.update(idAccountDestination, deposit));
	}

	public Mono<Void> debtToATM(String idAccountOrigin, double amount) {

		AccountWebClient accountWebClient = new AccountWebClient();
		var retirement = accountWebClient.details(idAccountOrigin)
				.switchIfEmpty(Mono.error(new ClassNotFoundException(".."))).map(document -> {
					document.setAvailableBalanceAccount(document.getAvailableBalanceAccount() - amount);
					return document;
				});
		return Mono.when(accountWebClient.update(idAccountOrigin, retirement));
	}

	public Mono<Void> debtToCredit(String idAccountOrigin, String idAccountDestination, double amount) {

		CreditWebClient creditWebClient = new CreditWebClient();
		AccountWebClient accountWebClient = new AccountWebClient();
		PaymentWebClient paymentWebClient = new PaymentWebClient();

		var bean = new PaymentDto();
		bean.setCreditId("");
		bean.setAmount(amount);
		bean.setCommission(0d);
		bean.setDescription("Pago realizado po la cuenta " + idAccountOrigin);
		bean.setCreditId(idAccountDestination);
		bean.setDate(new Date());
		bean.setParam("INS");

		var retirement = accountWebClient.details(idAccountOrigin)
				.switchIfEmpty(Mono.error(new ClassNotFoundException(".."))).map(document -> {
					document.setAvailableBalanceAccount(document.getAvailableBalanceAccount() - amount);
					return document;
				});

		var deposit = creditWebClient.details(idAccountDestination)
				.switchIfEmpty(Mono.error(new ClassNotFoundException("no exist account"))).map(document -> {
					document.setPayments(document.getPayments() + amount);
					return document;
				});

		return Mono.when(accountWebClient.update(idAccountOrigin, retirement),
				creditWebClient.update(idAccountDestination, deposit), paymentWebClient.create(bean));
	}

	public Mono<Void> creditToDebt(String idAccountOrigin, String idAccountDestination, double amount) {

		CreditWebClient creditWebClient = new CreditWebClient();
		AccountWebClient accountWebClient = new AccountWebClient();

		var retirement = creditWebClient.details(idAccountOrigin)
				.switchIfEmpty(Mono.error(new ClassNotFoundException(".."))).map(document -> {
					document.setAvailableBalance(document.getAvailableBalance() - (amount));
					document.setSpending(document.getSpending() + amount);
					return document;
				});
		var deposit = accountWebClient.details(idAccountDestination)
				.switchIfEmpty(Mono.error(new ClassNotFoundException("no exist account"))).map(document -> {
					document.setAvailableBalanceAccount(document.getAvailableBalanceAccount() + amount);
					return document;
				});

		return Mono.when(creditWebClient.update(idAccountOrigin, retirement),
				accountWebClient.update(idAccountDestination, deposit));

	}

	public Flux<TransferDto> reportMovement(String numberCard) {
		logger.info("inside methode reportCardCredit");
		Query query = new Query();
		query.addCriteria(Criteria.where("cardNumber").is(numberCard)).with(Sort.by(Sort.Direction.DESC, "date"))
				.limit(10);
		return reactiveMongoTemplate.find(query, Transfer.class).map(TransferUtil::entityToDto);
	}

	// by used
	public Flux<Transfer> findAllTransferBetweenDateByAccountId(String idAccountOrigin) {
		logger.info("inside methode find ");
		LocalDateTime localDateTime = LocalDateTime.now();
		Query query = new Query();
		query.addCriteria(Criteria.where("date").gte(localDateTime.withDayOfMonth(1)).lte(LocalDateTime.now())
				.and("idAccountOrigin").is(idAccountOrigin));
		return reactiveMongoTemplate.find(query, Transfer.class);
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
	public Flux<AccountDto> getClientByAccountDaoId(String idAccountDao) {
		Flux<AccountDto> flux = webClient.get().uri("http://localhost:8095/", idAccountDao)
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToFlux(AccountDto.class);
		return flux;
	}

}
