package gr.pkcoding.notemarkapp.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using Navigation 3.0
 */

// Root navigation graphs
@Serializable
object AuthGraph

@Serializable
object MainGraph

// Authentication flow routes
@Serializable
object LandingRoute

@Serializable
data class LoginRoute(
    val fromRegister: Boolean = false,
    val prefillEmail: String? = null
)

@Serializable
data class RegisterRoute(
    val fromLogin: Boolean = false
)

// Main application routes
@Serializable
object HomeRoute

@Serializable
data class ProfileRoute(
    val userId: String? = null
)