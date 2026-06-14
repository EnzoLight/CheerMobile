package com.cheermobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cheermobile.MyViewModel
import com.cheermobile.models.CreateEventoRequest
import com.cheermobile.models.Evento
import com.cheermobile.models.EnderecoRequest
import com.cheermobile.ui.theme.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// ---------------------------------------------------------------------------
// Estado do formulário
// ---------------------------------------------------------------------------

private data class EventoForm(
    val titulo: String = "",
    val tipoEvento: String = "",
    val constancia: String = "",
    val descricao: String = "",
    val numMaxVoluntarios: String = "",
    val dataInicio: String = "",
    val horaInicio: String = "",
    val dataFim: String = "",
    val horaFim: String = "",
    // Endereço
    val codigoPostal: String = "",
    val rua: String = "",
    val numero: String = "",
    val complemento: String = "",
    val bairro: String = "",
    val cidade: String = "",
    val uf: String = "",
)

// ---------------------------------------------------------------------------
// Helpers (espelham o FormularioEvento.jsx)
// ---------------------------------------------------------------------------

private fun onlyDigits(value: String) = value.filter { it.isDigit() }

private fun todayString(): String {
    val calendar = java.util.Calendar.getInstance()
    val year = calendar.get(java.util.Calendar.YEAR)
    val month = calendar.get(java.util.Calendar.MONTH) + 1
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
    return String.format("%04d-%02d-%02d", year, month, day)
}

private fun parseFormDateTime(date: String, time: String): LocalDateTime? {
    if (date.isBlank() || time.isBlank()) return null

    return try {
        LocalDateTime.parse("${date.trim()}T${time.trim()}:00")
    } catch (e: Exception) {
        null
    }
}

/** Monta datetime com offset local, ex: "2025-06-10T14:00:00-03:00" */
private fun toApiDateTime(localDt: LocalDateTime): String {
    val zoneOffset = ZoneId.systemDefault().rules.getOffset(localDt)
    val offsetStr = zoneOffset.toString().let { if (it == "Z") "+00:00" else it }
    return "${localDt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}$offsetStr"
}

// ---------------------------------------------------------------------------
// Tela principal
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarEventoScreen(
    onBackClick: () -> Unit = {},
    eventoIdToEdit: Int? = null,
    onSaved: (() -> Unit)? = null,
    myViewModel: MyViewModel = viewModel(),
) {
    var form by remember { mutableStateOf(EventoForm()) }
    var feedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var cepStatus by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    val latestFormState = rememberUpdatedState(form)
    val cepDigits = onlyDigits(form.codigoPostal)

    // Lista de eventos da instituição
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
    var listStatus by remember { mutableStateOf("loading") }   // loading | loaded | error
    var listError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(cepDigits) {
        if (cepDigits.length != 8) {
            cepStatus = null
            return@LaunchedEffect
        }

        val requestedCep = cepDigits
        cepStatus = Pair(true, "Buscando endereço...")

        myViewModel.buscarEnderecoPorCep(requestedCep) { success, address, message ->
            if (onlyDigits(latestFormState.value.codigoPostal) != requestedCep) {
                return@buscarEnderecoPorCep
            }

            if (success && address != null) {
                val current = latestFormState.value
                form = current.copy(
                    codigoPostal = requestedCep,
                    rua = address.logradouro.orEmpty(),
                    bairro = address.bairro.orEmpty(),
                    cidade = address.localidade.orEmpty(),
                    uf = address.uf.orEmpty(),
                )
                cepStatus = Pair(true, "Endereço localizado.")
            } else {
                cepStatus = Pair(false, message ?: "CEP não encontrado.")
            }
        }
    }

    // Adicione um tratamento de erro global no loadMeusEventos dentro da CriarEventoScreen
    fun loadMeusEventos() {
        listStatus = "loading"
        listError = null
        try {
            myViewModel.getMeusEventos { success, result, error ->
                if (success) {
                    eventos = result
                    listStatus = "loaded"
                } else {
                    eventos = emptyList()
                    listError = error ?: "Não foi possível carregar os eventos."
                    listStatus = "error"
                }
            }
        } catch (e: Exception) {
            listStatus = "error"
            listError = "Erro inesperado: ${e.message}"
        }
    }

    fun splitApiDate(value: String?): Pair<String, String> {
        if (value.isNullOrBlank()) return "" to ""
        return value.take(10) to value.substringAfter("T", "").take(5)
    }

    fun fillFormFromEvento(evento: Evento) {
        val (inicioData, inicioHora) = splitApiDate(evento.dataInicio)
        val (fimData, fimHora) = splitApiDate(evento.dataTermino)
        form = EventoForm(
            titulo = evento.titulo.orEmpty(),
            tipoEvento = evento.tipoEvento.orEmpty(),
            constancia = evento.constancia.orEmpty(),
            descricao = evento.descricao.orEmpty(),
            numMaxVoluntarios = evento.maxVoluntarios?.toString().orEmpty(),
            dataInicio = inicioData,
            horaInicio = inicioHora,
            dataFim = fimData,
            horaFim = fimHora,
            codigoPostal = evento.endereco?.cep.orEmpty(),
            rua = evento.endereco?.rua.orEmpty(),
            numero = evento.endereco?.numero.orEmpty(),
            complemento = evento.endereco?.complemento.orEmpty(),
            bairro = evento.endereco?.bairro.orEmpty(),
            cidade = evento.endereco?.cidade.orEmpty(),
            uf = evento.endereco?.uf.orEmpty(),
        )
    }

    LaunchedEffect(eventoIdToEdit) {
        if (eventoIdToEdit == null) {
            loadMeusEventos()
        } else {
            listStatus = "loading"
            myViewModel.getEvento(eventoIdToEdit) { success, evento, error ->
                if (success && evento != null) {
                    fillFormFromEvento(evento)
                    listStatus = "loaded"
                } else {
                    listError = error ?: "Nao foi possivel carregar o evento."
                    listStatus = "error"
                }
            }
        }
    }

    fun updateField(field: String, value: String) {
        form = when (field) {
            "titulo"             -> form.copy(titulo = value)
            "tipoEvento"         -> form.copy(tipoEvento = value)
            "constancia"         -> form.copy(constancia = value)
            "descricao"          -> form.copy(descricao = value)
            "numMaxVoluntarios"  -> form.copy(numMaxVoluntarios = value)
            "dataInicio"         -> form.copy(dataInicio = value)
            "horaInicio"         -> form.copy(horaInicio = value)
            "dataFim"            -> form.copy(dataFim = value)
            "horaFim"            -> form.copy(horaFim = value)
            "codigoPostal"       -> form.copy(codigoPostal = onlyDigits(value).take(8))
            "rua"                -> form.copy(rua = value)
            "numero"             -> form.copy(numero = value)
            "complemento"        -> form.copy(complemento = value)
            "bairro"             -> form.copy(bairro = value)
            "cidade"             -> form.copy(cidade = value)
            "uf"                 -> form.copy(uf = value.uppercase().take(2))
            else                 -> form
        }
        feedback = null
    }

    fun handleSubmit() {
        val titulo = form.titulo.trim()
        val tipoEvento = form.tipoEvento.trim()
        val maxVoluntarios = form.numMaxVoluntarios.trim().takeIf { it.isNotBlank() }?.toIntOrNull()
        val inicio = parseFormDateTime(form.dataInicio, form.horaInicio)
        val termino = parseFormDateTime(form.dataFim, form.horaFim)
        val cep = onlyDigits(form.codigoPostal)
        val uf = form.uf.trim().uppercase()

        if (titulo.isBlank()) {
            feedback = Pair(false, "Informe o título do evento.")
            return
        }
        if (tipoEvento.isBlank()) {
            feedback = Pair(false, "Selecione o tipo do evento.")
            return
        }
        if (form.numMaxVoluntarios.isNotBlank() && (maxVoluntarios == null || maxVoluntarios <= 0)) {
            feedback = Pair(false, "Informe uma quantidade de voluntários maior que zero.")
            return
        }
        if (inicio == null) {
            feedback = Pair(false, "Informe data e hora de início no formato 2026-06-13 e 14:00.")
            return
        }
        if (inicio.isBefore(LocalDateTime.now())) {
            feedback = Pair(false, "A data de início não pode ser anterior ao momento atual.")
            return
        }
        if ((form.dataFim.isNotBlank() || form.horaFim.isNotBlank()) && termino == null) {
            feedback = Pair(false, "Informe data e hora de fim ou deixe ambos em branco.")
            return
        }
        if (termino != null && !termino.isAfter(inicio)) {
            feedback = Pair(false, "A data de fim precisa ser posterior ao início.")
            return
        }
        if (cep.isBlank()) {
            feedback = Pair(false, "Informe o CEP do evento.")
            return
        }
        if (uf.length != 2) {
            feedback = Pair(false, "Informe a UF com 2 letras.")
            return
        }
        if (form.rua.isBlank() || form.bairro.isBlank() || form.cidade.isBlank()) {
            feedback = Pair(false, "Preencha rua, bairro e cidade do evento.")
            return
        }

        val dataHoraInicio = toApiDateTime(inicio)
        val dataHoraTermino = termino?.let { toApiDateTime(it) }

        isSubmitting = true
        feedback = null

        val request = CreateEventoRequest(
            titulo            = titulo,
            tipoEvento        = tipoEvento,
            constancia        = form.constancia.ifBlank { null },
            descricao         = form.descricao.trim().ifBlank { null },
            numMaxVoluntarios = maxVoluntarios,
            dataHoraInicio    = dataHoraInicio,
            dataHoraTermino   = dataHoraTermino,
            endereco = EnderecoRequest(
                rua          = form.rua.trim(),
                numero       = form.numero.trim().ifBlank { "S/N" },
                complemento  = form.complemento.trim(),
                bairro       = form.bairro.trim(),
                cidade       = form.cidade.trim(),
                uf           = uf,
                codigoPostal = cep,
            ),
        )

        val saveCallback: (Boolean, String) -> Unit = { success, message ->
            isSubmitting = false
            feedback = Pair(success, message)
            if (success) {
                form = EventoForm()
                if (eventoIdToEdit == null) {
                    loadMeusEventos()
                }
                onSaved?.invoke()
            }
        }

        if (eventoIdToEdit == null) {
            myViewModel.createEvento(request, saveCallback)
        } else {
            myViewModel.updateEvento(eventoIdToEdit, request, saveCallback)
        }
    }

    // ---------------------------------------------------------------------------
    // UI
    // ---------------------------------------------------------------------------

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (eventoIdToEdit == null) "Criar Evento" else "Editar Evento", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (eventoIdToEdit == null) {
                        IconButton(onClick = { loadMeusEventos() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Recarregar eventos")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CheerSurface,
                    titleContentColor = CheerText,
                ),
            )
        },
        containerColor = CheerBackground,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                EventosPanelHeader(
                    kicker = "INSTITUIÇÃO",
                    title = if (eventoIdToEdit == null) "Criar evento" else "Editar evento",
                    description = if (eventoIdToEdit == null) {
                        "Informe os detalhes para publicar uma nova oportunidade de voluntariado."
                    } else {
                        "Atualize os detalhes da oportunidade publicada."
                    },
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CheerSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        // Título
                        CheerTextField(
                            label = "Título do evento *",
                            value = form.titulo,
                            onValueChange = { updateField("titulo", it) },
                            placeholder = "Digite o título",
                        )

                        // Tipo + Frequência
                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            CheerDropdown(
                                label = "Tipo de evento *",
                                selected = form.tipoEvento,
                                options = listOf(
                                    "" to "Selecione",
                                    "doacao" to "Doação",
                                    "arrecadacao" to "Arrecadação",
                                    "organizacao" to "Organização",
                                    "preparacao" to "Preparação",
                                    "voluntariado" to "Voluntariado",
                                ),
                                onSelected = { updateField("tipoEvento", it) },
                            )
                            CheerDropdown(
                                label = "Frequência",
                                selected = form.constancia,
                                options = listOf(
                                    "" to "Selecione",
                                    "unico" to "Evento único",
                                    "semanal" to "Evento semanal",
                                    "mensal" to "Evento mensal",
                                ),
                                onSelected = { updateField("constancia", it) },
                            )
                        }

                        // Descrição
                        CheerTextArea(
                            label = "Descrição",
                            value = form.descricao,
                            onValueChange = { updateField("descricao", it) },
                            placeholder = "Descreva os detalhes da ação...",
                        )

                        // Máx. voluntários
                        CheerTextField(
                            label = "Número máximo de voluntários",
                            value = form.numMaxVoluntarios,
                            onValueChange = { updateField("numMaxVoluntarios", it) },
                            placeholder = "Quantidade",
                            keyboardType = KeyboardType.Number,
                        )

                        // Datas
                        val today = todayString()
                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            CheerTextField(
                                label = "Data de início *",
                                value = form.dataInicio,
                                onValueChange = { updateField("dataInicio", it) },
                                placeholder = today,
                                keyboardType = KeyboardType.Number,
                            )
                            CheerTextField(
                                label = "Hora de início *",
                                value = form.horaInicio,
                                onValueChange = { updateField("horaInicio", it) },
                                placeholder = "14:00",
                                keyboardType = KeyboardType.Number,
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            CheerTextField(
                                label = "Data de fim",
                                value = form.dataFim,
                                onValueChange = { updateField("dataFim", it) },
                                placeholder = today,
                                keyboardType = KeyboardType.Number,
                            )
                            CheerTextField(
                                label = "Hora de fim",
                                value = form.horaFim,
                                onValueChange = { updateField("horaFim", it) },
                                placeholder = "18:00",
                                keyboardType = KeyboardType.Number,
                            )
                        }

                        // Endereço
                        Spacer(Modifier.height(4.dp))
                        HorizontalDivider(color = CheerBrandBorder)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Endereço do evento",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CheerText,
                        )
                        Spacer(Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            CheerTextField(
                                label = "CEP *",
                                value = form.codigoPostal,
                                onValueChange = { updateField("codigoPostal", it) },
                                placeholder = "00000-000",
                                keyboardType = KeyboardType.Number,
                            )
                            CheerTextField(
                                label = "UF *",
                                value = form.uf,
                                onValueChange = { updateField("uf", it) },
                                placeholder = "SP",
                            )
                        }
                        CepStatusText(cepStatus)
                        CheerTextField(
                            label = "Rua *",
                            value = form.rua,
                            onValueChange = { updateField("rua", it) },
                            placeholder = "Rua das Flores",
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            CheerTextField(
                                label = "Número *",
                                value = form.numero,
                                onValueChange = { updateField("numero", it) },
                                placeholder = "123",
                                keyboardType = KeyboardType.Number,
                            )
                            CheerTextField(
                                label = "Complemento",
                                value = form.complemento,
                                onValueChange = { updateField("complemento", it) },
                                placeholder = "Apto 4",
                            )
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            CheerTextField(
                                label = "Bairro *",
                                value = form.bairro,
                                onValueChange = { updateField("bairro", it) },
                                placeholder = "Centro",
                            )
                            CheerTextField(
                                label = "Cidade *",
                                value = form.cidade,
                                onValueChange = { updateField("cidade", it) },
                                placeholder = "São Paulo",
                            )
                        }

                        // Feedback
                        feedback?.let { (isSuccess, message) ->
                            Spacer(Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSuccess) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = message,
                                    modifier = Modifier.padding(12.dp),
                                    color = if (isSuccess) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = { handleSubmit() },
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CheerPrimary),
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                )
                                Spacer(Modifier.width(8.dp))
                            } else {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(
                                text = if (isSubmitting) {
                                    if (eventoIdToEdit == null) "Criando evento..." else "Salvando evento..."
                                } else {
                                    if (eventoIdToEdit == null) "Criar evento" else "Salvar evento"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                            )
                        }
                    }
                }
            }

            if (eventoIdToEdit == null) Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                EventosPanelHeader(
                    kicker = "CHEER EVENTOS",
                    title = "Meus eventos",
                    description = "Acompanhe as ações publicadas pela sua instituição.",
                )

                when (listStatus) {
                    "loading" -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = CheerPrimary)
                                Spacer(Modifier.height(12.dp))
                                Text("Carregando eventos...", color = CheerMutedText)
                            }
                        }
                    }

                    "error" -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(listError ?: "Erro ao carregar.", color = CheerMutedText)
                                Spacer(Modifier.height(12.dp))
                                OutlinedButton(
                                    onClick = { loadMeusEventos() },
                                    shape = RoundedCornerShape(10.dp),
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Tentar novamente")
                                }
                            }
                        }
                    }

                    "loaded" -> {
                        if (eventos.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    "Sua instituição ainda não publicou eventos.",
                                    color = CheerMutedText,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                eventos.forEach { evento ->
                                    EventoListItem(evento = evento)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Componentes auxiliares
// ---------------------------------------------------------------------------

@Composable
private fun EventosPanelHeader(kicker: String, title: String, description: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = kicker,
            style = MaterialTheme.typography.labelMedium,
            color = CheerPrimary,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = CheerText,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = CheerMutedText,
        )
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun EventoListItem(evento: Evento) {
    val location = listOf(evento.cidade, evento.uf).filter { !it.isNullOrBlank() }.joinToString(" - ")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CheerSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = evento.titulo ?: "Evento sem titulo",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CheerText,
                    modifier = Modifier.weight(1f),
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = CheerPrimarySoft,
                ) {
                    Text(
                        text = "${evento.inscritos ?: 0} inscritos",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = CheerPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            EventoDetailRow(label = "Tipo", value = evento.tipoEvento ?: "—")
            EventoDetailRow(label = "Data", value = evento.dataInicio?.take(10)?.ifBlank { "Não informada" } ?: "Não informada")
            EventoDetailRow(label = "Local", value = location.ifBlank { "Não informado" })
        }
    }
}

@Composable
private fun EventoDetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = CheerText,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = CheerMutedText,
        )
    }
}

@Composable
private fun CepStatusText(status: Pair<Boolean, String>?) {
    status?.let { (success, message) ->
        Text(
            text = message,
            color = if (success) Color(0xFF2E7D32) else Color(0xFFC62828),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 12.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheerTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Column(modifier = modifier.padding(bottom = 12.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = CheerText, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = CheerMutedText) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = CheerBrandBorder,
                focusedBorderColor = CheerPrimary,
                unfocusedContainerColor = CheerSurface,
                focusedContainerColor = CheerSurface,
            ),
        )
    }
}

@Composable
private fun CheerTextArea(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    rows: Int = 4,
) {
    Column(modifier = modifier.padding(bottom = 12.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = CheerText, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = (rows * 24).dp),
            placeholder = { Text(placeholder, color = CheerMutedText) },
            maxLines = rows * 2,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = CheerBrandBorder,
                focusedBorderColor = CheerPrimary,
                unfocusedContainerColor = CheerSurface,
                focusedContainerColor = CheerSurface,
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CheerDropdown(
    modifier: Modifier = Modifier,
    label: String,
    selected: String,
    options: List<Pair<String, String>>,
    onSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val displayLabel = options.firstOrNull { it.first == selected }?.second ?: options.first().second

    Column(modifier = modifier.padding(bottom = 12.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = CheerText, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = displayLabel,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(10.dp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = CheerBrandBorder,
                    focusedBorderColor = CheerPrimary,
                    unfocusedContainerColor = CheerSurface,
                    focusedContainerColor = CheerSurface,
                ),
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (value, text) ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            onSelected(value)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CriarEventoScreenPreview() {
    com.cheermobile.ui.theme.CheerMobileTheme {
        CriarEventoScreen(onBackClick = {})
    }
}
