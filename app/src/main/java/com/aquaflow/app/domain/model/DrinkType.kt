package com.aquaflow.app.domain.model

enum class DrinkType(val hydrationFactor: Float, val displayName: String) {
    WATER(1.0f, "Eau"),
    COFFEE(0.8f, "Café"),
    TEA(0.9f, "Thé"),
    SODA(0.6f, "Soda"),
    JUICE(0.7f, "Jus de fruits"),
    MILK(0.9f, "Lait"),
    SPORTS_DRINK(0.85f, "Boisson sportive");
    
    companion object {
        fun fromName(name: String): DrinkType {
            return try {
                valueOf(name)
            } catch (e: Exception) {
                WATER
            }
        }
    }
}
