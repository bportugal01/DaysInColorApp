package com.example.daysincolors.Screens.Psicologo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.daysincolors.R

@Composable
fun HomePsicoScreen(navController: NavController) {
    val psicologoNome = "Dra. Marinês Romano"
    // Cor personalizada para o tema (rosa)
    val customColor = Color(0xFFFF0099) // Rosa

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Conteúdo principal da tela
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Aplica o padding da Scaffold
            color = Color(0xFFF8F8F8) // Fundo de cor suave
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), // Padding adicional, se necessário
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo acima do botão
                Image(
                    painter = painterResource(id = R.drawable.daysincolors), // Substitua 'daysincolors' pelo nome correto
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp) // Ajuste o tamanho da logo conforme necessário
                        .padding(bottom = 32.dp) // Adicionando espaçamento abaixo da logo
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Saudação com nome do psicólogo
                Text(
                    text = "Olá, $psicologoNome!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = customColor // Cor rosa para o texto
                )

                Spacer(modifier = Modifier.height(30.dp)) // Ajuste no espaçamento entre o texto e o botão

                // Botão para acessar os pacientes
                Button(
                    onClick = { navController.navigate("pacientes") }, // Navega para a tela de pacientes
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = customColor) // Cor de fundo rosa
                ) {
                    Text(
                        text = "Meus Pacientes",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White) // Texto branco no botão
                    )
                }
            }
        }

        // Botão de logout no canto superior direito
        IconButton(
            onClick = { navController.navigate("login") }, // Navega para a tela de login
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd) // Posiciona no canto superior direito
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,  // Ícone de logout
                contentDescription = "Logout",
                tint = Color(0xFFFF0099) // Cor do ícone
            )
        }
    }
}
