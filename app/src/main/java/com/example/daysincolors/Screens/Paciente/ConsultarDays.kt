package com.example.daysincolors.Screens.Paciente

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConsultarScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance() // Firebase Auth para pegar o usuário logado
    val firestore = FirebaseFirestore.getInstance() // Firebase Firestore
    val user = auth.currentUser // Usuário logado

    // Estado para armazenar os registros e feedbacks
    val registros = remember { mutableStateOf<List<RegistroDia>>(emptyList()) }
    val feedbacks = remember { mutableStateOf<List<Feedback>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Carrega os registros e feedbacks do Firestore assim que a tela é composta
    LaunchedEffect(Unit) {
        // Verifica se o usuário está logado
        user?.let { loggedInUser ->
            val userId = loggedInUser.uid // Pegando o ID do usuário logado

            // Carrega os registros do Firestore
            firestore.collection("users")
                .document(userId) // Referência ao documento do usuário
                .collection("diarios") // Coleção de diários dentro do documento do usuário
                .get()
                .addOnSuccessListener { result ->
                    val fetchedRegistros = result.map { document ->
                        // Converte os dados do Firestore para o modelo de RegistroDia
                        val avaliacao = document.get("avaliacao")
                        val avaliacaoFloat = when (avaliacao) {
                            is String -> avaliacao.toFloatOrNull() ?: 0f // Converte String para Float ou usa 0f
                            is Number -> avaliacao.toFloat() // Converte Number para Float
                            else -> 0f // Valor padrão caso o tipo não seja compatível
                        }

                        RegistroDia(
                            id = document.id, // ID agora é String
                            data = document.getString("data") ?: "",
                            cor = document.getString("cor") ?: "",
                            motivo = document.getString("motivo") ?: "",
                            avaliacao = avaliacaoFloat // Aqui você usa o valor convertido corretamente
                        )
                    }
                    registros.value = fetchedRegistros
                    isLoading.value = false
                }
                .addOnFailureListener {
                    errorMessage.value = "Erro ao carregar os registros."
                    isLoading.value = false
                }

            // Carrega os feedbacks do psicólogo
            firestore.collection("users")
                .document(userId)
                .collection("feedbacks") // Coleção de feedbacks do psicólogo
                .get()
                .addOnSuccessListener { result ->
                    val fetchedFeedbacks = result.map { document ->
                        val timestamp = document.get("data") as? Long
                        val feedbackDate = timestamp?.let { Date(it) } ?: Date() // Converte timestamp para Date
                        Feedback(
                            feedback = document.getString("feedback") ?: "",
                            data = feedbackDate
                        )
                    }
                    feedbacks.value = fetchedFeedbacks
                }
                .addOnFailureListener {
                    errorMessage.value = "Erro ao carregar os feedbacks."
                }
        } ?: run {
            errorMessage.value = "Usuário não está logado."
            isLoading.value = false
        }
    }

    // Se houver um erro, exibe uma mensagem
    errorMessage.value?.let {
        LaunchedEffect(it) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            errorMessage.value = null // Limpa a mensagem após exibir o Toast
        }
    }

    // Envolvendo o conteúdo da tela com o Surface para o fundo suave
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8F8) // Fundo de cor suave
    ) {
        // Se não houver registros, exibe uma mensagem
        if (registros.value.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Não há registros disponíveis.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Exibe a seção de registros com título "Meus Days in Colors"
                item {
                    Text(
                        text = "Meus Days in Colors",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                        color = Color(0xFF020000),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    registros.value.forEach { registro ->
                        RegistroCard(
                            registro = registro,
                            onEditClick = {
                                navController.navigate("editarPaciente/${registro.id}") // Corrigido para usar registro.id
                            },
                            onDeleteClick = {
                                // Ação de excluir
                                user?.let { loggedInUser ->
                                    val userId = loggedInUser.uid
                                    firestore.collection("users")
                                        .document(userId)
                                        .collection("diarios")
                                        .document(registro.id)
                                        .delete()
                                        .addOnSuccessListener {
                                            // Atualiza a lista de registros após a exclusão
                                            registros.value = registros.value.filterNot { it.id == registro.id }
                                            Toast.makeText(context, "Registro excluído com sucesso", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Erro ao excluir o registro", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))


                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }

                // Exibe a seção de feedbacks com título "Feedbacks do Psicólogo"
                item {
                    Text(
                        text = "Feedbacks do Psicólogo",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                        color = Color(0xFF000000),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    feedbacks.value.forEach { feedback ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            border = BorderStroke(2.dp,  color = Color(0xFFFF0099)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White) // Fundo branco
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Feedback do Psicólogo",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF0099)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Data: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(feedback.data)}", // Formatação da data
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                                    color = Color(0xFF616161)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = feedback.feedback,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                                    color = Color(0xFF616161)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}



data class Feedback(
    val feedback: String,
    val data: Date
)


@Composable
fun RegistroCard(
    registro: RegistroDia,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val contentColor = Color(0xFF616161)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8F8) // Fundo de cor suave
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            border = BorderStroke(2.dp, Color(0xFFFF0099)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White) // Mantém o fundo branco
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Exibe o título do registro
                Text(
                    text = "Registro de ${registro.data}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF0099)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Exibe as informações do registro, incluindo a cor dinâmica para o texto "Cor"
                Text(
                    text = "Cor: ${getColorName(registro.cor)} (${registro.cor})",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    color = getColorByLabel(registro.cor),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Motivo: ${registro.motivo}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Avaliação: ${registro.avaliacao}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    color = contentColor
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Botões de editar e excluir dentro de Cards
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Card para o botão de editar
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .width(50.dp), // Define uma largura fixa
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF0099) // Muda a cor do card para o rosa
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color.White)
                        }
                    }

                    // Card para o botão de excluir
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .width(50.dp), // Define uma largura fixa
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF0099) // Muda a cor do card para o rosa
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        IconButton(onClick = onDeleteClick) {
                            Icon(Icons.Filled.Delete, contentDescription = "Excluir", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// Função que retorna a cor baseada na descrição textual
fun getColorByLabel(label: String): Color {
    return when (label) {
        "Triste" -> Color(0xFF0008D1)
        "Estressante" -> Color(0xFFFF0303)
        "Animado" -> Color(0xFFFFD809)
        "Normal" -> Color(0xFF737373)
        "Tranquilo" -> Color(0xFF7ED957)
        "Solitário" -> Color.Black
        "Engraçado" -> Color(0xFFFF914D)
        "Frustrante" -> Color(0xFFFF66C4)
        else -> Color.Gray
    }
}

// Função que retorna o nome da cor para exibição
fun getColorName(label: String): String {
    return when (label) {
        "Triste" -> "Azul Escuro"
        "Estressante" -> "Vermelho"
        "Animado" -> "Amarelo"
        "Normal" -> "Cinza"
        "Tranquilo" -> "Verde Claro"
        "Solitário" -> "Preto"
        "Engraçado" -> "Laranja"
        "Frustrante" -> "Rosa"
        else -> "Indefinido"
    }
}



data class RegistroDia(
    val id: String, // ID agora é String
    val data: String,
    val cor: String,
    val motivo: String,
    val avaliacao: Float // Avaliação agora é Float
)
