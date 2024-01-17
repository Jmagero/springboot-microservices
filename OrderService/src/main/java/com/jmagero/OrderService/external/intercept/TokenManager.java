package com.jmagero.OrderService.external.intercept;

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

@Component
public class TokenManager {

    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;


    public TokenManager(OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        this.oAuth2AuthorizedClientManager = oAuth2AuthorizedClientManager;
    }

    public String  getAccessToken()  {
           return     oAuth2AuthorizedClientManager
                        .authorize(OAuth2AuthorizeRequest
                                .withClientRegistrationId("internal-client")
                                .principal("internal")
                                .build())
                        .getAccessToken().getTokenValue();
    }
}
