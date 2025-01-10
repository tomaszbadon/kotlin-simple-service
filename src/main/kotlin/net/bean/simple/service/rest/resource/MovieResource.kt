package net.bean.simple.service.rest.resource

import net.bean.simple.service.rest.model.MovieInfo
import net.bean.simple.service.rest.model.MoviesInfo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/movies")
class MovieResource {

    @GetMapping
    fun getMovies(): ResponseEntity<MoviesInfo> {
        return ResponseEntity.ok(MoviesInfo(listOf(MovieInfo(1, "Titanic", "A story about a ship"))));
    }

}