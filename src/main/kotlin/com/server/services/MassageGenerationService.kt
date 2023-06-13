package com.server.services

import its.reasoner.LearningSituation
import its.reasoner.util.JenaUtil
import its.reasoner.util.RDFUtil.resource
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.RDFNode

class MassageGenerationService {
    fun generateMessage(template : String, situation: LearningSituation) : String {
        val model = situation.model

        return Regex("\\{(.*?)\\}").replace(template, {
            var expression_elements = it.groupValues[1].split("->")

            var treeVariable = situation.decisionTreeVariables[expression_elements.first()];
            val treeVar = model.resource(treeVariable?:"symbol")
            var elements : List<Any> = listOf()
            treeVar.listProperties(model.getProperty(JenaUtil.POAS_PREF, expression_elements.get(1))).forEach {
                elements = elements + it.`object`
            }

            expression_elements = expression_elements.drop(2)

            expression_elements.forEach {
                var nextElements : List<Any> = listOf()

                if (elements.isEmpty()) {
                    throw Exception("Ошибка в шаблоне " + template)
                }

                if (!(elements.first() as RDFNode).isResource) {
                    throw Exception("Ошибка в шаблоне " + template)
                }

                elements.forEach { element ->
                    (element as RDFNode).asResource().listProperties(model.getProperty(JenaUtil.POAS_PREF, it)).
                    forEach { propertyValue ->
                        nextElements = nextElements + propertyValue.`object`
                    }
                }

                elements = nextElements
            }

            if (elements.isEmpty()) {
                throw Exception("Ошибка в шаблоне " + template)
            }

            if (!(elements.first() as RDFNode).isLiteral) {
                throw Exception("Ошибка в шаблоне " + template)
            }

            var outText = (elements.first() as RDFNode).asLiteral().string
            elements = elements.distinct()

            if (elements.size > 1) {
                var lastElement = elements.last()

                elements = elements.drop(1)
                elements = elements.dropLast(1)

                elements.forEach {
                    outText += ", " + it
                }

                outText += " или " + lastElement
            }

            return@replace outText
        })
    }
}