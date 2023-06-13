package com.server.services;

import com.server.models.AnswerElement;
import com.server.models.ClientAnswerElement;

class AnswersParsingService {
    fun parseClientAnswerElement(element : ClientAnswerElement) : List<AnswerElement> {
        when (element.type) {
            "start_element_expression" -> {
                var resultAr = listOf(AnswerElement(true, element.operand?.type?.toInt(), null))
                if (element.next != null) {
                    resultAr += parseClientAnswerElement(element.next)
                }
                return resultAr
            }
            "element_expression" -> {
                var resultAr = listOf(AnswerElement(false, element.operand?.type?.toInt(), element.operator))
                if (element.next != null) {
                    resultAr += parseClientAnswerElement(element.next)
                }
                return resultAr
            }
            "around_brackets" -> {
                var resultAr = listOf(AnswerElement(false, null, null))
                if (element.operand != null) {
                    resultAr = parseClientAnswerElement(element.operand)
                }
                resultAr += listOf(AnswerElement(false, null, element.operator))
                if (element.next != null) {
                    resultAr += parseClientAnswerElement(element.next)
                }
                return resultAr
            }
            "around_brackets_with_inner_operator" -> {
                var resultAr = listOf(AnswerElement(false, null, null))
                if (element.operand != null) {
                    resultAr = parseClientAnswerElement(element.operand)
                }
                resultAr += listOf(AnswerElement(false, element.secondOperand?.toInt(), element.operator))
                if (element.next != null) {
                    resultAr += parseClientAnswerElement(element.next)
                }
                return resultAr
            }
            else -> {
                return listOf()
            }
        }
    }
}