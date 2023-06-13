package com.server.controllers

import com.server.models.AnswerElement
import com.server.models.ClientAnswerElement
import com.server.models.SessionObj
import com.server.models.Status
import com.server.models.OutStatus
import com.server.controllers.TaskGenerationController
import com.server.services.AnswersParsingService
import com.server.services.MassageGenerationService
import com.server.services.ReasonerService
import org.apache.jena.rdf.model.Model

class ReasonerController {
    fun getTaskInfo(clientAnswer : ClientAnswerElement, session : SessionObj) : Pair<List<AnswerElement>, String> {
        val taskId = session.taskId

        val answerElements : List<AnswerElement> = AnswersParsingService().parseClientAnswerElement(clientAnswer)
        val taskFile : String = TaskGenerationController().getTaskFile(taskId)

        return Pair(answerElements, taskFile)
    }

    fun checkTaskInfo(answerElements : List<AnswerElement>) : OutStatus {
        if (answerElements.isEmpty()) {
            return OutStatus(false, "Неизвестный тип узла")
        }

        if (!answerElements.first().isStart) {
            return OutStatus(false, "Начальным элементом выражения не может быть применение оператора")
        }

        return OutStatus(true, null)
    }

    fun checkAnswer(clientAnswer : ClientAnswerElement, session : SessionObj): OutStatus {
        val taskInfo = getTaskInfo(clientAnswer, session)

        var outStatus = checkTaskInfo(taskInfo.first)

        if (!outStatus.status) {
            return outStatus
        }

        val reasonerStatus : Status = ReasonerService(taskInfo.second).check(taskInfo.first)
        if (!reasonerStatus.status) {
            return OutStatus(
                false,
                MassageGenerationService().generateMessage(reasonerStatus.message ?: "", reasonerStatus.model)
            )
        }

        return OutStatus(reasonerStatus.status, reasonerStatus.message)
    }

    fun checkForComplete(clientAnswer : ClientAnswerElement, session : SessionObj): OutStatus {
        val taskInfo = getTaskInfo(clientAnswer, session)

        var outStatus = checkTaskInfo(taskInfo.first)

        if (!outStatus.status) {
            return outStatus
        }

        val reasonerStatus : Status = ReasonerService(taskInfo.second).checkAll(taskInfo.first)
        if (!reasonerStatus.status) {
            return OutStatus(
                false,
                MassageGenerationService().generateMessage(reasonerStatus.message ?: "", reasonerStatus.model),
            )
        }

        return OutStatus(reasonerStatus.status, reasonerStatus.message)
    }

    fun getHint(clientAnswer : ClientAnswerElement, session : SessionObj): OutStatus {
        val taskInfo = getTaskInfo(clientAnswer, session)

        var outStatus = checkTaskInfo(taskInfo.first)

        if (taskInfo.first.isEmpty()) {
            return OutStatus(true, getFirstHint(session.taskId))
        }

        if (!outStatus.status) {
            return outStatus
        }

        val reasonerStatus : Status = ReasonerService(taskInfo.second).solve(taskInfo.first)
        if (!reasonerStatus.status) {
            return OutStatus(
                false,
                MassageGenerationService().generateMessage(reasonerStatus.message ?: "", reasonerStatus.model)
            )
        }

        return OutStatus(
            reasonerStatus.status,
            MassageGenerationService().generateMessage(reasonerStatus.message ?: "", reasonerStatus.model)
        )
    }

    fun getFirstHint(index : Int) : String {
        var hints : Array<String> = arrayOf(
            "Попробуйте взять переменную a",
            "Попробуйте взять переменную objectMatrix",
            "Попробуйте взять переменную objectPointer"
            )

        return hints[index]
    }
}