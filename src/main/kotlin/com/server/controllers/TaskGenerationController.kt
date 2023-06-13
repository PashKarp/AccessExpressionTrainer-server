package com.server.controllers

import com.server.models.SessionObj
import com.server.models.Task
import com.server.models.TaskElement
import com.server.models.TaskElementType

class TaskGenerationController {
    fun getNextTask(session : SessionObj) : Pair< Task, SessionObj > {
        return getTask(session.taskId + 1, session)
    }

    fun getTask(id : Int, session : SessionObj) : Pair< Task, SessionObj > {
        val tasks : Array<Task> = arrayOf(Task(
            "В объекте a класа A есть поле b класса B, в котором есть указатель на объект c класса C с целочисленным полем d. Напишите выражение для получения доступа к d.",
            arrayOf(
                TaskElement(
                TaskElementType.struct,
                "Класс A",
                arrayOf(
                    TaskElement(TaskElementType.variable, "b : B", arrayOf()),
                    TaskElement(TaskElementType.variable, "k : int", arrayOf()),
                )
            ),
                TaskElement(
                    TaskElementType.struct,
                    "Класс B",
                    arrayOf(
                        TaskElement(
                        TaskElementType.variable,
                        "c : C*",
                        arrayOf()
                    )
                    )),
                TaskElement(
                    TaskElementType.struct,
                    "Класс C",
                    arrayOf(
                        TaskElement(
                        TaskElementType.variable,
                        "d : int",
                        arrayOf()
                    )
                    ))
            ),
            getTaskOperands(0)
        ), Task(
            "objectMatrix- трехмерный массив указателей на объекты класса Cat размерностью 2x3x3. Получите доступ к полю name объекта содержащегося по указателю в objectMatrix с индексами [1][0][1]",
            arrayOf(
                TaskElement(
                    TaskElementType.struct,
                    "Класс Cat",
                    arrayOf(
                        TaskElement(TaskElementType.variable, "name : std::string", arrayOf()),
                        TaskElement(TaskElementType.variable, "age : int", arrayOf()),
                    )
                ),
            ),
            getTaskOperands(1)
        ),
            Task(
                "pointerObject- объект класса PointerObject с полем catPointer типа указателя на указатель объекта класса Cat. Получите доступ к полю name класса Cat содержащегося в этом указателе на указатель класса Cat",
                arrayOf(
                    TaskElement(
                        TaskElementType.struct,
                        "Класс PointerObject",
                        arrayOf(
                            TaskElement(TaskElementType.variable, "catPointer : Cat**", arrayOf()),
                            TaskElement(TaskElementType.variable, "dogPointer : Dog**", arrayOf()),
                        )
                    ),
                    TaskElement(
                        TaskElementType.struct,
                        "Класс Dog",
                        arrayOf(
                            TaskElement(TaskElementType.variable, "name : std::string", arrayOf()),
                            TaskElement(TaskElementType.variable, "age : int", arrayOf()),
                        )
                    ),
                    TaskElement(
                        TaskElementType.struct,
                        "Класс Cat",
                        arrayOf(
                            TaskElement(TaskElementType.variable, "name : std::string", arrayOf()),
                            TaskElement(TaskElementType.variable, "age : int", arrayOf()),
                        )
                    ),
                ),
                getTaskOperands(2)
            )
            )

        if (id >= tasks.size) {
            return Pair(tasks[0], session.copy(0))
        }

        return Pair(tasks[id], session.copy(id))
    }

    private fun getTaskOperands(index : Int) : Array<Pair<String, String>> {
        val taskOperands : Array<Array<Pair<String, String>>> = arrayOf(
            arrayOf(Pair("a", "0"), Pair("b", "1"), Pair("c", "2"), Pair("d", "3"), Pair("k", "4")),
            arrayOf(Pair("objectMatrix", "87"), Pair("Cat", "88"), Pair("name", "85"), Pair("age", "86"), Pair("0", "82"), Pair("1", "83"), Pair("2", "84")),
            arrayOf(Pair("pointerObject", "14"), Pair("catPointer", "17"), Pair("dogPointer", "18"), Pair("age", "16"), Pair("name", "15"), Pair("Cat", "19"), Pair("Dog", "20"))
        )

        if (index > taskOperands.size) {
            return taskOperands[0]
        }

        return taskOperands[index]
    }

    fun getTaskFile(index : Int) : String {
        val taskFiles : Array<String> = arrayOf(
            ".\\models\\input_examples_access\\1.ttl",
            ".\\models\\input_examples_access\\2.ttl",
            ".\\models\\input_examples_access\\3.ttl"
            )

        if (index > taskFiles.size) {
            return taskFiles[0]
        }

        return taskFiles[index]
    }
}