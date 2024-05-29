package io.chasen.gateway.handler;

import cn.chasen.rpc.core.api.LoadBalancer;
import cn.chasen.rpc.core.api.RegistryCenter;
import cn.chasen.rpc.core.cluster.RoundRibonLoadBalancer;
import cn.chasen.rpc.core.meta.InstanceMeta;
import cn.chasen.rpc.core.meta.ServiceMeta;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Program: csgateway
 * @Description: hello Handler
 * @Author: Chasen
 * @Create: 2024-05-25 17:47
 **/
@Component
public class GatewayHandler {

    @Autowired
    RegistryCenter rc;

    LoadBalancer<InstanceMeta> loadBalancer = new RoundRibonLoadBalancer<>();

    public Mono<ServerResponse> handler(ServerRequest request) {
        // 1. 通过请求路径或者获取服务名称
        String service = request.path().substring(4);
        System.out.println("service: " + service);
        // app=app1, namespace=public, env=dev, name=cn.chasen.rpc.demo.api.UserService
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app("app1").env("dev").name(service).namespace("public").build();
        // 2. 从注册中心获取服务实例
        List<InstanceMeta> instanceMetas = rc.fetchAll(serviceMeta);
        // 3. 选择一个服务实例进行请求
        InstanceMeta choose = loadBalancer.choose(instanceMetas);
        String url =  choose.toUrl();;
//        String url = instanceMetas.get(0).toUrl();
        System.out.println("url: " + url);
        // 4. 拿到请求报文
        Mono<String> requestMono = request.bodyToMono(String.class);

        return requestMono.flatMap(x -> {
            return invokeFromRegistry(x, url);
        });



    }

    @NotNull
    private static Mono<ServerResponse> invokeFromRegistry(String x, String url) {
        // 5. 通过webclient post  发送请求
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post()
                .header("Content-Type", "application/json")
                .bodyValue(x)
                .retrieve()
                .toEntity(String.class);
        // 6.通过entity获取响报文，并组装响应gateway响应
        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("cs.gw.version", "v1.0.0")
                .body(entity.map(ResponseEntity::getBody), String.class);
    }
}
