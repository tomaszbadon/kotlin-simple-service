package net.bean.simple.service

import dasniko.testcontainers.keycloak.KeycloakContainer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.test.context.DynamicPropertyRegistry
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestContainersConfiguration {

    @Bean
    @ServiceConnection
    fun mysqlContainer(): MySQLContainer<*> {
        return MySQLContainer(DockerImageName.parse("biarms/mysql:5.7")
                                .asCompatibleSubstituteFor("mysql"))
            .withDatabaseName("sakila")
            .withUsername("sakila")
            .withPassword("sakila")
    }

    @Bean
    fun keycloakContainer(registry: DynamicPropertyRegistry): KeycloakContainer? {
        val keycloak: KeycloakContainer? = KeycloakContainer("quay.io/keycloak/keycloak:24.0.2")
            .withEnv("DB_VENDOR", "h2")
            .withAdminUsername("admin")
            .withAdminPassword("admin")
            .withContextPath("/auth")
            .withRealmImportFile("/simple-application-realm.json")

        registry.add(
            "spring.security.oauth2.resourceserver.jwt.issuer-uri"
        ) { keycloak?.authServerUrl + "/realms/simple-application-realm" }

        registry.add(
            "spring.security.oauth2.client.provider.keycloak.token-uri"
        ) { keycloak?.authServerUrl + "/realms/simple-application-realm/protocol/openid-connect/token" }


        return keycloak
    }
}
