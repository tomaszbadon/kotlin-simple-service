// (C)2025
package net.bean.simple.service.rest.resource

import dasniko.testcontainers.keycloak.KeycloakContainer
import io.netty.handler.ssl.SslContext
import io.netty.handler.ssl.SslContextBuilder
import net.bean.simple.service.CLENT_ID
import net.bean.simple.service.CLIENT_SECRET
import net.bean.simple.service.REALM_NAME
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Timeout
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.AccessTokenResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.netty.http.client.HttpClient
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory

@Timeout(value = 10, unit = TimeUnit.MINUTES)
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractResourceTest {
    @LocalServerPort protected var port: Int? = null

    @Autowired private var keycloakContainer: KeycloakContainer? = null

    protected var accessToken: AccessTokenResponse? = null

    private var _webClient: WebTestClient? = null

    val webClient: WebTestClient
        get() =
            _webClient
                ?: throw RuntimeException("WebTestClient was not initialized in beforeAll() method")

    @BeforeAll
    fun beforeAll() {
        val trustStorePath: Path =
            Paths.get(
                "/Users/tomaszbadon/git/kotlin-simple-service/dev-tools/certs/client-truststore.p12",
            )
        val trustStoreStream: InputStream =
            Files.newInputStream(trustStorePath, StandardOpenOption.READ)
        val trustStore = KeyStore.getInstance("PKCS12")
        trustStore.load(trustStoreStream, "changeit".toCharArray())
        val trustManagerFactoryAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val trustManagerFactory = TrustManagerFactory.getInstance(trustManagerFactoryAlgorithm)
        trustManagerFactory.init(trustStore)
        trustStoreStream.close()

        val identityPath: Path =
            Paths.get("/Users/tomaszbadon/git/kotlin-simple-service/dev-tools/certs/client.p12")
        val identityStream: InputStream = Files.newInputStream(identityPath, StandardOpenOption.READ)
        val identity = KeyStore.getInstance("PKCS12")
        identity.load(identityStream, "changeit".toCharArray())

        val keyManagerFactoryAlgorithm = KeyManagerFactory.getDefaultAlgorithm()
        val keyManagerFactory = KeyManagerFactory.getInstance(keyManagerFactoryAlgorithm)
        keyManagerFactory.init(identity, "changeit".toCharArray())
        identityStream.close()

        val sslContext: SslContext =
            SslContextBuilder
                .forClient()
                .keyManager(keyManagerFactory)
                .trustManager(trustManagerFactory)
                .build()
        val httpClient =
            HttpClient.create().secure { sslContextSpec -> sslContextSpec.sslContext(sslContext) }

        _webClient =
            WebTestClient
                .bindToServer(ReactorClientHttpConnector(httpClient))
                .baseUrl("https://localhost:$port")
                .build()

        KeycloakBuilder
            .builder()
            .serverUrl(keycloakContainer?.authServerUrl)
            .realm(REALM_NAME)
            .clientId(CLENT_ID)
            .clientSecret(CLIENT_SECRET)
            .grantType(OAuth2Constants.PASSWORD)
            .username("test")
            .password("test")
            .build()
            .use { accessToken = it.tokenManager().accessToken }
    }
}
