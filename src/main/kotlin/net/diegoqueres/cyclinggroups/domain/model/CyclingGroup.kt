package net.diegoqueres.cyclinggroups.domain.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable

@Document(collection = "CyclingGroups")
data class CyclingGroup(
    @Id val id: String? = null
) : Serializable {
    @NotBlank @Size(min = 3, max=32) var name: String? = null
    @NotBlank @Size(min = 3, max=256) var description: String? = null
    @NotBlank @Size(min = 3, max=256) var socialProfileUrl: String? = null
    @NotNull @GeoSpatialIndexed(name = "position", type = GeoSpatialIndexType.GEO_2DSPHERE) var position: GeoJsonPoint? = null

    constructor(id: String? = null, name: String? = null, description: String? = null) : this(id) {
        this.name = name
        this.description = description
    }

}