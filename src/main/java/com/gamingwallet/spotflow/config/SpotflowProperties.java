package com.gamingwallet.spotflow.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "spotflow")
public record SpotflowProperties (
        String baseUrl,
        String secretKey,
        String publicKey
){

}
