package com.luccas.passelivredocumentos.models

class SoliciationDto (){

    var cep: String? = null
    var street: String? = null
    var complement : String? = null
    var city: String? = null
    var homeNumber: String? = null
    var neighborhood: String? = null
    var uf: String? = null
    var semesterPeriodStart: String? = null
    var semesterPeriodEnd : String? = null
    var schoolSemester: String? = null
    var schoolDays: List<String>? = null
    var level: String? = null
    var turn: List<String> ? = null
    var fullname: String?=null
    var phone: String?=null
    var nameFather: String?=null
    var nameMother: String?=null
    var dateBirthday: String?=null
    var cpf: String?=null
    var sex: String?=null
    var type: String?=null
    var line: String? = null
    var transportName: String? = null
    var alreadyHavePasseLivre : Boolean? = false
    var useIntegration : Boolean? = false
    var prouniScholarshipHolder : Boolean? = false
    var id: String = ""
    var collegeName : String = ""
    var email: String = ""
    var proof_of_address : String = ""
    var profile_pic : String = ""
    var front_of_identity : String = ""
    var identity_verse : String = ""
    var voucher_frequency: String = ""
    var registration_certificate: String = ""
    var card_voucher: String = ""
    var proof_of_income: String =""
    var sent_documents : Boolean?=false
    var status : String?=""
    var description : String? = ""
    var reason : String? = ""
}