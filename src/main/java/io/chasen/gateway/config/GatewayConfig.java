package io.chasen.gateway.config;

import cn.chasen.rpc.core.api.RegistryCenter;
import cn.chasen.rpc.core.registry.ck.ChasenRegistryCenter;
import io.chasen.gateway.plugin.GatewayPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Properties;

/**
 * @Program: csgateway
 * @Description: gateway config
 * @Author: Chasen
 * @Create: 2024-05-25 18:51
 **/
@Configuration
public class GatewayConfig {

    @Bean
    public RegistryCenter rc() {
        return new ChasenRegistryCenter();
    }

    @Bean
    public ApplicationRunner runner(@Autowired ApplicationContext context) {
        return args -> {
            SimpleUrlHandlerMapping handlerMapping = context.getBean(SimpleUrlHandlerMapping.class);
            Properties mappings = new Properties();
            mappings.put(GatewayPlugin.GATEWAY_PREFIX + "/**", "gatewayWebHandler");
            handlerMapping.setMappings(mappings);
            handlerMapping.initApplicationContext();
        };
    }
}
