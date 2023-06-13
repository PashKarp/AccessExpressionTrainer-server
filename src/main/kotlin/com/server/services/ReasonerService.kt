package com.server.services

import com.server.models.AnswerElement
import com.server.models.Status
import its.model.DomainModel
import its.model.dictionaries.*
import its.model.nodes.BranchResultNode
import its.reasoner.LearningSituation
import its.reasoner.nodes.DecisionTreeReasoner._static.getCorrectPath
import its.reasoner.util.JenaUtil
import its.reasoner.util.RDFUtil.resource

class ReasonerService(
    val path : String
) {
    fun solve(currentPath : List<AnswerElement>) : Status {
        var situation = LearningSituation(this.path)

        var outStatus = checkPath(currentPath, situation)

        if (outStatus.status) {
            if (situation.decisionTreeVariables["currentItem"] == situation.decisionTreeVariables["targetItem"]) {
                outStatus = Status(true, "Вы дошли до цели задачи", situation, null)
            } else {
                outStatus = Status(
                    true,
                    "Попробуйте взять {currentItem->next->connection_type->hint_text} {currentItem->next->text}",
                    situation,
                    null
                )
            }
        }

        return outStatus
    }

    fun check(currentPath : List<AnswerElement>) : Status
    {
        var situation = LearningSituation(this.path)

        return checkPath(currentPath, situation)
    }

    fun checkAll(currentPath : List<AnswerElement>) : Status
    {
        var situation = LearningSituation(this.path)

        var outStatus = checkPath(currentPath, situation)

        if (outStatus.status) {
            setStage(situation, "Final")

            val last = DomainModel.decisionTree.main.getCorrectPath(situation).last()
            val res = (last as BranchResultNode).value as Boolean
            if (!res) {
                outStatus = Status(false, last.additionalInfo["pattern"], situation, currentPath.size)
            }
        }

        return outStatus
    }

    private fun checkPath(currentPath : List<AnswerElement>, situation : LearningSituation) : Status {
        var outStatus = Status(true, null, situation, null)

        var operandRDFProperty = situation.model.getProperty(JenaUtil.POAS_PREF, "operand")
        var operatorRDFProperty = situation.model.getProperty(JenaUtil.POAS_PREF, "operator")
        var operatorTypeRDFProperty = situation.model.getProperty(JenaUtil.POAS_PREF, "operator_type")
        var unaryRDFProperty = situation.model.getProperty(JenaUtil.POAS_PREF, "unary")

        val symbolObj = situation.model.resource("symbol")

        currentPath.forEachIndexed { index, it ->
            if (it.isStart) {
                val operandId = it.operandId

                symbolObj.removeAll(operandRDFProperty)
                symbolObj.addProperty(operandRDFProperty,
                    situation.model.listStatements().toList().first{
                        it.predicate.localName == "operand_id" && it.`object`.asLiteral().int == operandId
                    }.subject)

                setStage(situation, "Start")
            } else {
                val operatorId = it.operatorId
                val operatorNode = situation.model.listStatements().toList().first{
                    it.predicate.localName == "operator_text" && it.`object`.asLiteral().string == operatorId
                }.subject

                symbolObj.removeAll(operatorRDFProperty)
                symbolObj.addProperty(operatorRDFProperty, operatorNode)

                if (operatorNode.getProperty(operatorTypeRDFProperty).`object`.asResource()
                    .getProperty(unaryRDFProperty).`object`.asLiteral().boolean) {
                    symbolObj.removeAll(operandRDFProperty)
                    symbolObj.addProperty(operandRDFProperty, situation.model.resource(situation.decisionTreeVariables["currentItem"]!!))
                } else {
                    val operandId = it.operandId

                    symbolObj.removeAll(operandRDFProperty)
                    symbolObj.addProperty(operandRDFProperty,
                        situation.model.listStatements().toList().first{
                            it.predicate.localName == "operand_id" && it.`object`.asLiteral().int == operandId
                        }.subject)
                }

                setStage(situation, "Main")
            }

            val path = DomainModel.decisionTree.main.getCorrectPath(situation)
            val last = path.last()
            val res = (last as BranchResultNode).value as Boolean
            if (!res) {
                outStatus = Status(false, last.additionalInfo["pattern"], situation, null)

                return@forEachIndexed
            } else {
                outStatus = Status(true, null, situation, null)
            }
        }

        return outStatus
    }

    private fun setStage(situation : LearningSituation, stage : String) {
        var currentStageRDFProperty = situation.model.getProperty(JenaUtil.POAS_PREF, "current_stage")

        val stageObj = situation.model.resource("stage")

        stageObj.removeAll(currentStageRDFProperty)
        stageObj.addProperty(currentStageRDFProperty, situation.model.resource(stage))
    }

    private fun readSolveModel() {
        val dir = "models\\input_solve_examples_access\\"
//val dir = "inputs\\"

        DomainModel(
            ClassesDictionary(),
            DecisionTreeVarsDictionary(),
            EnumsDictionary(),
            PropertiesDictionary(),
            RelationshipsDictionary(),
            dir,
        )
    }
}