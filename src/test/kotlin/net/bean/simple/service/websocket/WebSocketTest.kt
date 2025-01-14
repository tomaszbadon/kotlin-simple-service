package net.bean.simple.service.websocket

import net.bean.simple.service.TestContainersConfiguration
import net.bean.simple.service.model.GreetingInfo
import net.bean.simple.service.rest.resource.AbstractResourceTest
import net.bean.simple.service.websocket.controller.WebSocketController
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Import
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.ConnectionLostException
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.test.context.aot.DisabledInAotMode
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.testcontainers.junit.jupiter.Testcontainers
import java.text.MessageFormat
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.stream.Stream
import kotlin.test.Test

@DisabledInAotMode
@Testcontainers
@Import(TestContainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketTest : AbstractResourceTest() {

    @SpyBean
    private val webSocketController: WebSocketController? = null

    //@Spy
    private var stompSessionHandler: SimpleStompSessionHandler? = null

    private val converter: MappingJackson2MessageConverter = MappingJackson2MessageConverter()

    private val URI: String = "ws://localhost:%s/api/websocket"

    private var client: WebSocketClient? = null

    private var stompClient: WebSocketStompClient? = null

    @BeforeEach
    fun beforeEach() {
        client = StandardWebSocketClient()
        stompClient = WebSocketStompClient(client!!)
        stompClient?.messageConverter = converter
        stompSessionHandler = spy(SimpleStompSessionHandler())
    }

    @ParameterizedTest
    @MethodSource("headers")
    fun webSocketSecurityTest(httpHeaders: WebSocketHttpHeaders?, stompHeaders: StompHeaders?, stringPayload: String) {
        var session: StompSession? = null
        try {
            val future = stompClient!!.connectAsync(
                String.format(URI, port), httpHeaders, stompHeaders,
                stompSessionHandler!!
            )
            session = future.get(10, TimeUnit.SECONDS)
            session.subscribe("/topic/messages", stompSessionHandler!!)
            session.send("/app/hello", stringPayload)

            Thread.sleep(1000)

            verify(webSocketController, times(1))?.messageHandler(eq(stringPayload))
            verify(stompSessionHandler)?.handleFrame(any<StompHeaders>(), eq(GreetingInfo(stringPayload)))
            verify(stompSessionHandler, never())?.handleException(any(), any(), any(), any(), any())
            verify(stompSessionHandler, never())?.handleTransportError(any(), any())

        } finally {
            if (session != null && session.isConnected) {
                session.disconnect()
            }
        }
    }

    @Test
    fun authorisationTest() {
        assertThatThrownBy {
            val future = stompClient!!.connectAsync(
                String.format(URI, port),
                stompSessionHandler!!
            )
            //val session = future.get(10, TimeUnit.SECONDS)
        }.isInstanceOf(ExecutionException::class.java).rootCause().isInstanceOf(ConnectionLostException::class.java)
            .hasMessageContaining("Connection closed")
    }

    private fun headers(): Stream<Arguments> {
        val httpHeaders = WebSocketHttpHeaders()
        httpHeaders.add(WebSocketHttpHeaders.AUTHORIZATION, MessageFormat.format("Bearer {0}", accessToken?.token))

        val stompHeaders = StompHeaders()
        stompHeaders.add(WebSocketHttpHeaders.AUTHORIZATION, MessageFormat.format("Bearer {0}", accessToken?.token))

        return Stream.of(
            Arguments.of(httpHeaders, StompHeaders(), "PAYLOAD_1"),
            Arguments.of(WebSocketHttpHeaders(), stompHeaders, "PAYLOAD_2"))
    }

}