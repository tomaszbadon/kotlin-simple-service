package net.bean.simple.service.websocket.interceptor

import net.bean.simple.service.misc.BEARER
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver
import org.springframework.stereotype.Component
import java.util.*

@Component
class AuthInterceptor(val jwtIssuerAuthenticationManagerResolver: JwtIssuerAuthenticationManagerResolver): ChannelInterceptor {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(
            message,
            StompHeaderAccessor::class.java
        )

        if (Objects.isNull(accessor) || StompCommand.CONNECT != accessor!!.command) {
            return message
        }
        try {
            //Extract JWT token from header, validate it and extract user authorities
            val token = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION)
            if (Objects.isNull(token) || token!!.length < BEARER.length) {
                return message
            }

            // HTTP Servlet request is not used so it is safe for it to be null.
            val authManager: AuthenticationManager = jwtIssuerAuthenticationManagerResolver.resolve(null)

            val bearerToken = BearerTokenAuthenticationToken(token.substring(BEARER.length))
            val user = authManager.authenticate(bearerToken)
            accessor.user = user
        } catch (e: Exception) {
            log.error("Error verifying JWT token on STOMP CONNECT message", e)
        }

        return message

    }

}