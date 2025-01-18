package net.bean.simple.service.rest.resource

import net.bean.simple.service.repository.FilmRepository
import net.bean.simple.service.rest.model.MovieInfo
import net.bean.simple.service.rest.model.MoviesInfo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/movies")
class MovieResource(val filmRepository: FilmRepository) {

    @GetMapping
    fun getMovies(offset: Int?, limit: Int?, sortBy: String?): ResponseEntity<MoviesInfo> {

        val film = filmRepository.getById(2)

        return ResponseEntity.ok(MoviesInfo(listOf(MovieInfo(film.id, film.title, film.title))))
    }

}