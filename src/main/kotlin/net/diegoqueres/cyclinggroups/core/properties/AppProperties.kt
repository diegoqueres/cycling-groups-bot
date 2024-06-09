package net.diegoqueres.cyclinggroups.core.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.util.*

@ConfigurationProperties("app")
data class AppProperties (
    val supportedLocales: List<String>,
    val findGroups: FindGroups
) {

    fun supportedLocales(): List<Locale> {
        return supportedLocales.map { localeString ->
            val parts = localeString.split("-")
            if (parts.size > 1) {
                Locale(parts[0], parts[1])
            } else {
                Locale(parts[0])
            }
        }
    }

}

data class FindGroups (
    val resultsDistance: Double
)