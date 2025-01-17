package net.bean.simple.service.websocket.controller

import net.bean.simple.service.model.GreetingInfo
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class WebSocketController(val simpMessagingTemplate: SimpMessagingTemplate) {

    @MessageMapping("/hello")
    fun messageHandler(message: String?) {
        simpMessagingTemplate.convertAndSend("/topic/messages", GreetingInfo(message))
    }

}