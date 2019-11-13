package com.luccas.passelivredocumentos.models

class PersonalDataDto{

    constructor()
    constructor(
        name: String,
        phone: String,
        nameFather: String,
        nameMother: String,
        dateBirthday: String,
        cpf: String,
        sex: String,
        type: String
    ) {
        this.name = name
        this.phone = phone
        this.nameFather = nameFather
        this.nameMother = nameMother
        this.dateBirthday = dateBirthday
        this.cpf = cpf
        this.sex = sex
        this.type = type
    }

    var name: String?=null
    var phone: String?=null
    var nameFather: String?=null
    var nameMother: String?=null
    var dateBirthday: String?=null
    var cpf: String?=null
    var sex: String?=null
    var type: String?=null
}

