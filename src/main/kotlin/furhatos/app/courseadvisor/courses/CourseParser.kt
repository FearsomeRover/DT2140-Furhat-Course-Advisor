// kotlin
package furhatos.app.courseadvisor.courses

import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

object CourseParser {
    fun parse(input: InputStream): List<RestructuredCourseData> {
        val text = input.bufferedReader().use { it.readText() }
        val arr = JSONArray(text)
        val out = ArrayList<RestructuredCourseData>(arr.length())

        for (i in 0 until arr.length()) {
            val root = arr.optJSONObject(i) ?: continue

            val department = root.optString("department", "")
            val code = root.optString("code", "")
            val name = root.optString("name", "")
            val stateStr = root.optString("state", "")

            val detailed = root.optJSONObject("detailedInformation") ?: JSONObject()

            // credits
            val courseObj = detailed.optJSONObject("course") ?: JSONObject()
            val credits = courseObj.optDouble("credits", 0.0)

            // pick syllabus: first in-state-approved or first
            var syllabusObj: JSONObject? = null
            val publicSyll = detailed.optJSONArray("publicSyllabusVersions")
            if (publicSyll != null) {
                for (j in 0 until publicSyll.length()) {
                    val sv = publicSyll.optJSONObject(j) ?: continue
                    if (sv.optBoolean("inStateApproved", false)) { syllabusObj = sv; break }
                }
                if (syllabusObj == null && publicSyll.length() > 0) syllabusObj = publicSyll.optJSONObject(0)
            }
            val courseSyllabusObj = syllabusObj?.optJSONObject("courseSyllabus")
            val goals = courseSyllabusObj?.optString("goals", "") ?: ""
            val content = courseSyllabusObj?.optString("content", "") ?: ""
            val syllabusLanguage = courseSyllabusObj?.optString("languageOfInstruction", "") ?: ""
            val validFromTermFallback = syllabusObj?.optJSONObject("validFromTerm")?.optInt("term", 0) ?: 0

            // first round info -> round
            var roundObj: JSONObject? = null
            val roundInfos = detailed.optJSONArray("roundInfos")
            if (roundInfos != null && roundInfos.length() > 0) {
                val ri = roundInfos.optJSONObject(0)
                roundObj = ri?.optJSONObject("round")
            }

            // formattedPeriodsAndCredits and term from first courseRoundTerm if present
            val firstCourseRoundTerm = roundObj?.optJSONArray("courseRoundTerms")?.optJSONObject(0)
            val formattedPeriodsAndCredits = firstCourseRoundTerm?.optString("formattedPeriodsAndCredits", "") ?: ""
            val firstTermTerm = firstCourseRoundTerm?.optJSONObject("term")?.optInt("term", 0) ?: 0
            val startTermTerm = roundObj?.optJSONObject("startTerm")?.optInt("term", 0) ?: 0
            val term = if (firstTermTerm != 0) firstTermTerm else startTermTerm

            // year: prefer round.startWeek.year, fallback to syllabus validFromTerm
            val year = roundObj?.optJSONObject("startWeek")?.optInt("year", 0) ?: validFromTermFallback

            val targetGroup = roundObj?.optString("targetGroup", "") ?: ""
            val roundLanguage = roundObj?.optString("language", "") ?: ""
            val language = if (syllabusLanguage.isNotEmpty()) syllabusLanguage else roundLanguage

            val state = if (stateStr.uppercase() == CourseState.ESTABLISHED.value) CourseState.ESTABLISHED else CourseState.APPROVED

            out.add(
                RestructuredCourseData(
                    department = department,
                    code = code,
                    name = name,
                    credits = credits,
                    formattedPeriodsAndCredits = formattedPeriodsAndCredits,
                    targetGroup = targetGroup,
                    language = language,
                    goals = goals,
                    content = content,
                    year = year,
                    term = term,
                    state = state
                )
            )
        }

        return out
    }
}
