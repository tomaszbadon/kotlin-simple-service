package net.bean.simple.service.rest.resource

import dasniko.testcontainers.keycloak.KeycloakContainer
import net.bean.simple.service.CLENT_ID
import net.bean.simple.service.CLIENT_SECRET
import net.bean.simple.service.REALM_NAME
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.AccessTokenResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient


@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractResourceTest {

    @LocalServerPort
    protected var port: Int? = null

    @Autowired
    private var keycloakContainer: KeycloakContainer? = null

    protected var accessToken: AccessTokenResponse? = null

    protected val webClient: WebTestClient? = null

    @BeforeAll
    fun beforeAll() {

        webClient = WebTestClient.bindToServer().baseUrl("http://localhost:$port?").build()

        KeycloakBuilder.builder().serverUrl(keycloakContainer?.authServerUrl)
            .realm(REALM_NAME)
            .clientId(CLENT_ID)
            .clientSecret(CLIENT_SECRET)
            .grantType(OAuth2Constants.PASSWORD)
            .username("test")
            .password("test")
            .build().use {
                accessToken = it.tokenManager().accessToken;
            }
    }

}