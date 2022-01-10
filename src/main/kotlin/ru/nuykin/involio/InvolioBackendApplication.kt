package ru.nuykin.involio

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import ru.nuykin.involio.model.MyUser

@EntityScan("ru.nuykin.involio.model")
@SpringBootApplication
class InvolioBackendApplication

fun main(args: Array<String>) {
	runApplication<InvolioBackendApplication>(*args)
}
