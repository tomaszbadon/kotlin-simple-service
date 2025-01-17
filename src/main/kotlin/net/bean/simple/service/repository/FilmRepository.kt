package net.bean.simple.service.repository

import net.bean.simple.service.model.jpa.entity.Film
import net.bean.simple.service.repository.jpa.FilmJpaRepository
import org.springframework.stereotype.Component

@Component
class FilmRepository(filmJpaRepository: FilmJpaRepository) : GenericRepository<Film, FilmJpaRepository>(filmJpaRepository, Film::class) {



}