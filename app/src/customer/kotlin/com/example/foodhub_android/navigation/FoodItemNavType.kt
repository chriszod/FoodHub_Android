package com.example.foodhub_android.navigation

import androidx.navigation.NavType
import com.example.foodhub_android.data.models.FoodItem
import kotlinx.serialization.json.Json

val foodItemNavType = object : NavType<FoodItem>(false) {
    override fun get(
        bundle: android.os.Bundle,
        key: String
    ): FoodItem? {
        return parseValue(bundle.getString(key).toString()).copy(
            imageUrl = java.net.URLDecoder.decode(
                parseValue(bundle.getString(key).toString()).imageUrl,
                "UTF-8"
            )
        )
    }

    override fun parseValue(value: String): FoodItem {
        return Json.decodeFromString(FoodItem.serializer(), value)
    }

    override fun serializeAsValue(value: FoodItem): String {
        return Json.encodeToString(
            FoodItem.serializer(), value.copy(
                imageUrl = java.net.URLEncoder.encode(value.imageUrl, "UTF-8"),
            )
        )
    }

    override fun put(
        bundle: android.os.Bundle,
        key: String,
        value: FoodItem
    ) {
        bundle.putString(key, serializeAsValue(value))
    }
}