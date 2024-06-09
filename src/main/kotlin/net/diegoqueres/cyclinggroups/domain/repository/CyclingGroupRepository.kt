package net.diegoqueres.cyclinggroups.domain.repository

import net.diegoqueres.cyclinggroups.domain.model.CyclingGroup
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.repository.MongoRepository


interface CyclingGroupRepository : MongoRepository<CyclingGroup, String> {
    fun findByPositionNear(p: Point, d: Distance): List<CyclingGroup>
}