package pe.com.service.transfer.webclient;

import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import pe.com.service.transfer.endpoints.Constants;
import pe.com.service.transfer.model.dto.CreditDto;
import reactor.core.publisher.Mono;

public class CreditWebClient {

    WebClient client = WebClient.builder()
            .baseUrl(Constants.URL_CREDIT_SERVICE_8097)
            .defaultCookie("cookieKey", "cookieValue")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultUriVariables(Collections.singletonMap("url", Constants.URL_CREDIT_SERVICE_8097))
            .build();

    public Mono<CreditDto> details(String id) {
        return client.get()
                .uri( Constants.URL_CREDIT_SERVICE_8097 + Constants.CREDIT + "/" + id)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(CreditDto.class);
                    }
                    else {
                        // Turn to error
                        return response.createException().flatMap(Mono::error);
                    }
                });
    }
    public Mono<CreditDto> update(String creditDtoId, Mono<CreditDto> creditDtoMono) {
        return client.put()
                .uri(Constants.URL_CREDIT_SERVICE_8097 + Constants.CREDIT +"/"+ creditDtoId)
                .body(creditDtoMono, CreditDto.class)
                .retrieve()
                .bodyToMono(CreditDto.class);
    }
}
