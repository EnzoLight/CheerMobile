package com.cheermobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cheermobile.MyViewModel
import com.cheermobile.models.EnderecoRequest
import com.cheermobile.models.RegisterInstituicaoRequest
import com.cheermobile.ui.theme.*

// ---------------------------------------------------------------------------
// Estado do formulário
// ---------------------------------------------------------------------------

private data class EnderecoForm(
    val codigoPostal: String = "",
    val rua: String = "",
    val numero: String = "",
    val complemento: String = "",
    val bairro: String = "",
    val cidade: String = "",
    val uf: String = "",
)

private data class CadastroInstituicaoForm(
    val nome: String = "",
    val email: String = "",
    val telefone: String = "",
    val cnpj: String = "",
    val tipo: String = "",
    val categoria: String = "",
    val anoFundacao: String = "",
    val internacional: String = "",   // "", "true", "false"
    val password: String = "",
    val passwordConfirmation: String = "",
    val endereco: EnderecoForm = EnderecoForm(),
)

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

private fun onlyDigits(value: String) = value.filter { it.isDigit() }

private val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

// ---------------------------------------------------------------------------
// Tela principal
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroInstituicaoScreen(
    onBackClick: () -> Unit = {},
    onSuccessNavigate: () -> Unit = {},
    myViewModel: MyViewModel = viewModel(),
) {
    var form by remember { mutableStateOf(CadastroInstituicaoForm()) }
    var feedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }   // true = sucesso
    var isSubmitting by remember { mutableStateOf(false) }

    // Validações de senha em tempo real
    val password = form.password
    val validations = mapOf(
        "hasUpper"   to Regex("[A-Z]").containsMatchIn(password),
        "hasNumber"  to Regex("\\d").containsMatchIn(password),
        "hasSpecial" to Regex("[!@#\$%^&*]").containsMatchIn(password),
        "minLength"  to (password.length >= 8),
    )
    val hasValidPassword = validations.values.all { it }

    fun updateField(field: String, value: String) {
        form = when (field) {
            "nome"                 -> form.copy(nome = value)
            "email"                -> form.copy(email = value)
            "telefone"             -> form.copy(telefone = value)
            "cnpj"                 -> form.copy(cnpj = value)
            "tipo"                 -> form.copy(tipo = value)
            "categoria"            -> form.copy(categoria = value)
            "anoFundacao"          -> form.copy(anoFundacao = value)
            "internacional"        -> form.copy(internacional = value)
            "password"             -> form.copy(password = value)
            "passwordConfirmation" -> form.copy(passwordConfirmation = value)
            else                   -> form
        }
        feedback = null
    }

    fun updateEndereco(field: String, value: String) {
        val e = form.endereco
        form = form.copy(
            endereco = when (field) {
                "codigoPostal" -> e.copy(codigoPostal = value)
                "rua"          -> e.copy(rua = value)
                "numero"       -> e.copy(numero = value)
                "complemento"  -> e.copy(complemento = value)
                "bairro"       -> e.copy(bairro = value)
                "cidade"       -> e.copy(cidade = value)
                "uf"           -> e.copy(uf = value)
                else           -> e
            }
        )
        feedback = null
    }

    fun handleSubmit() {
        // 1. Validações básicas de segurança antes de enviar
        if (onlyDigits(form.cnpj).length != 14) {
            feedback = Pair(false, "Informe um CNPJ válido com 14 números.")
            return
        }
        if (!hasValidPassword) {
            feedback = Pair(false, "A senha precisa atender a todos os requisitos informados.")
            return
        }
        if (form.password != form.passwordConfirmation) {
            feedback = Pair(false, "A confirmação de senha não corresponde à senha criada.")
            return
        }

        isSubmitting = true
        feedback = null

        // 2. O bloco que você pediu implementado corretamente aqui dentro:
        // Criamos o endereço coletando os dados que o usuário digitou nos inputs da tela
        val endereco = EnderecoRequest(
            rua          = form.endereco.rua.trim(),
            numero       = form.endereco.numero.trim(),
            complemento  = form.endereco.complemento.trim(),
            bairro       = form.endereco.bairro.trim(),
            cidade       = form.endereco.cidade.trim(),
            uf           = form.endereco.uf.trim().uppercase(),
            codigoPostal = onlyDigits(form.endereco.codigoPostal) // Use o nome exato da sua Model
        )

        // Criamos a requisição principal da instituição usando os dados do formulário
        val request = RegisterInstituicaoRequest(
            nome        = form.nome.trim(),
            email       = form.email.trim(),
            password    = form.password, // Sua tela já possui o campo password mapeado!
            cnpj        = onlyDigits(form.cnpj),
            endereco    = endereco,
            // Mantendo os opcionais que sua model pede para não quebrar o construtor:
            telefone    = form.telefone.ifBlank { null }?.let { onlyDigits(it) },
            tipo        = form.tipo.trim().ifBlank { null },
            categoria   = form.categoria.trim().ifBlank { null },
            anoFundacao = form.anoFundacao.toIntOrNull(),
            internacional = when (form.internacional) {
                "true"  -> true
                "false" -> false
                else    -> null
            }
        )

        // 3. Execução da chamada na ViewModel enviando o request criado acima
        myViewModel.registerInstituicao(request) { success, message ->
            isSubmitting = false
            feedback = Pair(success, message) // Mostra a caixinha de aviso na tela se falhar ou der certo

            if (success) {
                form = CadastroInstituicaoForm() // Limpa os campos após o sucesso
                onSuccessNavigate() // Dispara a navegação (que vai te levar para "criar_evento" via MainActivity)
            }
        }
    }

    // ---------------------------------------------------------------------------
    // UI
    // ---------------------------------------------------------------------------

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cadastro de Instituição", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
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
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Cabeçalho colorido ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(listOf(CheerPrimarySoft, CheerBackground))
                    )
                    .padding(horizontal = 24.dp, vertical = 28.dp),
            ) {
                Column {
                    Text(
                        text = "CHEER INSTITUIÇÃO",
                        color = CheerPrimary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Cadastre sua instituição",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = CheerText,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Conecte sua organização a voluntários e publique oportunidades de impacto.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CheerMutedText,
                    )

                    // Painel de requisitos de senha
                    Spacer(Modifier.height(20.dp))
                    PasswordRequirementsPanel(validations)
                }
            }

            // ── Formulário ──────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CheerSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    FormSectionTitle("Dados institucionais", "Informe os dados da organização.")

                    CheerTextField(
                        label = "Nome da instituição *",
                        value = form.nome,
                        onValueChange = { updateField("nome", it) },
                        placeholder = "Instituto Esperança",
                    )
                    CheerTextField(
                        label = "E-mail institucional *",
                        value = form.email,
                        onValueChange = { updateField("email", it) },
                        placeholder = "contato@instituto.org",
                        keyboardType = KeyboardType.Email,
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CheerTextField(
                            modifier = Modifier.weight(1f),
                            label = "Telefone (opcional)",
                            value = form.telefone,
                            onValueChange = { updateField("telefone", it) },
                            placeholder = "(00) 00000-0000",
                            keyboardType = KeyboardType.Phone,
                        )
                        CheerTextField(
                            modifier = Modifier.weight(1f),
                            label = "CNPJ *",
                            value = form.cnpj,
                            onValueChange = { updateField("cnpj", it) },
                            placeholder = "00.000.000/0001-00",
                            keyboardType = KeyboardType.Number,
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CheerTextField(
                            modifier = Modifier.weight(1f),
                            label = "Tipo (opcional)",
                            value = form.tipo,
                            onValueChange = { updateField("tipo", it) },
                            placeholder = "ONG",
                        )
                        CheerTextField(
                            modifier = Modifier.weight(1f),
                            label = "Categoria (opcional)",
                            value = form.categoria,
                            onValueChange = { updateField("categoria", it) },
                            placeholder = "Educação",
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CheerTextField(
                            modifier = Modifier.weight(1f),
                            label = "Ano de fundação (opcional)",
                            value = form.anoFundacao,
                            onValueChange = { updateField("anoFundacao", it) },
                            placeholder = "2010",
                            keyboardType = KeyboardType.Number,
                        )
                        CheerDropdown(
                            modifier = Modifier.weight(1f),
                            label = "Atuação internacional",
                            selected = form.internacional,
                            options = listOf("" to "Não informado", "false" to "Não", "true" to "Sim"),
                            onSelected = { updateField("internacional", it) },
                        )
                    }

                    // Senha
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = CheerBrandBorder)
                    Spacer(Modifier.height(16.dp))
                    FormSectionTitle("Segurança", "Crie uma senha de acesso.")

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CheerTextField(
                            modifier = Modifier.weight(1f),
                            label = "Senha *",
                            value = form.password,
                            onValueChange = { updateField("password", it) },
                            placeholder = "Crie uma senha",
                            isPassword = true,
                        )
                        CheerTextField(
                            modifier = Modifier.weight(1f),
                            label = "Confirmar senha *",
                            value = form.passwordConfirmation,
                            onValueChange = { updateField("passwordConfirmation", it) },
                            placeholder = "Confirme sua senha",
                            isPassword = true,
                        )
                    }

                    // Endereço
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = CheerBrandBorder)
                    Spacer(Modifier.height(16.dp))
                    FormSectionTitle("Endereço da instituição", "Local de atuação da organização.")

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CheerTextField(
                            modifier = Modifier.weight(1f),
                            label = "CEP *",
                            value = form.endereco.codigoPostal,
                            onValueChange = { updateEndereco("codigoPostal", it) },
                            placeholder = "00000-000",
                            keyboardType = KeyboardType.Number,
                        )
                        CheerTextField(
                            modifier = Modifier.weight(1f),
                            label = "UF *",
                            value = form.endereco.uf,
                            onValueChange = { updateEndereco("uf", it.take(2)) },
                            placeholder = "SP",
                        )
                    }

                    CheerTextField(
                        label = "Rua *",
                        value = form.endereco.rua,
                        onValueChange = { updateEndereco("rua", it) },
                        placeholder = "Rua das Flores",
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CheerTextField(
                            modifier = Modifier.weight(1f),
                            label = "Número *",
                            value = form.endereco.numero,
                            onValueChange = { updateEndereco("numero", it) },
                            placeholder = "123",
                            keyboardType = KeyboardType.Number,
                        )
                        CheerTextField(
                            modifier = Modifier.weight(1f),
                            label = "Complemento",
                            value = form.endereco.complemento,
                            onValueChange = { updateEndereco("complemento", it) },
                            placeholder = "Apto 4",
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CheerTextField(
                            modifier = Modifier.weight(1f),
                            label = "Bairro *",
                            value = form.endereco.bairro,
                            onValueChange = { updateEndereco("bairro", it) },
                            placeholder = "Centro",
                        )
                        CheerTextField(
                            modifier = Modifier.weight(1f),
                            label = "Cidade *",
                            value = form.endereco.cidade,
                            onValueChange = { updateEndereco("cidade", it) },
                            placeholder = "São Paulo",
                        )
                    }

                    // Feedback
                    feedback?.let { (isSuccess, message) ->
                        Spacer(Modifier.height(16.dp))
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
                            text = if (isSubmitting) "Cadastrando instituição..." else "Cadastrar instituição",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ---------------------------------------------------------------------------
// Componentes auxiliares
// ---------------------------------------------------------------------------

@Composable
private fun FormSectionTitle(title: String, subtitle: String) {
    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CheerText)
    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = CheerMutedText)
    Spacer(Modifier.height(12.dp))
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
    isPassword: Boolean = false,
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
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
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

@Composable
private fun PasswordRequirementsPanel(validations: Map<String, Boolean>) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CheerSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Sua senha precisa ter:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = CheerText,
            )
            Spacer(Modifier.height(8.dp))
            PasswordRule(met = validations["hasUpper"] == true,  label = "Uma letra maiúscula")
            PasswordRule(met = validations["hasNumber"] == true, label = "Pelo menos um número")
            PasswordRule(met = validations["minLength"] == true, label = "Mínimo de 8 caracteres")
            PasswordRule(met = validations["hasSpecial"] == true, label = "Um caractere especial (!@#\$%^&*)")
        }
    }
}

@Composable
private fun PasswordRule(met: Boolean, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 3.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = if (met) CheerSecondary else CheerBrandBorder,
            modifier = Modifier.size(18.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (met) Text("✓", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (met) CheerText else CheerMutedText,
        )
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CadastroInstituicaoScreenPreview() {
    com.cheermobile.ui.theme.CheerMobileTheme {
        CadastroInstituicaoScreen(
            onBackClick = {},
            onSuccessNavigate = {},
        )
    }
}
