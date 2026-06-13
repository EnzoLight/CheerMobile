package com.cheermobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
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


/** Monta datetime com offset local, ex: "2025-06-10T14:00:00-03:00" */
private fun toApiDateTime(date: String, time: String): String? {
    if (date.isBlank() || time.isBlank()) return null
    return try {
        val localDt = LocalDateTime.parse("${date}T${time}:00")
        val zoneOffset = ZoneOffset.systemDefault().rules.getOffset(localDt)
        val offsetStr = zoneOffset.toString().let { if (it == "Z") "+00:00" else it }
        "${date}T${time}:00$offsetStr"
    } catch (e: Exception) {
        null
    }
}

// ---------------------------------------------------------------------------
// Tela principal
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarEventoScreen(
    onBackClick: () -> Unit = {},
    myViewModel: MyViewModel = viewModel(),
) {
    var form by remember { mutableStateOf(EventoForm()) }
    var feedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Lista de eventos da instituição
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
    var listStatus by remember { mutableStateOf("loading") }   // loading | loaded | error
    var listError by remember { mutableStateOf<String?>(null) }

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


    LaunchedEffect(Unit) { loadMeusEventos() }

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
            "codigoPostal"       -> form.copy(codigoPostal = value)
            "rua"                -> form.copy(rua = value)
            "numero"             -> form.copy(numero = value)
            "complemento"        -> form.copy(complemento = value)
            "bairro"             -> form.copy(bairro = value)
            "cidade"             -> form.copy(cidade = value)
            "uf"                 -> form.copy(uf = value.take(2))
            else                 -> form
        }
        feedback = null
    }

    fun handleSubmit() {
        val dataHoraInicio = toApiDateTime(form.dataInicio, form.horaInicio)
        val dataHoraTermino = toApiDateTime(form.dataFim, form.horaFim)

        // Validações (espelham o JS)
        if (dataHoraInicio == null) {
            feedback = Pair(false, "Informe data e hora de início válidas.")
            return
        }
        if (LocalDateTime.parse(dataHoraInicio.substringBefore("+").substringBefore("-", "").let {
                // pega a parte antes do offset
                dataHoraInicio.take(19)
            }).isBefore(LocalDateTime.now())) {
            feedback = Pair(false, "A data de início não pode ser anterior ao momento atual.")
            return
        }
        if ((form.dataFim.isNotBlank() || form.horaFim.isNotBlank()) && dataHoraTermino == null) {
            feedback = Pair(false, "Informe data e hora de fim ou deixe ambos em branco.")
            return
        }
        if (dataHoraTermino != null &&
            !LocalDateTime.parse(dataHoraTermino.take(19))
                .isAfter(LocalDateTime.parse(dataHoraInicio.take(19)))
        ) {
            feedback = Pair(false, "A data de fim precisa ser posterior ao início.")
            return
        }

        isSubmitting = true
        feedback = null

        val request = CreateEventoRequest(
            titulo            = form.titulo.trim(),
            tipoEvento        = form.tipoEvento,
            constancia        = form.constancia.ifBlank { null },
            descricao         = form.descricao.trim().ifBlank { null },
            numMaxVoluntarios = form.numMaxVoluntarios.toIntOrNull(),
            dataHoraInicio    = dataHoraInicio,
            dataHoraTermino   = dataHoraTermino,
            endereco = EnderecoRequest(
                rua          = form.rua.trim(),
                numero       = form.numero.trim(),
                complemento  = form.complemento.trim(),
                bairro       = form.bairro.trim(),
                cidade       = form.cidade.trim(),
                uf           = form.uf.trim().uppercase(),
                codigoPostal = onlyDigits(form.codigoPostal),
            ),
        )

        myViewModel.createEvento(request) { success, message ->
            isSubmitting = false
            feedback = Pair(success, message)
            if (success) {
                form = EventoForm()
                loadMeusEventos()
            }
        }
    }

    // ---------------------------------------------------------------------------
    // UI
    // ---------------------------------------------------------------------------

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Evento", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = CheerText,
                ),
            )
        },
        containerColor = CheerBackground,
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            // ── Painel esquerdo: formulário ─────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                EventosPanelHeader(
                    kicker = "INSTITUIÇÃO",
                    title = "Criar evento",
                    description = "Informe os detalhes para publicar uma nova oportunidade de voluntariado.",
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CheerDropdown(
                                modifier = Modifier.weight(1f),
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
                                modifier = Modifier.weight(1f),
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
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CheerTextField(
                                modifier = Modifier.weight(1f),
                                label = "Data de início *",
                                value = form.dataInicio,
                                onValueChange = { updateField("dataInicio", it) },
                                placeholder = today,
                                keyboardType = KeyboardType.Number,
                            )
                            CheerTextField(
                                modifier = Modifier.weight(1f),
                                label = "Hora de início *",
                                value = form.horaInicio,
                                onValueChange = { updateField("horaInicio", it) },
                                placeholder = "14:00",
                                keyboardType = KeyboardType.Number,
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CheerTextField(
                                modifier = Modifier.weight(1f),
                                label = "Data de fim",
                                value = form.dataFim,
                                onValueChange = { updateField("dataFim", it) },
                                placeholder = today,
                                keyboardType = KeyboardType.Number,
                            )
                            CheerTextField(
                                modifier = Modifier.weight(1f),
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

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CheerTextField(
                                modifier = Modifier.weight(1f),
                                label = "CEP *",
                                value = form.codigoPostal,
                                onValueChange = { updateField("codigoPostal", it) },
                                placeholder = "00000-000",
                                keyboardType = KeyboardType.Number,
                            )
                            CheerTextField(
                                modifier = Modifier.weight(1f),
                                label = "UF *",
                                value = form.uf,
                                onValueChange = { updateField("uf", it) },
                                placeholder = "SP",
                            )
                        }
                        CheerTextField(
                            label = "Rua *",
                            value = form.rua,
                            onValueChange = { updateField("rua", it) },
                            placeholder = "Rua das Flores",
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CheerTextField(
                                modifier = Modifier.weight(1f),
                                label = "Número *",
                                value = form.numero,
                                onValueChange = { updateField("numero", it) },
                                placeholder = "123",
                                keyboardType = KeyboardType.Number,
                            )
                            CheerTextField(
                                modifier = Modifier.weight(1f),
                                label = "Complemento",
                                value = form.complemento,
                                onValueChange = { updateField("complemento", it) },
                                placeholder = "Apto 4",
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CheerTextField(
                                modifier = Modifier.weight(1f),
                                label = "Bairro *",
                                value = form.bairro,
                                onValueChange = { updateField("bairro", it) },
                                placeholder = "Centro",
                            )
                            CheerTextField(
                                modifier = Modifier.weight(1f),
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
                            }
                            Text(
                                text = if (isSubmitting) "Criando evento..." else "Criar evento",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }

            // ── Painel direito: lista de meus eventos ───────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
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
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
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
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
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
