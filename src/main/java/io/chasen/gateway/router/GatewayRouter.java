package io.chasen.gateway.router;

import io.chasen.gateway.handler.GatewayHandler;
import io.chasen.gateway.handler.HelloHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @Program: csgateway
 * @Description: gateway router
 * @Author: Chasen
 * @Create: 2024-05-25 17:37
 **/
@Component
public class GatewayRouter {

    @Autowired
    private HelloHandler helloHandler;

    @Autowired
    private GatewayHandler gatewayHandler;

    @Bean
    public RouterFunction<?> helloRouterFunction() {
//        return route(GET("/hello"),
//                request -> ServerResponse.ok()
//                        .body(Mono.just("hello,gateway."), String.class)
//        );
        return route(GET("/hello"), helloHandler::handler);
    }

    @Bean
    public RouterFunction<?> gatewayRouterFunction() {
        return route(GET("/gw").or(POST("/gw/**")), gatewayHandler::handler);
    }


    @Bean
    public RouterFunction<?> gatewayWebRouterFunction() {
        return route(GET("/ga").or(POST("/gw/ga")), gatewayHandler::handler);
    }
}
