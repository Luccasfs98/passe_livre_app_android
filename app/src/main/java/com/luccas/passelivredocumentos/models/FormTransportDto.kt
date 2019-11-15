package com.luccas.passelivredocumentos.models

class FormTransportDto() {

    constructor(
        transportName: String,
        line: String,
        alreadyHavePasseLivre: Boolean?,
        useIntegration: Boolean?,
        prouniScholarshipHolder: Boolean?
    ) : this(){
        this.transportName = transportName
        this.line = line
        this.alreadyHavePasseLivre = alreadyHavePasseLivre
        this.useIntegration = useIntegration
        this.prouniScholarshipHolder = prouniScholarshipHolder
    }

    var line: String? = null
    var transportName: String? = null
    var alreadyHavePasseLivre : Boolean? = false
    var useIntegration : Boolean? = false
    var prouniScholarshipHolder : Boolean? = false

}
