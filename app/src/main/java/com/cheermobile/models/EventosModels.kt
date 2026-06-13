package com.cheermobile.models

import com.google.gson.annotations.SerializedName

data class Evento(
    val id: Int,
    val titulo: String?,
    val descricao: String?,
    @SerializedName("id_instituicao") val idInstituicao: Int? = null,
    @SerializedName(value = "data_hora_inicio", alternate = ["data"]) val dataInicio: String? = null,
    @SerializedName("data_hora_termino") val dataTermino: String? = null,
    @SerializedName(value = "num_max_voluntarios", alternate = ["vagas"]) val maxVoluntarios: Int? = null,
    @SerializedName("tipo_evento") val tipoEvento: String? = null,
    @SerializedName("constancia") val constancia: String? = null,
    val inscritos: Int?,
    val cidade: String?,
    val uf: String?,
    val endereco: Endereco? // Opcional: se a API retornar o endereço junto
)

data class InscricaoRequest(
    @SerializedName("id_evento") val idEvento: Int
)

data class InscricaoResponse(
    val status: String? = null,
    val message: String? = null,
    val data: InscricaoStatus? = null,
)

data class InscricaoStatus(
    val status: String? = null,
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
    val status: String? = null,
    val data: List<Evento> = emptyList(),
    val message: String? = null,
)

data class EventosResponse(
    val status: String? = null,
    val data: List<Evento> = emptyList(),
    val message: String? = null,
)
