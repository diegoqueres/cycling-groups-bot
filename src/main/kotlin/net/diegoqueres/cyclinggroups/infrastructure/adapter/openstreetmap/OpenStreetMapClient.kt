package net.diegoqueres.cyclinggroups.infrastructure.adapter.openstreetmap

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request

class OpenStreetMapClient {
    private val client = OkHttpClient()
    private val objectMapper = ObjectMapper()

    fun getCityFromCoordinates(latitude: Double, longitude: Double): String? {
        val url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$latitude&lon=$longitude"
        val request = Request.Builder()
            .url(url)
            .header("accept-language", "pt-BR")
            .build()

        val response = client.newCall(request).execute()
        val jsonData = response.body?.string()

        return if (!jsonData.isNullOrBlank()) {
            val jsonNode = objectMapper.readTree(jsonData)

            val addressNode = jsonNode.get("address")
            val address = objectMapper.treeToValue(addressNode, Address::class.java)

            address?.city ?: address?.town ?: address?.village
        } else {
            null
        }
    }

    fun getCoordinatesFromCity(city: String): Pair<Double, Double>? {
        val url = "https://nominatim.openstreetmap.org/search?format=json&q=${city.replace(" ", "+")}&accept-language=pt-BR&limit=1"
        val request = Request.Builder()
            .url(url)
            .header("accept-language", "pt-BR")
            .build()

        val response = client.newCall(request).execute()
        val jsonData = response.body?.string()

        if (!jsonData.isNullOrBlank()) {
            val addresses = objectMapper.readValue(jsonData, Array<Address>::class.java)
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val latitude = address.lat?.toDoubleOrNull()
                val longitude = address.lon?.toDoubleOrNull()
                if (latitude != null && longitude != null) {
                    return Pair(latitude, longitude)
                }
            }
        }
        return null
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Address(
        val city: String?,
        val town: String?,
        val village: String?,
        val lat: String?,
        val lon: String?
    ) {
        constructor() : this(null, null, null, null, null)
    }

}