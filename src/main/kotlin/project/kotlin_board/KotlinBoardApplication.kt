package project.kotlin_board

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class KotlinBoardApplication

fun main(args: Array<String>) {
	runApplication<KotlinBoardApplication>(*args)
}
