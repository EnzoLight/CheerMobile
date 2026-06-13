package com.cheermobile.models

import com.google.gson.annotations.SerializedName

data class Inscricao(
    val id: Int,
    val titulo: String? = null,
    val instituicao: String? = null,
    val cidade: String? = null,
    val uf: String? = null,
    val data: String? = null,
    val status: String? = null,
    @SerializedName("data_inscricao") val dataInscricao: String? = null,
)

data class InscricoesResponse(
    val status: String? = null,
    val data: List<Inscricao> = emptyList(),
    val message: String? = null,
)

data class InscritoEvento(
    @SerializedName("id_evento") val idEvento: Int,
    val evento: String? = null,
    @SerializedName("id_voluntario") val idVoluntario: Int,
    val nome: String? = null,
    val email: String? = null,
    val telefone: String? = null,
    val status: String? = null,
    @SerializedName("data_inscricao") val dataInscricao: String? = null,
)

data class InscritosEventoResponse(
    val status: String? = null,
    val data: List<InscritoEvento> = emptyList(),
    val message: String? = null,
)

data class DashboardInstituicaoResponse(
    val status: String? = null,
    val data: DashboardData? = null,
    val message: String? = null,
)

data class DashboardData(
    val kpis: DashboardKpis? = null,
    val series: DashboardSeries? = null,
    val tables: DashboardTables? = null,
)

data class DashboardKpis(
    @SerializedName("total_eventos") val totalEventos: Int = 0,
    @SerializedName("eventos_futuros") val eventosFuturos: Int = 0,
    @SerializedName("total_inscritos") val totalInscritos: Int = 0,
    @SerializedName("inscricoes_pendentes") val inscricoesPendentes: Int = 0,
    @SerializedName("inscricoes_aprovadas") val inscricoesAprovadas: Int = 0,
    @SerializedName("inscricoes_rejeitadas") val inscricoesRejeitadas: Int = 0,
    @SerializedName("taxa_ocupacao_percentual") val taxaOcupacaoPercentual: Double = 0.0,
)

data class DashboardSeries(
    @SerializedName("eventos_por_mes") val eventosPorMes: List<DashboardSeriesPoint> = emptyList(),
    @SerializedName("eventos_por_tipo") val eventosPorTipo: List<DashboardSeriesPoint> = emptyList(),
    @SerializedName("inscricoes_por_status") val inscricoesPorStatus: List<DashboardSeriesPoint> = emptyList(),
    @SerializedName("inscritos_por_evento") val inscritosPorEvento: List<DashboardSeriesPoint> = emptyList(),
)

data class DashboardSeriesPoint(
    val label: String? = null,
    val value: Int = 0,
)

data class DashboardTables(
    val eventos: List<Evento> = emptyList(),
    @SerializedName("inscritos_recentes") val inscritosRecentes: List<InscritoEvento> = emptyList(),
)

data class LogsResponse(
    val status: String? = null,
    val data: LogsData? = null,
    val message: String? = null,
)

data class LogsData(
    val items: List<LogEvento> = emptyList(),
    val pagination: Pagination? = null,
)

data class LogEvento(
    val id: Int,
    @SerializedName("tipo_evento") val tipoEvento: String? = null,
    val descricao: String? = null,
    val nivel: String? = null,
    val origem: String? = null,
    @SerializedName("id_usuario") val idUsuario: Int? = null,
    @SerializedName("tipo_usuario") val tipoUsuario: String? = null,
    @SerializedName("ip_origem") val ipOrigem: String? = null,
    @SerializedName("user_agent") val userAgent: String? = null,
    @SerializedName("data_hora") val dataHora: String? = null,
)

data class Pagination(
    val page: Int = 1,
    @SerializedName("per_page") val perPage: Int = 20,
    val total: Int = 0,
)

data class StatusInscritoRequest(
    val status: String,
)
