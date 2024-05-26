package io.chasen.gateway.config;

import cn.chasen.rpc.core.api.RegistryCenter;
import cn.chasen.rpc.core.registry.ck.ChasenRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
