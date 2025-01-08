package net.bean.simple.service.rest.resource

import net.bean.simple.service.TestContainersConfiguration
import net.bean.simple.service.misc.BEARER
import net.bean.simple.service.rest.model.MoviesInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.*
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@Import(TestContainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MovieResourceTest : AbstractResourceTest() {

    @Autowired
    private val template: TestRestTemplate? = null


    @Test
    @DisplayName("Authorisation Test With Token - Role ApplicationUser needed")
    fun authorisationWithApplicationRoleTest() {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "$BEARER${accessToken?.token}")
        val response: ResponseEntity<MoviesInfo>? = template?.exchange<MoviesInfo>(
            "/api/v1/movies", HttpMethod.GET, HttpEntity<Any>(httpHeaders),
            MoviesInfo::class.java
        )
        assertThat(response?.statusCode).isEqualTo(HttpStatusCode.valueOf(200))
    }

}