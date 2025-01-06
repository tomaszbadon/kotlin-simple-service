package net.bean.simple.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinSimpleServiceApplication

fun main(args: Array<String>) {
	runApplication<KotlinSimpleServiceApplication>(*args)
}
