package net.bean.simple.service

import dasniko.testcontainers.keycloak.KeycloakContainer
import org.slf4j.LoggerFactory
import org.springframework.boot.devtools.restart.RestartScope
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.utility.DockerImageName

val REALM_NAME = "simple-application-realm"
val CLENT_ID = "simple-microservice"
val CLIENT_SECRET = "Y1JUH3DVEeZNXKz9UsRH3Y3SyOLAtkNb"

@TestConfiguration(proxyBeanMethods = false)
class TestContainersConfiguration {

    private val logger = LoggerFactory.getLogger(TestContainersConfiguration::class.java)

    @Bean
    @ServiceConnection
    @RestartScope
    fun postgreslContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("sakila")
            .withUsername("postgres")
            .withPassword("postgres")
            .withLogConsumer(Slf4jLogConsumer(logger))
    }

    @Bean
//    @RestartScope
    fun keycloakContainer(registry: DynamicPropertyRegistry): KeycloakContainer? {

        val keycloak: KeycloakContainer? = KeycloakContainer("quay.io/keycloak/keycloak:24.0.2")
            .withEnv("DB_VENDOR", "h2")
            .withAdminUsername("admin")
            .withAdminPassword("admin")
            .withContextPath("/auth")
            .withRealmImportFile("/simple-application-realm.json")
            .withLogConsumer(Slf4jLogConsumer(logger))

        registry.add(
            "spring.security.oauth2.resourceserver.jwt.issuer-uri"
        ) { keycloak?.authServerUrl + "/realms/simple-application-realm" }

        registry.add(
            "spring.security.oauth2.client.provider.keycloak.token-uri"
        ) { keycloak?.authServerUrl + "/realms/simple-application-realm/protocol/openid-connect/token" }

        registry.add(
            "spring.security.oauth2.client.registration.keycloak.client-id"
        ) { CLENT_ID }

        registry.add(
            "spring.security.oauth2.client.registration.keycloak.client-secret"
        ) { CLIENT_SECRET }

        return keycloak
    }

}
