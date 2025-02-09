// (C)2025
package net.bean.simple.service.websocket.controller

import net.bean.simple.service.model.GreetingInfo
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

/** @property simpMessagingTemplate */
@Controller
class WebSocketController(
    val simpMessagingTemplate: SimpMessagingTemplate,
) {
    /** @param message */
    @MessageMapping("/hello")
    fun messageHandler(message: String?) {
        simpMessagingTemplate.convertAndSend("/topic/messages", GreetingInfo(message))
    }
}
