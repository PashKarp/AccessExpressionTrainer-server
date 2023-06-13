package com.server.models

import its.reasoner.LearningSituation
import kotlinx.serialization.Serializable
import org.apache.jena.rdf.model.Model


class Status(
    val status : Boolean,
    val message : String?,
    val model : LearningSituation,
    val errorIndex : Int?
)