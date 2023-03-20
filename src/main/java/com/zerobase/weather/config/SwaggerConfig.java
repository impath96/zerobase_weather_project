package com.zerobase.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    // apis(RequestHandlerSelectors.any())를 할 경우
    // swagger-ui 에서 baisc-error-controller가 나오게 된다.
    // 이때 이 controller는 스프링 부트에서 기본적으로 제공하는 controller라고 보면 된다.
    // 이것을 없애주기 위해서 아래와 같이 기본 패키지 위치를 지정해서 내가 만든 controller에 대해서만
    // 보이도록 할 수 있다.

    // paths(PathSelectors)의 경우 내가 만든 controller에 해당하는 mapping 을
    // 노출시키기 싶은 부분을 노출할 수 있도록 설정할 수 있다.
    // PathSelectors.ant()의 경우 ant 패턴으로 특정 패턴의 api 호출 경로를 넣을 수 있다.
    //paths(PathSelectors.ant("/read/**")) : /read로 시작하는 경로를 모두 보여주겠다!
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zerobase.weather"))
                .paths(PathSelectors.any()) // /read로 시작하는 경로를 모두 보여주겠다!
                .build().apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        String description = "날씨 일기를 CRUD 할 수 있는 백엔드 API 입니다.";
        return new ApiInfoBuilder()
                .title("날씨 일기 프로젝트")
                .description(description)
                .version("1.0")
                .build();
    }


}
