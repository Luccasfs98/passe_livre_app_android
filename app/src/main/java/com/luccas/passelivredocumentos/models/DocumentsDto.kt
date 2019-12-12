package com.luccas.passelivredocumentos.models

class DocumentsDto () {

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

    constructor(status : String, description :String,sent_documents:Boolean) : this () {
        this.status = status
        this.sent_documents = sent_documents
        this.description = description}
}
class DocumentsDto2 () {
    var name : String = ""
    var path : String = ""
    var type : String = ""
}