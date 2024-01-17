package com.jmagero.OrderService.external.intercept;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateInterceptor.class);


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        LOGGER.info("RestTemplate does a http/s to - {} with HTTP Method : {}", request.getURI(), request.getMethod().name());

        request.getHeaders().add("Authorization", "Bearer "+
                oAuth2AuthorizedClientManager
                        .authorize(OAuth2AuthorizeRequest
                                .withClientRegistrationId("internal-client")
                                .principal("internal")
                                .build())
                        .getAccessToken().getTokenValue());
        ClientHttpResponse response = execution.execute(request, body);
        if (response.getStatusCode().is4xxClientError() || response.getStatusCode()
                .is5xxServerError()) {
            LOGGER.error("RestTemplate received a bad response from : {} - with response status : {} and body : {} ",
                    request.getURI(), response.getStatusCode(), new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8));
        } else {
            LOGGER.info("RestTemplate received a good response from : {}- with response status {}",
                    request.getURI(),
                    response.getStatusCode());
        }

        return response;
    }

}
