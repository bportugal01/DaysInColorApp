package com.example.daysincolors.Screens.Psicologo

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.daysincolors.Navigation.TopBar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@Composable
fun PacientesScreen(navController: NavController) {
    var isLoading by remember { mutableStateOf(true) }
    var pacientes by remember { mutableStateOf<List<Paciente>>(emptyList()) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                pacientes = result.map { document ->
                    Paciente(
                        id = document.id,
                        name = document.getString("name") ?: "Nome desconhecido",
                        status = document.getString("status") ?: "Status desconhecido"
                    )
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        color = Color(0xFFF8F8F8)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Meus Pacientes",
                fontSize = 25.sp,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF363636)
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.Black
                )
            } else {
                if (pacientes.isEmpty()) {
                    Text(
                        text = "Nenhum paciente registrado.",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    PacientesList(pacientes = pacientes, navController = navController)
                }
            }
        }
    }
}

@Composable
fun PacientesList(pacientes: List<Paciente>, navController: NavController) {
    val sortedPacientes = pacientes
        .sortedWith(compareBy({ it.status != "Ativo" }, { it.name }))

    LazyColumn {
        items(sortedPacientes) { paciente ->
            PacienteItem(paciente = paciente, navController = navController)
        }
    }
}

@Composable
fun PacienteItem(paciente: Paciente, navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8F8)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(2.dp, Color(0xFFFF0099))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = paciente.name,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF363636)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Status: ${paciente.status}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = if (paciente.status == "Ativo") Color(0xFF51A904) else Color(0xFFA92104)
                    )
                }

                Button(
                    onClick = { navController.navigate("diarios/${paciente.id}") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0099)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Ver Diários",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiariosScreen(pacienteId: String, navController: NavController) {
    var isLoading by remember { mutableStateOf(true) }
    var diarios by remember { mutableStateOf<List<Diario>>(emptyList()) }
    var feedbacks by remember { mutableStateOf<List<Feedback>>(emptyList()) }
    var newFeedbackText by remember { mutableStateOf("") }
    var showFeedbackField by remember { mutableStateOf(true) }

    val db = FirebaseFirestore.getInstance()
    val customColor = Color(0xFFFF0099)

    LaunchedEffect(pacienteId) {
        db.collection("users").document(pacienteId).collection("diarios")
            .get()
            .addOnSuccessListener { result ->
                diarios = result.map { document ->
                    val avaliacao = when (val value = document.get("avaliacao")) {
                        is String -> value
                        is Long -> value.toString()
                        is Double -> value.toString()
                        else -> "Valor desconhecido"
                    }

                    Diario(
                        data = document.getString("data") ?: "Data desconhecida",
                        cor = document.getString("cor") ?: "Cor desconhecida",
                        motivo = document.getString("motivo") ?: "Motivo desconhecido",
                        avaliacao = avaliacao
                    )
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }

        db.collection("users").document(pacienteId).collection("feedbacks")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Firestore", "Erro ao obter feedbacks", e)
                    return@addSnapshotListener
                }
                feedbacks = snapshot?.documents?.map { document ->
                    Feedback(
                        feedback = document.getString("feedback") ?: "Sem feedback",
                        data = document.getLong("data") ?: 0L,
                        psicologoNome = document.getString("psicologoNome") ?: "Desconhecido",
                        id = document.id
                    )
                } ?: emptyList()
            }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        color = Color(0xFFF8F8F8)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Diários",
                fontSize = 25.sp,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF363636)
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.Black
                )
            } else {
                if (diarios.isEmpty()) {
                    Text("Não há diários", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                } else {
                    LazyColumn {
                        items(diarios) { diario ->
                            DiarioItem(diario)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            Divider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                thickness = 1.dp
                            )
                        }
                        item {
                            Text(
                                text = "Feedback do Psicólogo",
                                fontSize = 25.sp,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF363636)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        items(feedbacks) { feedback ->
                            FeedbackCard(feedback, pacienteId, db, onFeedbackDeleted = {
                                showFeedbackField = true
                            })
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            if (showFeedbackField) {
                                OutlinedTextField(
                                    value = newFeedbackText,
                                    onValueChange = { newFeedbackText = it },
                                    label = { Text("Adicionar Feedback", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = customColor,
                                        unfocusedBorderColor = customColor.copy(alpha = 0.4f),
                                        cursorColor = customColor
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        if (newFeedbackText.isNotBlank()) {
                                            val feedback = Feedback(
                                                feedback = newFeedbackText,
                                                data = System.currentTimeMillis(),
                                                psicologoNome = "Dra. Marinês Romano",
                                                id = ""
                                            )

                                            val newFeedbackRef = db.collection("users").document(pacienteId).collection("feedbacks").document()
                                            newFeedbackRef.set(feedback)
                                                .addOnSuccessListener {
                                                    newFeedbackText = ""
                                                    showFeedbackField = false
                                                }
                                                .addOnFailureListener {
                                                    Log.w("Firestore", "Erro ao salvar feedback", it)
                                                }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = customColor),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Salvar", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiarioItem(diario: Diario) {
    val contentColor = Color(0xFF616161)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8F8)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(2.dp, Color(0xFFFF0099))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Data: ${diario.data}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF363636)
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Motivo: ${diario.motivo}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Cor: ${diario.cor}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Avaliação: ${diario.avaliacao}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackCard(
    feedback: Feedback,
    pacienteId: String,
    db: FirebaseFirestore,
    onFeedbackDeleted: () -> Unit
) {
    val customColor = Color(0xFFFF0099)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8F8)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(2.dp, customColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = feedback.feedback,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF363636)
                )
                Spacer(modifier = Modifier.height(4.dp))

                val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
                val formattedDate = dateFormat.format(Date(feedback.data))

                Text(
                    text = "Psicólogo: ${feedback.psicologoNome}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFF7B7B7B)
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Data: $formattedDate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF7B7B7B)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            db.collection("users").document(pacienteId)
                                .collection("feedbacks").document(feedback.id)
                                .delete()
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Feedback excluído com sucesso.")
                                    onFeedbackDeleted()
                                }
                                .addOnFailureListener {
                                    Log.w("Firestore", "Erro ao excluir feedback.", it)
                                }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Deletar Feedback",
                            tint = Color(0xFFFF4B4B)
                        )
                    }
                }
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

data class Diario(
    val data: String,
    val cor: String,
    val motivo: String,
    val avaliacao: String
)

data class Feedback(
    val feedback: String,
    val data: Long,
    val psicologoNome: String,
    val id: String
)

data class Paciente(
    val id: String,
    val name: String,
    val status: String,
    var diarios: MutableList<Diario> = mutableListOf()
)
