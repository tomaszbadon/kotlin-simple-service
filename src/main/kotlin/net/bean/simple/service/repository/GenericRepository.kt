package net.bean.simple.service.repository

import net.bean.simple.service.misc.SortingDirection
import net.bean.simple.service.model.jpa.entity.AbstractEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass

abstract class GenericRepository<T : AbstractEntity, R : JpaRepository<T, Int>>(protected val jpaRepository: R, protected val type: KClass<T>) {

    fun getById(id: Int): T {
        return jpaRepository.findById(id).getOrNull() ?: throw RuntimeException("Ble Ble Ble")
    }

    fun getPage(offset: Int?, limit: Int?, sortBy: String?): Page<T> {
        val sort = if (SortingDirection.ASC == SortingDirection.ASC)
            Sort.by(sortBy ?: "id").ascending()
        else
            Sort.by(sortBy ?: "id").descending()
        val pageRequest = PageRequest.of(offset ?: 0, limit ?: 20, sort);
        return jpaRepository.findAll(pageRequest);
    }

}