package com.server.models

import kotlinx.serialization.*

@Serializable
data class ClientAnswerElement (
    val type : String,
    val operand : ClientAnswerElement?,
    val secondOperand : String?,
    val operator : String?,
    val next : ClientAnswerElement?
)