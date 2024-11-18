package com.example.daysincolors.Screens.Paciente

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarScreen(navController: NavHostController, registroId: String) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val user = auth.currentUser
    var selectedDate by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf<String?>(null) }
    var motivoCor by remember { mutableStateOf(TextFieldValue("")) }
    var sliderPosition by remember { mutableFloatStateOf(5f) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var registro: RegistroDia? by remember { mutableStateOf(null) }

    val colorOptions = listOf(
        "Triste" to Color(0xFF0008D1),
        "Estressante" to Color(0xFFFF0303),
        "Animado" to Color(0xFFFFD809),
        "Sem Graça" to Color(0xFF737373),
        "Tranquilo" to Color(0xFF7ED957),
        "Solitário" to Color.Black,
        "Engraçado" to Color(0xFFFF914D),
        "Frustrante" to Color(0xFFFF66C4)
    )

 
    LaunchedEffect(registroId) {
        user?.let { loggedInUser ->
            val userId = loggedInUser.uid
            firestore.collection("users")
                .document(userId)
                .collection("diarios")
                .document(registroId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        registro = RegistroDia(
                            id = document.id,
                            data = document.getString("data") ?: "",
                            cor = document.getString("cor") ?: "",
                            motivo = document.getString("motivo") ?: "",
                            // Certificando-se de que avaliacao é tratado como Float
                            avaliacao = (document.getDouble("avaliacao")?.toFloat() ?: 5f)
                        )
                        selectedDate = registro?.data ?: ""
                        selectedColor = registro?.cor
                        motivoCor = TextFieldValue(registro?.motivo ?: "")
                        sliderPosition = registro?.avaliacao ?: 5f
                    }
                }
                .addOnFailureListener {
                    errorMessage = "Erro ao carregar o registro."
                }
        }
    }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        }, year, month, day
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFFF0099),
                    containerColor = Color(0xFFF3E5F5)
                ),
                border = BorderStroke(2.dp, Color(0xFFFF0099)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (selectedDate.isEmpty()) "Selecione a Data" else "Data: $selectedDate",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(text = "Selecione uma cor que representa seu dia:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            colorOptions.forEach { (label, color) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = selectedColor == label,
                        onCheckedChange = { isChecked ->
                            selectedColor = if (isChecked) label else null
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = color,
                            uncheckedColor = color.copy(alpha = 0.6f)
                        )
                    )
                    Text(
                        text = label,
                        color = color,
                        fontWeight = if (selectedColor == label) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = motivoCor,
                onValueChange = { motivoCor = it },
                label = {
                    Text(
                        text = "Motivo da Cor",
                        color = Color(0xFFFF0099),
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFF3E5F5))
                    .border(2.dp, Color(0xFFFF0099), shape = RoundedCornerShape(11.dp))
                    .clip(RoundedCornerShape(11.dp)),
                maxLines = 3,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    containerColor = Color(0xFFF3E5F5),
                    cursorColor = Color(0xFFFF0099)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                text = "Avalie seu dia (0 a 10): ${sliderPosition.roundToInt()}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 0f..10f,
                steps = 9,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFFF0099),
                    activeTrackColor = Color(0xFFFF4EB8),
                    inactiveTrackColor = Color(0xFFFFAAEC)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                onClick = {
                    if (selectedDate.isNotEmpty() && selectedColor != null) {
                        user?.let { loggedInUser ->
                            val userId = loggedInUser.uid
                            firestore.collection("users")
                                .document(userId)
                                .collection("diarios")
                                .document(registroId)
                                .update(
                                    "data", selectedDate,
                                    "cor", selectedColor,
                                    "motivo", motivoCor.text,
                                    "avaliacao", sliderPosition 
                                )
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Alterações salvas", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                                .addOnFailureListener {
                                    errorMessage = "Erro ao salvar as alterações."
                                }
                        }
                    } else {
                        errorMessage = "Por favor, selecione a data e a cor."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF0099),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Salvar Alterações", fontWeight = FontWeight.Bold)
            }
        }
    }

    errorMessage?.let {
        LaunchedEffect(it) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            errorMessage = null
        }
    }
}
