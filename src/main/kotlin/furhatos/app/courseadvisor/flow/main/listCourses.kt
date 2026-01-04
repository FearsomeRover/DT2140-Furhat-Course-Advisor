package furhatos.app.courseadvisor.flow.main

import furhatos.app.courseadvisor.courses.CourseData
import furhatos.flow.kotlin.*

//val model = OpenAIChatCompletionModel(serviceKey = System.getenv("OPENAI_API_KEY"),
//    systemPrompt = "You are a helpful assistant that provides university course information based on user queries. When given a description, respond with a single keyword that best matches relevant course names.")
//val httpScope = CoroutineScope(Dispatchers.IO)

val courseData = CourseData("/data/course_all.json")

val ListCourses: State = state {
    onEntry {
        val course = courseData.getRandomCourse()
        furhat.say("Here is a course you might be interested in:")
        furhat.say(course.name)
        furhat.say("The course code is ${course.code} and it is worth ${course.credits} credits.")
        furhat.say("It is offered by the ${course.department} department.")
        furhat.say("The course is taught in ${course.language} during year ${course.year}, term ${course.term}.")

    }


}