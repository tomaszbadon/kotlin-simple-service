package net.bean.simple.service.rest.resource

import net.bean.simple.service.repository.FilmRepository
import net.bean.simple.service.rest.model.MovieInfo
import net.bean.simple.service.rest.model.MoviesInfo
import net.bean.simple.service.service.MovieService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/movies")
class MovieResource(val movieService: MovieService) {

    @GetMapping
    fun getMovies(offset: Int?, limit: Int?, sortBy: String?): ResponseEntity<MoviesInfo> {

        val movies = movieService.getMovies(offset, limit, sortBy);

        return ResponseEntity.ok(MoviesInfo(movies.toList().map { MovieInfo(it.id, it.title, it.title) }))
    }

}