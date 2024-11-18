package com.example.daysincolors.Screens.Paciente

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val user = auth.currentUser
    val userId = user?.uid

    // Variáveis de estado para os dados do perfil
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    // Flags para editar
    val isEditing = remember { mutableStateOf(false) }

    // Dialog para confirmar a exclusão
    val showDialog = remember { mutableStateOf(false) }

    // Carregar os dados do usuário do Firestore
    LaunchedEffect(userId) {
        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        name = document.getString("name") ?: "Sem nome"
                        email = document.getString("email") ?: "Sem email"
                        status = document.getString("status") ?: "Ativo"
                    }
                }
        }
    }

    val context = LocalContext.current // Obter o contexto do Composable

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8F8) // Fundo de cor suave
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícone de conta acima do nome
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Ícone de Conta",
                modifier = Modifier.size(100.dp),
                tint = Color(0xFFFF0099)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Título da tela
            Text(
                text = "Perfil de ${name}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            Divider(
                color = Color(0xFFFF0099),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .height(2.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Nome
            Text(
                text = "Nome:",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )
            ProfileField(label = "", value = name, isEditing = isEditing.value) { newValue ->
                name = newValue
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Campo de Email
            Text(
                text = "Email:",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )
            ProfileField(label = "", value = email, isEditing = isEditing.value) { newValue ->
                email = newValue
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Status do usuário
            Text(
                text = "Status: ${status}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (status == "Ativo") Color(0xFF51A904) else Color(0xFFA92104)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botões de ação: Editar, Excluir, Sair em Cards
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Card do botão de editar
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF0099) // Muda a cor do card para branco
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (isEditing.value) {
                                updateProfile(userId, name, email, senha, db, context)
                            }
                            isEditing.value = !isEditing.value // Alterna entre editar e não editar
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (isEditing.value) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditing.value) "Salvar" else "Editar",
                            tint = Color.White
                        )
                    }
                }

                // Card do botão de excluir
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF0099) // Muda a cor do card para branco
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    IconButton(
                        onClick = {
                            showDialog.value = true // Mostra a confirmação de exclusão
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = Color.White
                        )
                    }
                }

                // Card do botão de sair
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF0099) // Muda a cor do card para branco
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    IconButton(
                        onClick = {
                            auth.signOut() // Sair do Firebase
                            navController.navigate("login") // Navega para a tela de login
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sair",
                            tint = Color.White
                        )
                    }
                }
            }

            Divider(
                color = Color(0xFFFF0099),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .height(2.dp)
            )
        }
    }

    // Confirmar exclusão
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = {
                Text(
                    text = "Confirmar Exclusão",
                    color = Color(0xFFFF0099) // Título rosa
                )
            },
            text = {
                Text(
                    text = "Você tem certeza que deseja desativar sua conta?",
                    color = Color.Black // Cor do texto
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        userId?.let {
                            // Atualizar o status do usuário para "Inativo"
                            val userRef = db.collection("users").document(it)
                            userRef.update("status", "Inativo")
                                .addOnSuccessListener {
                                    // Exclui a conta após atualizar o status
                                    auth.currentUser?.delete()?.addOnSuccessListener {
                                        navController.navigate("login")
                                        // Exibe o Toast aqui dentro de um Composable
                                        Toast.makeText(context, "Conta desativada com sucesso", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        showDialog.value = false
                    }
                ) {
                    Text(
                        text = "Desativar",
                        color = Color(0xFFFF0099) // Cor do botão confirmar
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        color = Color.Gray // Cor do botão cancelar
                    )
                }
            },
            containerColor = Color(0xFFF0F0F0), // Cor de fundo do AlertDialog
            textContentColor = Color.Black // Cor do conteúdo
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileField(label: String, value: String, isEditing: Boolean, onValueChange: (String) -> Unit) {
    // Cor personalizada
    val corPersonalizada = Color(0xFFFF0099)

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.Gray
        )
        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(label) },
                maxLines = 1,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = corPersonalizada,
                    unfocusedBorderColor = corPersonalizada.copy(alpha = 0.4f),
                    cursorColor = corPersonalizada
                )
            )
        } else {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

fun updateProfile(userId: String?, name: String, email: String, senha: String, db: FirebaseFirestore, context: Context) {
    val userRef = db.collection("users").document(userId ?: return)
    val updatedData = hashMapOf(
        "name" to name,
        "email" to email,
        "status" to "Ativo"
    )

    userRef.update(updatedData as Map<String, Any>)
        .addOnSuccessListener {
            // Exibe o Toast aqui dentro de um Composable
            Toast.makeText(context, "Perfil atualizado com sucesso", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            // Exibe o Toast aqui dentro de um Composable
            Toast.makeText(context, "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show()
        }
}
