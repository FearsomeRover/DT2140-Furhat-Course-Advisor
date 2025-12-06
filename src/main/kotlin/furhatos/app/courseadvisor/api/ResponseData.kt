package furhatos.app.courseadvisor.api

import kotlinx.serialization.Serializable


@Serializable
data class SearchResponse(
    val searchData: SearchData
)

@Serializable
data class SearchData(
    val results: List<CourseInstance>,
    val type: String
)

@Serializable
data class CourseInstance(
    val kod: String,
    val benamning: String,
    val omfattning: String,
    val utbildningstyper: List<String>,
    val studietakter: List<Int>,
    val undervisningssprak: List<String>,
    val studieorter: List<String>,
    val startTerm: String,
    val perioder: List<Period>
)

@Serializable
data class Period(
    val startPeriodYear: Int,
    val startPeriod: Int,
    val endPeriodYear: Int,
    val endPeriod: Int,
    val tillfallesperioderNummer: Int
)