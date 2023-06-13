package com.server.models

import kotlinx.serialization.Serializable

@Serializable
class TaskElement (
    val type : TaskElementType = TaskElementType.variable,
    val text : String = "",
    val elements : Array<TaskElement> = arrayOf()
)