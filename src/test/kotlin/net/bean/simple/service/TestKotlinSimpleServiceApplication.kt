package net.bean.simple.service

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
	fromApplication<KotlinSimpleServiceApplication>().with(TestContainersConfiguration::class).run(*args)
}
