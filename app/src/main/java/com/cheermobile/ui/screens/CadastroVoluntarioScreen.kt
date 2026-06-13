package com.cheermobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cheermobile.MyViewModel
import com.cheermobile.models.EnderecoRequest
import com.cheermobile.models.RegisterVoluntarioRequest
import com.cheermobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroVoluntarioScreen(
    onBackClick: () -> Unit,
    onSuccessNavigate: () -> Unit,
    myViewModel: MyViewModel = viewModel()
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var dataNascimento by remember { mutableStateOf("") }

    var cep by remember { mutableStateOf("") }
    var rua by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var bairro by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf("") }
    var uf by remember { mutableStateOf("") }

    var isSubmitting by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }

    // Validação: Senhas coincidem?
    val passwordsMatch = password == confirmPassword && password.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CheerPrimarySoft, CheerBackground, CheerAccentSoft)
                )
            )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Seja um Voluntário", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = CheerSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("Dados Pessoais", fontWeight = FontWeight.Bold, color = CheerPrimary, fontSize = 18.sp)
                            Spacer(Modifier.height(16.dp))

                            CustomField(value = nome, onValueChange = { nome = it }, label = "Nome Completo *")
                            CustomField(value = email, onValueChange = { email = it }, label = "E-mail *")
                            CustomField(value = cpf, onValueChange = { cpf = it }, label = "CPF *")
                            CustomField(value = dataNascimento, onValueChange = { dataNascimento = it }, label = "Nascimento (AAAA-MM-DD) *")

                            Spacer(Modifier.height(16.dp))
                            Text("Segurança", fontWeight = FontWeight.Bold, color = CheerPrimary, fontSize = 18.sp)
                            Spacer(Modifier.height(8.dp))

                            CustomField(value = password, onValueChange = { password = it }, label = "Senha *", isPassword = true)
                            CustomField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = "Confirmar Senha *",
                                isPassword = true,
                                isError = !passwordsMatch && confirmPassword.isNotEmpty()
                            )

                            if (!passwordsMatch && confirmPassword.isNotEmpty()) {
                                Text("As senhas não coincidem", color = Color.Red, fontSize = 12.sp)
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = CheerSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("Endereço", fontWeight = FontWeight.Bold, color = CheerPrimary, fontSize = 18.sp)
                            Spacer(Modifier.height(16.dp))

                            CustomField(value = cep, onValueChange = { cep = it }, label = "CEP *")
                            CustomField(value = rua, onValueChange = { rua = it }, label = "Rua *")
                            CustomField(value = bairro, onValueChange = { bairro = it }, label = "Bairro *")

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Box(modifier = Modifier.weight(1f)) { CustomField(value = numero, onValueChange = { numero = it }, label = "Nº") }
                                Spacer(Modifier.width(8.dp))
                                Box(modifier = Modifier.weight(1f)) { CustomField(value = uf, onValueChange = { uf = it }, label = "UF") }
                            }
                            CustomField(value = cidade, onValueChange = { cidade = it }, label = "Cidade *")
                        }
                    }

                    if (feedbackMessage.isNotEmpty()) {
                        Text(feedbackMessage, color = Color.Red, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(16.dp))
                    }

                    Button(
                        onClick = {
                            if (!passwordsMatch) {
                                feedbackMessage = "As senhas precisam ser iguais."
                                return@Button
                            }
                            isSubmitting = true
                            val endereco = EnderecoRequest(rua, numero, "", bairro, cidade, uf, cep)
                            val request = RegisterVoluntarioRequest(nome, email, password, null, cpf, null, "M", dataNascimento, endereco)

                            myViewModel.registerNewVoluntario(request) { success, message ->
                                isSubmitting = false
                                if (success) onSuccessNavigate()
                                else feedbackMessage = message
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !isSubmitting && passwordsMatch,
                        colors = ButtonDefaults.buttonColors(containerColor = CheerAccent),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isSubmitting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("CADASTRAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun CustomField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    isError: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = isError,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = CheerBrandBorder,
                focusedBorderColor = CheerPrimary
            )
        )
    }
}
