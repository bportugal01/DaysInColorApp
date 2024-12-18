package com.example.daysincolors.Screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.daysincolors.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import androidx.work.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var nomeUsuario by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var loginBemSucedido by remember { mutableStateOf(false) }

    val contexto = LocalContext.current

    val autenticacao = FirebaseAuth.getInstance()


    val corPersonalizada = Color(0xFFFF0099)

    // Credenciais psicólogo (fixas)
    val usuarioCorretoPsico = "psicologo"
    val senhaCorretaPsico = "123"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8F8) 
    ) {
        // Lógica do formulário
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Image(
                painter = painterResource(id = R.drawable.daysincolors),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) 
                    .padding(bottom = 32.dp)
            )
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Entrar",
                style = TextStyle(fontSize = 30.sp, color = Color(0xFFFF0099))
            ) 

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = nomeUsuario,
                onValueChange = { nomeUsuario = it },
                label = { Text("E-mail", color = Color.Gray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { }
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = corPersonalizada,
                    unfocusedBorderColor = corPersonalizada.copy(alpha = 0.4f),
                    cursorColor = corPersonalizada
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        handleLogin(navController, nomeUsuario, senha, contexto, autenticacao)
                    }
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = corPersonalizada,
                    unfocusedBorderColor = corPersonalizada.copy(alpha = 0.4f),
                    cursorColor = corPersonalizada
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    handleLogin(navController, nomeUsuario, senha, contexto, autenticacao)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0099))
            ) {
                Text("Entrar", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { navController.navigate("cadastro") }) {
                Text(
                    text = "Não tem uma conta? Cadastre-se",
                    color = Color(0xFFFF0099)
                ) // Cor modificada
            }
        }
    }
}





fun agendarNotificacaoAleatoria(contexto: Context) {
    val autenticacao = FirebaseAuth.getInstance()

    if (autenticacao.currentUser != null) {
        val requisicaoNotificacaoImediata = OneTimeWorkRequestBuilder<TrabalhadorNotificacao>()
            .setInitialDelay(1, TimeUnit.SECONDS) 
            .build()
        WorkManager.getInstance(contexto).enqueue(requisicaoNotificacaoImediata)

        val requisicaoNotificacaoPeriodica = PeriodicWorkRequestBuilder<TrabalhadorNotificacao>(1, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.MINUTES)  
            .build()

        WorkManager.getInstance(contexto).enqueueUniquePeriodicWork(
            "TrabalhadorNotificacao",
            ExistingPeriodicWorkPolicy.REPLACE,  
            requisicaoNotificacaoPeriodica
        )
    }
}

class TrabalhadorNotificacao(contexto: Context, parametrosTrabalho: WorkerParameters) : Worker(contexto, parametrosTrabalho) {

    override fun doWork(): Result {
        // Enviar a notificação
        enviarNotificacao()
        return Result.success()
    }

    private fun enviarNotificacao() {
        val idCanal = "canal_days_in_color"
        val gerenciadorNotificacao = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                idCanal,
                "Notificações do Days In Color",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            gerenciadorNotificacao.createNotificationChannel(canal)
        }

        val notificacao = NotificationCompat.Builder(applicationContext, idCanal)
            .setSmallIcon(R.drawable.daysincolors) 
            .setContentTitle("Days In Color")
            .setContentText("Como foi o seu dia? Registre agora no Days In Color!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        gerenciadorNotificacao.notify(Random.nextInt(), notificacao)
    }
}

fun handleLogin(
    navController: NavController,
    nomeUsuario: String,
    senha: String,
    contexto: Context,
    autenticacao: FirebaseAuth
) {
    val usuarioCorretoPsico = "psicologo"
    val senhaCorretaPsico = "123"

    if (nomeUsuario == usuarioCorretoPsico && senha == senhaCorretaPsico) {
        navController.navigate("homePsico")
        Toast.makeText(contexto, "Login como psicólogo bem-sucedido!", Toast.LENGTH_SHORT).show()

        agendarNotificacaoAleatoria(contexto)
        return
    }

    autenticacao.signInWithEmailAndPassword(nomeUsuario, senha)
        .addOnCompleteListener { tarefa ->
            if (tarefa.isSuccessful) {
                val usuario: FirebaseUser? = autenticacao.currentUser
                if (usuario != null) {

                    navController.navigate("homePaciente")
                    Toast.makeText(contexto, "Login como paciente bem-sucedido!", Toast.LENGTH_SHORT).show()

                    agendarNotificacaoAleatoria(contexto)
                }
            } else {

                Toast.makeText(contexto, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
            }
        }
}
