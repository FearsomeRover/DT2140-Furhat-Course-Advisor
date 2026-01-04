package furhatos.app.furhatlab.api

import furhatos.app.courseadvisor.api.Department
import furhatos.app.courseadvisor.api.SearchResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.json.JSONObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


//Here we use this api to search the courses
///api/kopps/v2/courses/search?educational_level=RESEARCH&educational_level=ADVANCED&term_period=20182:1&department_prefix=ADC

//See more details: https://api.kth.se/api/kopps/v2/apiInfo/courses
public class SearchDetails(
    val textPattern: String = "test", //
    val onlyMhu: Boolean?,
    val inEnglishOnly: Boolean?,
    val includeNonActive : Boolean? = false,
    val termPeriod: String?, // format {5 digit term}:{1 digit period}
    val educationalLevel: List<EducationalLevel>?,
    val departmentPrefix: String?, //
){
    enum class EducationalLevel(val level: String) {
        ADVANCED("ADVANCED"),
        BASIC("BASIC"),
        RESEARCH("RESEARCH"),
        PREPARATORY("PREPARATORY")
    }
    fun generateParams(): Parameters {
        val params = ParametersBuilder()
        params.append("text_pattern", textPattern)
        onlyMhu?.let {
            params.append("only_mhu", it.toString())
        }
        inEnglishOnly?.let {
            params.append("in_english_only", it.toString())
        }
        includeNonActive?.let {
            params.append("include_non_active", it.toString())
        }
        termPeriod?.let {
            params.append("term_period", it)
        }
        educationalLevel?.let {
            for (level in it){
                params.append("educational_level", level.level)
            }
        }
        departmentPrefix?.let {
            params.append("department_prefix", it)
        }
        return params.build()
    }
}

public class SearchDetailsNew(
    val pattern: String,
    val semesters: List<String>? = null,
    val eduLevel: List<Number>? = null,
    val department: Department? = null,
    val onlyEnglish: Boolean = false,
    val onlyMhu: Boolean = false
){
    fun generateParams(): Parameters {
        val params = ParametersBuilder()
        params.append("pattern", pattern)
        if(semesters!=null) {
            for (semester in semesters) {
                params.append("semesters[]", semester)
            }
        }
        if(eduLevel!=null) {
            for (level in eduLevel) {
                params.append("eduLevel[]", level.toString())
            }
        }
        if(department != null && department.code.isNotEmpty()) {
            params.append("departmentPrefix", department.code)
        }
        if(onlyEnglish){
            params.append("showOptions[]", "onlyEnglish")
        }
        if(onlyMhu){
            params.append("showOptions[]", "onlyMhu")
        }
        return params.build()
    }
}

suspend fun getCourses(details: SearchDetailsNew): List<String> {
    println("Searching courses with details: ${details.generateParams()}")
    val client =  HttpClient(Apache)

//    val url = URLBuilder(
//        protocol = URLProtocol.HTTPS,
//        host = "api.kth.se",
//        pathSegments = listOf("api","kopps","v2","courses","search"),
//        parameters = details.generateParams()
//    ).buildString()

    try{
        val url = URLBuilder(
            protocol = URLProtocol.HTTPS,
            host = "www.kth.se",
            pathSegments = listOf("student","kurser","intern-api","sok","en"),
            parameters = details.generateParams()
        ).buildString()
        println("Constructed URL: $url")

        val jsonText: String = client.get(url).body()
        print(jsonText)

        val courses: SearchResponse = Json.decodeFromString(jsonText)

        return courses.searchData.results.map { it.kod }
    }catch (e: Exception    ){
        println("Error occurred: ${e.message}")
        println(e.stackTraceToString())
        return listOf("Error occurred: ${e.message}")
    }

}

