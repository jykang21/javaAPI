package kr.co.softbridge.sobroplatform.commons;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.Lists;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Value("${jwt.header}")
    private String HEADER_AUTH;
	
	@Bean public Docket swaggerSpringfoxDocket() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2) 
				.apiInfo(apiEndPointsInfo())
				.pathMapping("/")
				.forCodeGeneration(true)
				.genericModelSubstitutes(ResponseEntity.class)
				.ignoredParameterTypes(java.sql.Date.class)
				.directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
				.directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
				.directModelSubstitute(java.time.LocalDateTime.class, Date.class)
				.securityContexts(Lists.newArrayList(securityContext()))
				.securitySchemes(Lists.newArrayList(apiKey()))
				.useDefaultResponseMessages(false);
		
		docket = docket.select()
				.paths(PathSelectors.any())
				.build();
		return docket;
	}
	
	private ApiInfo apiEndPointsInfo() {
		return new ApiInfoBuilder().title("Softbridge Soboro Platform API")
				.description("This documents Soboro Platform about decide version 1 REST API")
				.contact(new Contact("Jo Eun Ilk", "softbridge.co.kr", "joeunilk@softbridge.co.kr"))
				.license("Apache 2.0")
				.version("v1")
				.build();
	}
	
	private ApiKey apiKey() {
		return new ApiKey("JWT", HEADER_AUTH, "header");
	}
	
	private springfox.documentation.spi.service.contexts.SecurityContext securityContext() {
		return springfox.documentation.spi.service.contexts.SecurityContext.builder()
				.securityReferences(defaultAuth())
				.forPaths(PathSelectors.any())
				.build();
	}
	
	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		
		return Lists.newArrayList(new SecurityReference("JWT", authorizationScopes)); 
	}

}