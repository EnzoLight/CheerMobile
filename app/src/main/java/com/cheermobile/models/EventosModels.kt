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
    val contancia: String?,
    val inscritos: Int?,
    val cidade: String?,
    val uf: String?,
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

// Criação de evento

data class CreateEventoRequest(
    val titulo: String,
    @SerializedName("tipo_evento") val tipoEvento: String,
    val constancia: String?,
    val descricao: String?,
    @SerializedName("num_max_voluntarios") val numMaxVoluntarios: Int?,
    @SerializedName("data_hora_inicio") val dataHoraInicio: String,
    @SerializedName("data_hora_termino") val dataHoraTermino: String?,
    val endereco: EnderecoRequest,
)
 
data class CreateEventoResponse(
    val message: String,
    val data: Evento?,
)
 
 // Lista de meus Eventos
data class MeusEventosResponse(
    val data: List<Evento>,
)