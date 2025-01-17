package net.bean.simple.service.repository

import net.bean.simple.service.model.jpa.entity.AbstractEntity
import org.springframework.data.jpa.repository.JpaRepository
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass

abstract class GenericRepository<T : AbstractEntity, R : JpaRepository<T, Int>>(val jpaRepository: R, val type: KClass<T>) {

    fun getById(id: Int): T {
        return jpaRepository.findById(id).getOrNull() ?: throw RuntimeException("Ble Ble Ble")
    }

}