package io.chasen.gateway.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @Program: csgateway
 * @Description: hello Handler
 * @Author: Chasen
 * @Create: 2024-05-25 17:47
 **/
@Component
public class HelloHandler {

    public Mono<ServerResponse> handler(ServerRequest request) {

        String url = "http://127.0.0.1:8081/rpc";
        String requestJSON = """
               {
                   "service": "cn.chasen.rpc.demo.api.UserService",
                   "methodSign": "findById@1_int",
                   "args":[100]
               }
               """;
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .bodyValue(requestJSON)
                .retrieve()
                .toEntity(String.class);
        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("cs.gw.version", "v1.0.0")
                .body(entity.map(ResponseEntity::getBody), String.class);
    }
}
