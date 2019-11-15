package com.luccas.passelivredocumentos.models

/**
/ Created by Luccas Ferreira da Silva on 01 Novembro,2019
/ Company: CroSoften Tecnologia
 **/
class User{


    var id: String = ""
    var name: String = ""
    var email: String = ""
    var type: String = ""

    constructor(
        id:String,
        name:String,
        email:String,
        type:String
    ) : this() {
        this.id = id
        this.name = name
        this.email = email
        this.type = type
    }


    constructor()
}