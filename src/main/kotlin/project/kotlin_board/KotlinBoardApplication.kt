package project.kotlin_board

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
class KotlinBoardApplication

fun main(args: Array<String>) {
	runApplication<KotlinBoardApplication>(*args)
}
