package net.bean.simple.service.repository.jpa

import net.bean.simple.service.model.jpa.entity.Film
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FilmJpaRepository : JpaRepository<Film, Int>