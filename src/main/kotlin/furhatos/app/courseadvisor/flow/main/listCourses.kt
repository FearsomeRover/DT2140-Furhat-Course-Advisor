package furhatos.app.courseadvisor.flow.main

import furhatos.app.courseadvisor.llm.OpenAIChatCompletionModel
import furhatos.app.furhatlab.api.SearchDetails
import furhatos.app.furhatlab.api.SearchDetailsNew
import furhatos.app.furhatlab.api.getCourses
import furhatos.flow.kotlin.*
import furhatos.nlu.EnumEntity
import furhatos.nlu.Intent
import furhatos.util.Language
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val model = OpenAIChatCompletionModel(serviceKey = System.getenv("OPENAI_API_KEY"),
    systemPrompt = "You are a helpful assistant that provides university course information based on user queries. When given a description, respond with a single keyword that best matches relevant course names.")
val httpScope = CoroutineScope(Dispatchers.IO)

val ListCourses: State = state {
    onEntry {
        furhat.ask("What keyword to search for?")
//       furhat.say( keyword.toString())
//        httpScope.launch {
////            val details = SearchDetails(
////                textPattern = it.intent.keyword,
////                onlyMhu = null,
////                inEnglishOnly = null,
////                includeNonActive = false,
////                termPeriod = null,
////                educationalLevel = null,
////                departmentPrefix = null
////            )
//
//            val details = SearchDetailsNew(
//                pattern = keyword.toString(),
//            )
//            val courses = getCourses(details)
//            furhat.say("Here are the courses I found for the keyword ${it.intent.keyword}:")
//            furhat.say(courses.joinToString(separator = "\n"))
//        }

    }
    onResponse {
        httpScope.launch {
            //val keywords = model.request(it.text)
            val keywords = "data science" // for testing purposes
            furhat.say("The keywords are $keywords")
//            val details = SearchDetails(
//                textPattern = response,
//                onlyMhu = null,
//                inEnglishOnly = null,
//                includeNonActive = false,
//                termPeriod = null,
//                educationalLevel = null,
//                departmentPrefix = null
//            )
            val details = SearchDetailsNew(
                pattern = keywords,
            )
            println("Searching courses with keywords: $keywords")
            val courses = getCourses(details)
            furhat.say("Here are the courses I found for the keywords ${keywords}:")
            furhat.say(courses.joinToString(separator = "\n"))
        }
    }

}