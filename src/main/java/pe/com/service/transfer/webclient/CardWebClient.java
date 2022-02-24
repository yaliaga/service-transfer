package pe.com.service.transfer.webclient;

import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import pe.com.service.transfer.endpoints.Constants;
import pe.com.service.transfer.model.dto.CardDto;
import reactor.core.publisher.Mono;

public class CardWebClient {
    WebClient client = WebClient.builder()
            .baseUrl(Constants.URL_CARD_SERVICE_8094)
            .defaultCookie("cookieKey", "cookieValue")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultUriVariables(Collections.singletonMap("url", Constants.URL_CARD_SERVICE_8094))
            .build();

    public Mono<CardDto> findByNumber(String number) {
        return client.get()
                .uri( Constants.URL_CARD_SERVICE_8094 + Constants.CARD+"/" +number)
                .accept(MediaType.APPLICATION_NDJSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(CardDto.class);
                    }
                    else {
                        // Turn to error
                        return response.createException().flatMap(Mono::error);
                    }
                });
    }
}
