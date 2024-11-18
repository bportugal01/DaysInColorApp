package com.example.daysincolors.Screens


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.example.daysincolors.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastroScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isButtonEnabled by remember { mutableStateOf(false) }

    // Validar se todos os campos foram preenchidos corretamente
    LaunchedEffect(name, email, password, confirmPassword) {
        isButtonEnabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword
    }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Cor personalizada
    val customColor = Color(0xFFFF0099)


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8F8) // Fundo de cor suave
    ) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
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

        Text(text = "Cadastro", style = TextStyle(fontSize = 30.sp, color = Color(0xFFFF0099))) // Cor modificada

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it.split(" ").joinToString(" ") { word -> word.replaceFirstChar { char -> char.uppercase() } }
            },
            label = { Text("Nome completo", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {}),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = customColor,
                unfocusedBorderColor = customColor.copy(alpha = 0.4f),
                cursorColor = customColor
            )
        )



        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {}),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = customColor,
                unfocusedBorderColor = customColor.copy(alpha = 0.4f),
                cursorColor = customColor
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = {}),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = customColor,
                unfocusedBorderColor = customColor.copy(alpha = 0.4f),
                cursorColor = customColor
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar senha", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { /* Se desejar, faça algo ao finalizar */ }),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = customColor,
                unfocusedBorderColor = customColor.copy(alpha = 0.4f),
                cursorColor = customColor
            )

        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (password == confirmPassword) {
                    // Criando o usuário no Firebase Authentication
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Armazenando dados adicionais no Firestore
                                val userId = auth.currentUser?.uid
                                val user = hashMapOf(
                                    "name" to name,
                                    "email" to email,
                                    "status" to "Ativo"  // Status pode ser "ativo" ou "inativo"
                                )

                                db.collection("users").document(userId!!).set(user)
                                    .addOnSuccessListener {
                                        // Sucesso ao salvar os dados no Firestore
                                        Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                        navController.navigate("login")  // Navega para a tela de login após cadastro
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(context, "Erro ao cadastrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isButtonEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0099)) // Cor modificada

        ) {
            Text("Cadastrar", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text(text = "Já tem uma conta? Faça login", color = customColor)
        }
    }
}
}