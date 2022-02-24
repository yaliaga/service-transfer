package pe.com.service.transfer.webclient;

import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import pe.com.service.transfer.endpoints.Constants;
import pe.com.service.transfer.model.dto.PaymentDto;
import reactor.core.publisher.Mono;

public class PaymentWebClient {

    WebClient client = WebClient.builder()
            .baseUrl(Constants.URL_PAYMENT_SERVICE_8098)
            .defaultCookie("cookieKey", "cookieValue")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultUriVariables(Collections.singletonMap("url", Constants.URL_PAYMENT_SERVICE_8098))
            .build();


    public Mono<PaymentDto> create(PaymentDto paymentDtoMono) {
        return client.post()
                .uri( Constants.URL_PAYMENT_SERVICE_8098 + Constants.PAYMENT)
                //.accept(MediaType.APPLICATION_NDJSON)
                .body(Mono.just(paymentDtoMono), PaymentDto.class)
                .retrieve()
                .bodyToMono(PaymentDto.class);
    }

}
