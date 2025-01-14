package net.bean.simple.service.model.jpa.entity

import jakarta.persistence.*

@Entity
@Table(name = "film")
class Film: AbstractEntity() {

    @Id
    @GeneratedValue
    @Column(name = "film_id")
    protected var id: Int? = null

}