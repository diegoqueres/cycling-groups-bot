package net.diegoqueres.cyclinggroups.domain.service

import net.diegoqueres.cyclinggroups.core.properties.AppProperties
import net.diegoqueres.cyclinggroups.domain.model.CyclingGroup
import net.diegoqueres.cyclinggroups.domain.repository.CyclingGroupRepository
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.stereotype.Service


@Service
class CyclingGroupService(
    private val appProperties: AppProperties,
    private val cyclingGroupRepository: CyclingGroupRepository
) {

    fun save(cyclingGroup: CyclingGroup): CyclingGroup {
        return cyclingGroupRepository.save(cyclingGroup)
    }

    fun findByPositionNear(longitude: Double, latitude: Double): List<CyclingGroup> {
        val point = Point(longitude, latitude)
        val distance = Distance(appProperties.findGroups.resultsDistance, Metrics.KILOMETERS)
        return cyclingGroupRepository.findByPositionNear(point, distance)
    }

}