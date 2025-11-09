package com.pawtner.WithPet.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        HttpClient http = HttpClient.create().responseTimeout(Duration.ofSeconds(20));
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(http))
                .build();
    }
}