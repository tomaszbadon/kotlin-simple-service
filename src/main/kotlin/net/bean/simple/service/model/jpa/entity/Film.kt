package net.bean.simple.service.model.jpa.entity

import jakarta.persistence.*

@Entity
@Table(name = "film")
class Film: AbstractEntity() {

    @Id
    @GeneratedValue
    @Column(name = "film_id")
    var id: Int? = null

    @Column(name = "title")
    var title: String? = null

    @Column(name = "description")
    var description: String? = null


}