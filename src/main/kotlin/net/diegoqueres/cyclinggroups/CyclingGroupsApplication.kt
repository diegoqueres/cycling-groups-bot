package net.diegoqueres.cyclinggroups

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CyclingGroupsApplication

fun main(args: Array<String>) {
	runApplication<CyclingGroupsApplication>(*args)
}