// (C)2025
package net.bean.simple.service.rest.resource

import net.bean.simple.service.TestContainersConfiguration
import net.bean.simple.service.misc.BEARER
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.test.context.aot.DisabledInAotMode
import org.testcontainers.junit.jupiter.Testcontainers

@DisabledInAotMode
@Testcontainers
@Import(TestContainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmResourceTest : AbstractResourceTest() {
    @Test
    @DisplayName("Authorisation Test With Token - Role ApplicationUser needed")
    fun authorisationWithApplicationRoleTest() {
        val moviesInfo =
            webClient
                .get()
                .uri { builder ->
                    builder
                        .path("/api/v1/movies")
                        .queryParam("limit", 1)
                        .queryParam("offset", 0)
                        .queryParam("sortBy", "asc(id)")
                        .build()
                }.header(HttpHeaders.AUTHORIZATION, "$BEARER${accessToken?.token}")
                .exchange()
                .expectStatus()
                .isOk
                .expectBody()
                .jsonPath("movies[0].id")
                .isEqualTo(1)
                .jsonPath("movies[0].title")
                .isEqualTo("ACADEMY DINOSAUR")
                .jsonPath(
                    "movies[0].description",
                ).isEqualTo(
                    "A Epic Drama of a Feminist And a Mad Scientist who must Battle a Teacher in The Canadian Rockies",
                )
        assertThat(moviesInfo).isNotNull
    }
}
