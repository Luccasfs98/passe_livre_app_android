package com.luccas.passelivredocumentos.models

class AddressDto(){

    constructor(
        cep: String,
        street: String,
        complement: String,
        city: String,
        homeNumber: String,
        neighborhood: String,
        uf: String
    ) :this(){
        this.cep = cep
        this.street = street
        this.complement = complement
        this.city = city
        this.homeNumber = homeNumber
        this.neighborhood = neighborhood
        this.uf = uf
    }

    var cep: String? = null
    var street: String? = null
    var complement : String? = null
    var city: String? = null
    var homeNumber: String? = null
    var neighborhood: String? = null
    var uf: String? = null

}


