package com.jmagero.OrderService;

import com.jmagero.OrderService.external.intercept.RestTemplateInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@RequiredArgsConstructor
@Configuration
public class OrderServiceApplication {

	private final ClientRegistrationRepository clientRegistrationRepository;
	private final OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setInterceptors(
                List.of(
                        new RestTemplateInterceptor(
								authorizedClientManager(
                                        clientRegistrationRepository,
                                        oAuth2AuthorizedClientRepository
                                )
                        )
                )
		);
		return new RestTemplate();
	}

//	@Bean
//	public OAuth2AuthorizedClientManager clientManager(
//			ClientRegistrationRepository clientRegistrationRepository,
//			OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository
//	){
//		OAuth2AuthorizedClientProvider oAuth2AuthorizedClientProvider
//		= OAuth2AuthorizedClientProviderBuilder.builder()
//						.clientCredentials()
//				.build();
//
//		DefaultOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager
//				= new DefaultOAuth2AuthorizedClientManager(
//						clientRegistrationRepository,
//				oAuth2AuthorizedClientRepository
//		);
//		oAuth2AuthorizedClientManager.setAuthorizedClientProvider(
//				oAuth2AuthorizedClientProvider
//		);
//        return oAuth2AuthorizedClientManager;
//	}

	@Bean
	public OAuth2AuthorizedClientManager authorizedClientManager(
			ClientRegistrationRepository clientRegistrationRepository,
			OAuth2AuthorizedClientRepository authorizedClientRepository) {

		OAuth2AuthorizedClientProvider authorizedClientProvider =
				OAuth2AuthorizedClientProviderBuilder.builder()
						.clientCredentials()
						.build();

		var authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
				clientRegistrationRepository,
				authorizedClientRepository);

		authorizedClientManager
				.setAuthorizedClientProvider(authorizedClientProvider);

		return authorizedClientManager;
	}
}
