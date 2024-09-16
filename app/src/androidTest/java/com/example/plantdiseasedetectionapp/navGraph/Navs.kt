package com.example.plantdiseasedetectionapp.navGraph

import kotlinx.serialization.Serializable

sealed class Navs {

    @Serializable
    object Homescreen

    @Serializable
    object Loginscreen

    @Serializable
    object Signupscreen

    @Serializable
    object Curescreen


}