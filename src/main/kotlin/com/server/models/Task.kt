package com.server.models
import kotlinx.serialization.Serializable
import com.server.models.TaskElement

@Serializable
class Task(
    val condition : String = "",
    val taskStructure : Array<TaskElement> = arrayOf(),
    val availableOperands : Array<Pair<String, String>> = arrayOf(Pair("", ""))
)