// (C)2025
package net.bean.simple.service.repository

import net.bean.simple.service.misc.SortingDirection
import net.bean.simple.service.model.jpa.entity.AbstractEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass

abstract class GenericRepository<T : AbstractEntity, R : JpaRepository<T, Int>>(
    protected val jpaRepository: R,
    protected val type: KClass<T>,
) {
    fun getById(id: Int): T = jpaRepository.findById(id).getOrNull() ?: throw RuntimeException("Ble Ble Ble")

    fun getPage(
        offset: Int?,
        limit: Int?,
        sortBy: String?,
    ): Page<T> {
        val result = Regex("^(asc|desc)\\((.*)\\)\$").find(sortBy?.lowercase() ?: "asc(id)")
        val direction = result?.groups?.get(1)?.value ?: "asc"
        val property = result?.groups?.get(2)?.value ?: "id"

        val sort =
            if (SortingDirection.ASC.name
                    .lowercase()
                    .equals(direction)
            ) {
                Sort.by(property).ascending()
            } else {
                Sort.by(property).descending()
            }
        val pageRequest = PageRequest.of(offset ?: 0, limit ?: 20, sort)
        return jpaRepository.findAll(pageRequest)
    }
}
