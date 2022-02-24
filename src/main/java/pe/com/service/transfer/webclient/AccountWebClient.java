package pe.com.service.transfer.webclient;

import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import pe.com.service.transfer.endpoints.Constants;
import pe.com.service.transfer.model.dto.AccountDto;
import reactor.core.publisher.Mono;

public class AccountWebClient {
    WebClient client = WebClient.builder()
            .baseUrl(Constants.URL_ACCOUNT_SERVICE_8095)
            .defaultCookie("cookieKey", "cookieValue")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultUriVariables(Collections.singletonMap("url", Constants.URL_ACCOUNT_SERVICE_8095))
            .build();

    public Mono<AccountDto> details(String id) {
        return client.get()
                .uri( Constants.URL_ACCOUNT_SERVICE_8095 + Constants.ACCOUNT+ "/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(AccountDto.class);
                    }
                    else {
                        // Turn to error
                        return response.createException().flatMap(Mono::error);
                    }
                });
    }

    public Mono<AccountDto> update(String accountId, Mono<AccountDto> account) {
        return client.put()
                .uri(Constants.URL_ACCOUNT_SERVICE_8095 + Constants.ACCOUNT +"/"+ accountId)
                .body(account, AccountDto.class)
                .retrieve()
                .bodyToMono(AccountDto.class);
    }
}
