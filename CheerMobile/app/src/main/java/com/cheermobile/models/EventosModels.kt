package com.cheermobile.models

import com.google.gson.annotations.SerializedName

data class Evento(
    val id: Int,
    val titulo: String,
    val descricao: String?,
    @SerializedName("id_instituicao") val idInstituicao: Int,
    @SerializedName("data_hora_inicio") val dataInicio: String,
    @SerializedName("data_hora_termino") val dataTermino: String?,
    @SerializedName("num_max_voluntarios") val maxVoluntarios: Int,
    @SerializedName("tipo_evento") val tipoEvento: String,
    val endereco: Endereco? // Opcional: se a API retornar o endereço junto
)

data class InscricaoRequest(
    @SerializedName("id_evento") val idEvento: Int
)

data class InscricaoResponse(
    val status: String,
    val message: String,
    @SerializedName("data_inscricao") val dataInscricao: String
)
