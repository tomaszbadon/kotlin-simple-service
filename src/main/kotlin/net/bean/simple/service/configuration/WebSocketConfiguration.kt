package net.bean.simple.service.configuration

import net.bean.simple.service.websocket.interceptor.AuthInterceptor
import net.bean.simple.service.websocket.interceptor.CsrfNoopInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
class WebSocketConfiguration(val csrfNoopInterceptor: CsrfNoopInterceptor, val authInterceptor: AuthInterceptor) :
    WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic")
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/api/websocket").setAllowedOrigins("*")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(csrfNoopInterceptor, authInterceptor)
    }

}