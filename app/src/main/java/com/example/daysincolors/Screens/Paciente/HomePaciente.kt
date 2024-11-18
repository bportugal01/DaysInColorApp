package com.example.daysincolors.Screens.Paciente

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.daysincolors.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePacienteScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val user = auth.currentUser
    val userId = user?.uid

    // Variável de estado para o nome do paciente
    var name by remember { mutableStateOf("Carregando...") }

    // Carregar o nome do paciente logado do Firestore
    LaunchedEffect(userId) {
        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        name = document.getString("name") ?: "Usuário sem nome"
                    }
                }
                .addOnFailureListener {
                    name = "Erro ao carregar nome"
                }
        }
    }

    // Cor personalizada para o tema (rosa)
    val customColor = Color(0xFFFF0099) // Rosa

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8F8) // Fundo de cor suave
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Ícone de perfil como botão no canto superior direito
            IconButton(
                onClick = { navController.navigate("perfilPaciente") }, // Navega para a tela de perfil
                modifier = Modifier
                    .size(60.dp) // Tamanho do ícone ajustado
                    .align(Alignment.TopEnd) // Posiciona no canto superior direito
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Ícone de perfil",
                    modifier = Modifier.size(60.dp), // Tamanho ajustado do ícone
                    tint = customColor // Cor rosa para o ícone
                )
            }

            // Conteúdo principal da tela
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Logo acima do botão
                Image(
                    painter = painterResource(id = R.drawable.daysincolors), // Substitua 'logo' pelo nome real da sua logo no diretório drawable
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp) // Ajuste o tamanho da logo conforme necessário
                        .padding(bottom = 32.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Saudação
                Text(
                    text = "Olá, $name", // Exibe o nome do paciente logado
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = customColor // Cor rosa para o texto
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pergunta
                Text(
                    text = "Como foi seu dia?",
                    fontSize = 20.sp,
                    color = Color.Gray // Cor cinza suave para o texto
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botão de registrar
                Button(
                    onClick = { navController.navigate("cadastro1") }, // Navega para a tela de testes
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = customColor) // Cor de fundo rosa
                ) {
                    Text(text = "Registrar", fontSize = 18.sp, color = Color.White) // Texto branco no botão
                }
            }
        }
    }
}
