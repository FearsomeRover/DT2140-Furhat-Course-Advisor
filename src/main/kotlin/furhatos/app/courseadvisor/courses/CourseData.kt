package furhatos.app.courseadvisor.courses

class CourseData(path: String) {
    val courses: List<RestructuredCourseData>

    init {
        val inputStream = this::class.java.getResourceAsStream(path)
            ?: throw IllegalArgumentException("Resource not found: $path")
        courses = CourseParser.parse(inputStream)

    }

    fun getCourseByCode(code: String): RestructuredCourseData? {
        return courses.find { it.code.equals(code, ignoreCase = true) }
    }

    fun searchCourses(query: String): List<RestructuredCourseData> {
        val lowerQuery = query.lowercase()
        return courses.filter {
            it.code.lowercase().contains(lowerQuery) ||
                    it.name.lowercase().contains(lowerQuery) ||
                    it.department.lowercase().contains(lowerQuery)
        }
    }
    fun getRandomCourse(): RestructuredCourseData {
        if (courses.isEmpty()) throw Error("No courses available")
        return courses.random()
    }

}


data class RestructuredCourseData(
    val department: String,
    val code: String,
    val name: String,
    val credits: Double,
    val formattedPeriodsAndCredits: String,
    val targetGroup: String,
    val language: String,
    val goals: String,
    val content: String,
    val year: Int,
    val term: Int,
    val state: CourseState,
)

enum class CourseState(val value: String) {
    ESTABLISHED("ESTABLISHED"),
    APPROVED("APPROVED"),
}

data class CourseRoot(
    val department: String,
    val code: String,
    val name: String,
    val state: String,
    val detailedInformation: DetailedInformation
)

data class DetailedInformation(
    val course: Course,
    val examiners: List<Examiner>,
    val roundInfos: List<RoundInfo>,
    val mainSubjects: List<String>,                    // structure unknown from sample
    val examinationSets: Map<String, ExaminationSet>,
    val publicSyllabusVersions: List<PublicSyllabusVersion>,
    val socialCoursePageUrl: String,
    val formattedGradeScales: Map<String, String>
)

data class Course(
    val courseCode: String,
    val versionNumber: Int,
    val departmentCode: String,
    val department: Department,
    val educationalLevelCode: String,
    val educationalTypeId: Int,
    val gradeScaleCode: String,
    val title: String,
    val titleOther: String,
    val cancelled: Boolean,
    val deactivated: Boolean,
    val credits: Double,
    val creditUnitLabel: String,
    val creditUnitAbbr: String,
    val state: String,
    val courseLiterature: String,
    val courseVersion: CourseVersion
)

data class Department(
    val code: String,
    val name: String
)

data class CourseVersion(
    val versionNumber: Int,
    val keywordsEn: List<String>,
    val keywordsSv: List<String>
)

data class Examiner(
    val kthid: String,
    val givenName: String,
    val lastName: String,
    val email: String,
    val username: String
)


data class RoundInfo(
    val usage: List<Any>,                // empty in sample; adjust if you know structure
    val hasTimeslots: Boolean,
    val ldapResponsibles: List<Any>,     // empty in sample
    val ldapTeachers: List<Any>,         // empty in sample
    val round: Round
)

data class Round(
    val ladokRoundId: String,
    val ladokUID: String,
    val state: String,
    val tutoringTimeOfDay: NamedKey,
    val municipalityCode: String,
    val tutoringForm: NamedKey,
    val campus: Campus,
    val minSeats: Int,
    val selectionCriteriaSv: String,
    val selectionCriteriaEn: String,
    val language: String,
    val targetGroup: String,
    val draftCourseRoundType: CourseRoundType,
    val courseRoundTerms: List<CourseRoundTerm>,
    val applicationCodes: List<ApplicationCode>,
    val startTerm: TermWrapper,
    val isPU: Boolean,
    val isVU: Boolean,
    val startWeek: YearWeek,
    val endWeek: YearWeek,
    val startDate: String,           // e.g. "Jan 14, 2025 12:00:00 AM"
    val firstTuitionDate: String,    // e.g. "2025-01-14"
    val lastTuitionDate: String,     // e.g. "2025-06-02"
    val studyPace: Int
)

data class NamedKey(
    val name: String,
    val key: String
)

data class Campus(
    val name: String,
    val label: String
)

data class CourseRoundType(
    val code: String,
    val name: String,
    val active: Boolean,
    val category: String
)

data class CourseRoundTerm(
    val formattedPeriodsAndCredits: String,
    val term: TermWrapper,
    val startWeek: YearWeek,
    val endWeek: YearWeek,
    val creditsP3: Double,
    val creditsP4: Double
)

data class TermWrapper(
    val term: Int
)

data class YearWeek(
    val year: Int,
    val week: Int
)

data class ApplicationCode(
    val applicationCode: String,
    val term: String,
    val courseRoundType: CourseRoundType,
    val avgFri: Boolean
)

// -------------------- examinationSets --------------------

data class ExaminationSet(
    val startingTerm: TermWrapper,
    val examinationRounds: List<ExaminationRound>
)

data class ExaminationRound(
    val examCode: String,
    val title: String,
    val gradeScaleCode: String,
    val credits: Double,
    val creditUnitLabel: String,
    val creditUnitAbbr: String,
    val ladokUID: String
)

// -------------------- publicSyllabusVersions --------------------

data class PublicSyllabusVersion(
    val edition: Int,
    val validFromTerm: TermWrapper,
    val inStateApproved: Boolean,
    val courseSyllabus: CourseSyllabus
)

data class CourseSyllabus(
    val discontinuationText: String,
    val goals: String,
    val content: String,
    val eligibility: String,
    val examComments: String,
    val reqsForFinalGrade: String,
    val establishment: String,
    val decisionToDiscontinue: String,
    val languageOfInstruction: String,
    val transitionalRegulations: String,
    val ethicalApproach: String,
    val additionalRegulations: String,
    val courseSyllabusVersionValidFromTerm: TermWrapper
)

enum class MainSubjects(val value: String) {
    TECHNOLOGY( "Technology"),
    INDUSTRIAL_MANAGEMENT( "Industrial Management"),
    MECHANICAL_ENGINEERING( "Mechanical Engineering"),
    TECHNOLOGY_AND_HEALTH( "Technology and Health"),
    COMPUTER_SCIENCE_AND_ENGINEERING( "Computer Science and Engineering"),
    TECHNOLOGY_AND_LEARNING( "Technology and Learning"),
    BUILT_ENVIRONMENT( "Built Environment"),
    ELECTRICAL_ENGINEERING( "Electrical Engineering"),
    MATHEMATICS( "Mathematics"),
    MEDICAL_ENGINEERING( "Medical Engineering"),
    ARCHITECTURE( "Architecture")



}