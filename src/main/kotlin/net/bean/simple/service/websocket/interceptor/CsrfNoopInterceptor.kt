package net.bean.simple.service.websocket.interceptor

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component

@Component("csrfChannelInterceptor")
class CsrfNoopInterceptor : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        return message
    }

}

// https://stackoverflow.com/questions/75068726/setting-up-csrf-for-spring-websocket