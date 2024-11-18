package com.example.daysincolors.Navigation

import android.app.Activity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.daysincolors.R
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController, showBackButton: Boolean = false) {
    val customColor = Color(0xFFFF0099)
    val context = LocalContext.current
    val window = (context as Activity).window
    val insetsController = WindowInsetsControllerCompat(window, window.decorView)

    insetsController.isAppearanceLightStatusBars = false
    window.statusBarColor = customColor.toArgb()

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val routesWithBackButton = listOf("pacientes", "diarios/{pacienteId}")

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 18.sp,
                color = Color.White
            )
        },
        navigationIcon = {
            if (currentRoute != null && routesWithBackButton.contains(currentRoute)) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = customColor
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val focusManager = LocalFocusManager.current

    if (currentRoute != "login" && currentRoute != "cadastro" && currentRoute != "homePsico" && currentRoute != "detalhes" && currentRoute != "pacientes" && currentRoute != "detalhes2" && currentRoute != "weekDetails/{weekStartDate}" && currentRoute != "diarios/{pacienteId}") {
        val items = listOf(
            NavigationItem.Home,
            NavigationItem.Cadastro,
            NavigationItem.Consultar
        )

        NavigationBar(
            containerColor = Color(0xFFFF0099)
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(id = R.string.app_name)
                        )
                    },
                    label = {
                        val isSelected = currentRoute == item.route
                        val labelColor = if (isSelected) Color.White else Color.White.copy(0.4f)

                        Text(
                            text = item.title,
                            color = labelColor,
                            modifier = Modifier.onFocusChanged {
                                if (it.isFocused) {
                                    focusManager.clearFocus()
                                }
                            }
                        )
                    },
                    selected = currentRoute == item.route,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.White.copy(0.4f),
                        indicatorColor = Color(0xFFFF0099)
                    ),
                    onClick = {
                        navController.navigate(item.route) {
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) { saveState = true }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
