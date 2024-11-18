@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val isLoggedIn = auth.currentUser != null
    val startDestination = if (isLoggedIn) "homePaciente" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController = navController) }
        composable("homePaciente") { HomePacienteScreen(navController = navController) }
        composable("homePsico") { HomePsicoScreen(navController = navController) }
        composable("cadastro") { CadastroScreen(navController = navController) }
        composable("profile") { ConsultarScreen(navController = navController) }
        composable("detalhes") { DetalhesScreen(navController = navController) }

        composable(
            "editarPaciente/{registroId}",
            arguments = listOf(navArgument("registroId") { type = NavType.StringType })
        ) { backStackEntry ->
            val registroId = backStackEntry.arguments?.getString("registroId")
            if (registroId != null) {
                EditarScreen(navController = navController, registroId = registroId)
            }
        }
        
        composable(
            "weekDetails/{weekStartDate}",
            arguments = listOf(navArgument("weekStartDate") { type = NavType.StringType })
        ) { backStackEntry ->
            val weekStartDate = backStackEntry.arguments?.getString("weekStartDate")
            if (weekStartDate != null) {
                WeekDetailsScreen(weekStartDate = weekStartDate)
            }
        }
    }
}