package com.example.howtobeamillionaire.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.howtobeamillionaire.R
import com.example.howtobeamillionaire.ui.components.MenuButton
import com.example.howtobeamillionaire.ui.theme.NeonCyan
import com.example.howtobeamillionaire.ui.theme.gradientBrush
import kotlinx.coroutines.delay
import kotlin.random.Random
import kotlin.math.*
import com.airbnb.lottie.compose.*

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    var life: Float,
    val size: Float
)

@Composable
fun Fireworks() {
    val particles = remember { mutableStateListOf<Particle>() }

    LaunchedEffect(Unit) {
        while (true) {
            val centerX = Random.nextFloat() * 0.8f + 0.1f
            val centerY = Random.nextFloat() * 0.4f + 0.1f
            val color = listOf(
                NeonCyan, Color.Yellow, Color.Magenta, Color.Green, Color.White
            ).random()

            repeat(30) {
                val angle = Random.nextFloat() * 2 * PI
                val speed = Random.nextFloat() * 0.012f + 0.003f
                particles.add(
                    Particle(
                        x = centerX,
                        y = centerY,
                        vx = (cos(angle) * speed).toFloat(),
                        vy = (sin(angle) * speed).toFloat(),
                        color = color,
                        life = 1.0f,
                        size = Random.nextFloat() * 3f + 1f
                    )
                )
            }
            delay(Random.nextLong(400, 900))
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos {
                val iterator = particles.iterator()
                while (iterator.hasNext()) {
                    val p = iterator.next()
                    p.x += p.vx
                    p.y += p.vy
                    p.vy += 0.0002f // gravity
                    p.life -= 0.01f
                    if (p.life <= 0) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            drawCircle(
                color = p.color.copy(alpha = p.life),
                radius = p.size.dp.toPx(),
                center = Offset(p.x * size.width, p.y * size.height)
            )
        }
    }
}

@Composable
fun WinScreen(navController: NavController, prize: Long) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url("https://assets5.lottiefiles.com/packages/lf20_tou9dfsq.json")
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush())
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.FillBounds
        )
        Fireworks()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.image),
                contentDescription = "Win Logo",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.win_congratulations),
                fontSize = 32.sp,
                color = NeonCyan,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.win_ita_manan),
                fontSize = 28.sp,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.win_total_prize),
                fontSize = 20.sp,
                color = Color.Gray
            )
            Text(
                text = "${stringResource(R.string.win_dollars_prefix)}$prize",
                fontSize = 48.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = NeonCyan
            )
            Spacer(modifier = Modifier.height(64.dp))
            MenuButton(
                text = stringResource(R.string.win_new_game),
                onClick = { navController.popBackStack("menu", inclusive = false) }
            )
        }
    }
}
