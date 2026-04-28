package com.example.howtobeamillionaire.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.howtobeamillionaire.R
import com.example.howtobeamillionaire.ui.components.MenuButton
import com.example.howtobeamillionaire.ui.theme.gradientBrush
import com.example.howtobeamillionaire.ui.theme.NeonCyan

import com.example.howtobeamillionaire.viewmodel.LoginViewModel

@Composable
fun MenuScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.image),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "OINSÁ ATU SAI MILIONÁRIU",
                color = NeonCyan,
                fontSize = 32.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 40.sp,
                letterSpacing = 2.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(48.dp))

            MenuButton(
                text = stringResource(R.string.menu_hahu_game),
                onClick = { navController.navigate("game") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            MenuButton(
                text = stringResource(R.string.menu_kona_ba_game),
                onClick = { navController.navigate("about") }
            )
            Spacer(modifier = Modifier.height(32.dp))
            MenuButton(
                text = stringResource(R.string.menu_high_scores),
                onClick = { navController.navigate("highscores") }
            )
            Spacer(modifier = Modifier.height(32.dp))
            MenuButton(
                text = "Sai",
                onClick = {
                    loginViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("menu") { inclusive = true }
                    }
                }
            )
        }
    }
}