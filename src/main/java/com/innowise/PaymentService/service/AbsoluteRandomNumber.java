package com.innowise.PaymentService.service;

import com.innowise.common.exception.ExternalApiResponseCustomException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@AllArgsConstructor
@Component
public class AbsoluteRandomNumber {

    private WebClient webClient;

    public Integer getAbsoluteRandomNumber() {
        String response = webClient.get()
                .uri("https://www.random.org/integers/?num=1&min=1&max=32767&col=1&base=10&format=plain&rnd=new")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        if (response == null) {
            throw new ExternalApiResponseCustomException("Could not get response from Web Client");
        }
        return Integer.parseInt(response.trim());
    }
}
