// (C)2025
package net.bean.simple.service.service

import net.bean.simple.service.model.jpa.entity.Film
import net.bean.simple.service.repository.FilmRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class MovieService(
    val filmRepository: FilmRepository,
) {
    fun getMovies(
        offset: Int?,
        limit: Int?,
        sortBy: String?,
    ): Page<Film> = filmRepository.getPage(offset, limit, sortBy)
}
