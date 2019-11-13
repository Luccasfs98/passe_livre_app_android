package com.luccas.passelivredocumentos.models

class EducationalEstablishmentDto(){

    constructor(
        name: String,
        semesterPeriodStart: String,
        semesterPeriodEnd: String,
        schoolSemester: String,
        schoolDays: List<String>,
        level: String,
        turn: List<String>
    ) : this() {
        this.name = name
        this.semesterPeriodStart = semesterPeriodStart
        this.semesterPeriodEnd = semesterPeriodEnd
        this.schoolSemester = schoolSemester
        this.schoolDays = schoolDays
        this.level = level
        this.turn = turn
    }

    var name: String? = null
    var semesterPeriodStart: String? = null
    var semesterPeriodEnd : String? = null
    var schoolSemester: String? = null
    var schoolDays: List<String>? = null
    var level: String? = null
    var turn: List<String> ? = null
}
