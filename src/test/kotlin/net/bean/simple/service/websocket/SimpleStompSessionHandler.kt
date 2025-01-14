package net.bean.simple.service.websocket

import net.bean.simple.service.model.GreetingInfo
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandler
import java.lang.reflect.Type


class SimpleStompSessionHandler: StompSessionHandler {

    override fun getPayloadType(headers: StompHeaders): Type {
        return GreetingInfo::class.java
    }

    override fun handleFrame(headers: StompHeaders, payload: Any?) {
    }

    override fun afterConnected(session: StompSession, connectedHeaders: StompHeaders) {

    }

    override fun handleException(
        session: StompSession,
        command: StompCommand?,
        headers: StompHeaders,
        payload: ByteArray,
        exception: Throwable
    ) {

    }

    override fun handleTransportError(session: StompSession, exception: Throwable) {

    }
}