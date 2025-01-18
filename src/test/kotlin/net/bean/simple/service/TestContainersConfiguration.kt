package net.bean.simple.service

import dasniko.testcontainers.keycloak.KeycloakContainer
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.boot.devtools.restart.RestartScope
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.utility.DockerImageName
import org.testcontainers.junit.jupiter.Testcontainers

val REALM_NAME = "simple-application-realm"
val CLENT_ID = "simple-microservice"
val CLIENT_SECRET = "Y1JUH3DVEeZNXKz9UsRH3Y3SyOLAtkNb"

@TestConfiguration(proxyBeanMethods = false)
@Testcontainers
@ContextConfiguration(classes = [TestContainersConfiguration::class])
@ExtendWith(SpringExtension::class)
class TestContainersConfiguration {

    private val logger = LoggerFactory.getLogger(TestContainersConfiguration::class.java)

    companion object {

        private val logger = LoggerFactory.getLogger(TestContainersConfiguration::class.java)

        val postgeesql = PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("sakila")
            .withUsername("postgres")
            .withPassword("postgres")
            .withLogConsumer(Slf4jLogConsumer(logger))

        val keycloak: KeycloakContainer? = KeycloakContainer("quay.io/keycloak/keycloak:24.0.2")
            .withEnv("DB_VENDOR", "h2")
            .withAdminUsername("admin")
            .withAdminPassword("admin")
            .withContextPath("/auth")
            .withRealmImportFile("/simple-application-realm.json")
            .withLogConsumer(Slf4jLogConsumer(logger))

        init {
            keycloak?.start();

            System.setProperty(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                keycloak?.authServerUrl + "/realms/simple-application-realm"
            )

            System.setProperty(
                "spring.security.oauth2.client.provider.keycloak.token-uri",
                keycloak?.authServerUrl + "/realms/simple-application-realm/protocol/openid-connect/token"
            )

            System.setProperty(
                "spring.security.oauth2.client.registration.keycloak.client-id", CLENT_ID
            )

            System.setProperty(
                "spring.security.oauth2.client.registration.keycloak.client-secret", CLIENT_SECRET
            )
        }

    }

    @Bean
    @ServiceConnection
    @RestartScope
    fun postgreslContainer(): PostgreSQLContainer<*> {
        return postgeesql
    }

    @Bean
    @RestartScope
    fun keycloakContainer(registry: DynamicPropertyRegistry): KeycloakContainer? {
        return keycloak
    }

}
