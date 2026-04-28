package com.example.howtobeamillionaire.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.howtobeamillionaire.R
import com.example.howtobeamillionaire.ui.theme.NeonCyan
import com.example.howtobeamillionaire.ui.theme.gradientBrush
import com.example.howtobeamillionaire.viewmodel.LoginViewModel

@Composable
fun RegisterScreen(navController: NavController, loginViewModel: LoginViewModel) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val registerState by loginViewModel.registerState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(registerState) {
        registerState?.let {
            if (it.isSuccess) {
                Toast.makeText(context, context.getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                val errorMessage = it.exceptionOrNull()?.message ?: context.getString(R.string.register_failed)
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
            loginViewModel.clearErrors()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(gradientBrush())) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.register_title), color = NeonCyan, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.register_name_label), color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = NeonCyan
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text(stringResource(R.string.register_age_label), color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = NeonCyan
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(stringResource(R.string.register_gender_label), color = Color.Gray, fontSize = 16.sp, modifier = Modifier.align(Alignment.Start))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { gender = "Mane" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (gender == "Mane") NeonCyan else Color.DarkGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Mane", color = if (gender == "Mane") Color.Black else Color.White)
                }
                Button(
                    onClick = { gender = "Feto" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (gender == "Feto") NeonCyan else Color.DarkGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Feto", color = if (gender == "Feto") Color.Black else Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.register_email_label), color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = NeonCyan
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.register_password_label), color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = NeonCyan
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    val ageInt = age.toIntOrNull() ?: 0
                    loginViewModel.register(name, ageInt, gender, email, password) 
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(stringResource(R.string.register_button), color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.register_have_account), color = Color.White)
            }
        }
    }
}
