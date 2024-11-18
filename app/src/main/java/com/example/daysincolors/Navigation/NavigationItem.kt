package com.example.daysincolors.Navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import com.example.daysincolors.Screens.CadastroScreen
import com.example.daysincolors.Screens.Psicologo.HomePsicoScreen
import com.example.daysincolors.Screens.LoginScreen
import com.example.daysincolors.Screens.Paciente.CadastroDaysScreen
import com.example.daysincolors.Screens.Paciente.ConsultarScreen
import com.example.daysincolors.Screens.Paciente.EditarScreen
import com.example.daysincolors.Screens.Paciente.HomePacienteScreen
import com.example.daysincolors.Screens.Paciente.PerfilScreen
import com.example.daysincolors.Screens.Psicologo.DiariosScreen
import com.example.daysincolors.Screens.Psicologo.PacientesScreen
import com.google.firebase.auth.FirebaseAuth

sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : NavigationItem("home", Icons.Default.Home, "Home")
    object Cadastro : NavigationItem("cadastro1", Icons.Default.Create, "Cadastro")
    object Consultar : NavigationItem("consultar", Icons.Default.List, "Consultar")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val isLoggedIn = auth.currentUser != null
    val startDestination = if (isLoggedIn) "homePaciente" else "login"

    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("home") {
            HomePacienteScreen(navController = navController)
        }
        composable("cadastro1") {
            CadastroDaysScreen(navController = navController)
        }
        composable("consultar") {
            ConsultarScreen(navController = navController)
        }
        composable("cadastro") {
            CadastroScreen(navController = navController)
        }
        composable("homePaciente") {
            HomePacienteScreen(navController = navController)
        }

        composable(
            "editarPaciente/{registroId}",
            arguments = listOf(navArgument("registroId") { type = NavType.StringType })
        ) { backStackEntry ->
            val registroId = backStackEntry.arguments?.getString("registroId")
            if (registroId != null) {
                EditarScreen(navController = navController, registroId = registroId)
            } else {
                // Tratamento caso registroId seja nulo
                // Exibir uma mensagem de erro ou redirecionar para outra tela, se necessário
            }
        }

        composable("perfilPaciente") {
            PerfilScreen(navController = navController)
        }

        composable("homePsico") {
            HomePsicoScreen(navController = navController)
        }

        composable("pacientes") {
            PacientesScreen(navController = navController)
        }

        // Adicionando a rota para a tela de Diários do Paciente
        composable(
            "diarios/{pacienteId}",
            arguments = listOf(navArgument("pacienteId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pacienteId = backStackEntry.arguments?.getString("pacienteId")
            if (pacienteId != null) {
                DiariosScreen(pacienteId = pacienteId, navController = navController)
            } else {
                // Tratamento caso pacienteId seja nulo
            }
        }
    }
}
