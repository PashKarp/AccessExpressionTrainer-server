package com.server.models

import its.reasoner.LearningSituation
import kotlinx.serialization.Serializable

@Serializable
class OutStatus (
    val status : Boolean,
    val message : String?,
)