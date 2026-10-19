package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ProductLookupResult(
    val barcode: String,
    val productName: String,
    val brand: String? = null,
    val strengthOrQuantity: String? = null
)

class BarcodeLookupService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(3500, TimeUnit.MILLISECONDS)
        .readTimeout(3500, TimeUnit.MILLISECONDS)
        .build()

    /**
     * Söker efter produktinformation via EAN-kod i öppna produktregister.
     * Körs asynkront och säkert i bakgrunden utan att blockera gränssnittet.
     */
    suspend fun lookupBarcode(barcode: String): ProductLookupResult? = withContext(Dispatchers.IO) {
        val cleanBarcode = barcode.trim()
        if (cleanBarcode.length < 8) return@withContext null

        // Försök först i Open Products Facts, därefter Open Food Facts
        val endpoints = listOf(
            "https://world.openproductsfacts.org/api/v2/product/$cleanBarcode.json",
            "https://world.openfoodfacts.org/api/v2/product/$cleanBarcode.json"
        )

        for (url in endpoints) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "MedicinskapetAndroidApp/1.2")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: return@use
                        val json = JSONObject(body)
                        val status = json.optInt("status", 0)

                        if (status == 1 && json.has("product")) {
                            val product = json.getJSONObject("product")

                            // Prioritera svenskt produktnamn om det finns
                            val name = product.optString("product_name_sv").ifBlank {
                                product.optString("product_name").ifBlank {
                                    product.optString("generic_name_sv").ifBlank {
                                        product.optString("generic_name")
                                    }
                                }
                            }

                            val brand = product.optString("brands").ifBlank { null }
                            val quantity = product.optString("quantity").ifBlank { null }

                            if (name.isNotBlank()) {
                                return@withContext ProductLookupResult(
                                    barcode = cleanBarcode,
                                    productName = name.trim(),
                                    brand = brand?.trim(),
                                    strengthOrQuantity = quantity?.trim()
                                )
                            }
                        }
                    }
                }
            } catch (_: Exception) {
                // Vid nätverksfel eller timeout fortsätter loopen eller returnerar null
            }
        }

        null
    }
}
