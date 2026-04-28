package com.example.howtobeamillionaire.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.howtobeamillionaire.R
import com.example.howtobeamillionaire.data.repository.HighScoreRepository
import com.example.howtobeamillionaire.data.repository.QuestionRepository
import com.example.howtobeamillionaire.ui.components.MenuButton
import com.example.howtobeamillionaire.ui.theme.NeonCyan
import com.example.howtobeamillionaire.ui.theme.gradientBrush
import com.example.howtobeamillionaire.utils.Constants
import com.example.howtobeamillionaire.viewmodel.GameState
import com.example.howtobeamillionaire.viewmodel.GameViewModel

@Composable
fun GameScreen(
    navController: NavController,
    viewModel: GameViewModel
) {
    val gameState by viewModel.gameState.collectAsState()
    val currentQuestion by viewModel.currentQuestion.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val currentPrize by viewModel.currentPrize.collectAsState()
    val timeLeft by viewModel.timeLeft.collectAsState()
    val remainingLifelines by viewModel.remainingLifelines.collectAsState()
    val visibleAnswerIndices by viewModel.visibleAnswerIndices.collectAsState()
    val showAudienciaDialog by viewModel.showAudienciaDialog.collectAsState()
    val husuHint by viewModel.husuHint.collectAsState()

    LaunchedEffect(gameState) {
        if (gameState == GameState.Won) {
            navController.navigate("win/${currentPrize}")
        }
    }

    if (gameState == GameState.GameOver) {
        AlertDialog(
            onDismissRequest = { },
            containerColor = Color(0xFF0A0F1F),
            tonalElevation = 8.dp,
            title = {
                Text(
                    stringResource(R.string.game_over),
                    color = Color.Red,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Ita hatán sala.",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Ita lakon osan hotu.",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.resetGame() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Joga Fali", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Menu", color = Color.White)
                }
            }
        )
    }

    if (gameState == GameState.Decision) {
        AlertDialog(
            onDismissRequest = { },
            containerColor = Color(0xFF0A0F1F),
            tonalElevation = 8.dp,
            title = {
                Text(
                    "Parabéns!",
                    color = Color(0xFFD4AF37),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Ita-nia resposta loos!",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Ita manán ona $$currentPrize",
                        color = NeonCyan,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Ita hakarak kontinua hodi hetan osan barak liután? \n\nAtensaun: Se ita kontinua no hatán sala iha pergunta tuirmai, ita sei lakon osan hotu ($0)!",
                        color = Color.Red.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.continueGame() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Kontinua (Kontinua)", color = Color.Black)
                }
            },
            dismissButton = {
                Button(
                    onClick = { 
                        viewModel.stopGame()
                        navController.navigate("win/${currentPrize}")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Para (Rai Osan)", color = Color.White)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush())
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(2.5f)
                    .fillMaxHeight()
            ) {
                // Top Header Stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Timer with background
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (timeLeft <= 5) Color.Red.copy(alpha = 0.3f) else Color(0xFF1E1E3A))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = if (timeLeft <= 5) Color.Red else NeonCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${timeLeft}s",
                                color = if (timeLeft <= 5) Color.Red else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Prize Box
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFD4AF37).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFFD4AF37), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$$currentPrize",
                            color = Color(0xFFD4AF37),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    AnimatedContent(
                        targetState = currentQuestion,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.9f) togetherWith
                                    fadeOut(animationSpec = tween(500)) + scaleOut(targetScale = 1.1f)
                        },
                        label = "QuestionAnim"
                    ) { question ->
                        Column {
                            // Question Card
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                color = Color(0xFF1E1E3A),
                                border = BorderStroke(2.dp, Color(0xFF3E3E5A)),
                                shadowElevation = 8.dp
                            ) {
                                Text(
                                    text = question?.text ?: "",
                                    modifier = Modifier.padding(24.dp),
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 28.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Answer buttons
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                visibleAnswerIndices.forEach { index ->
                                    val answer = question?.options?.getOrNull(index) ?: ""
                                    AnswerButton(
                                        index = index,
                                        text = answer,
                                        onClick = { viewModel.onAnswerSelected(index) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Lifelines Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                EnhancedLifelineButton(
                                    text = "50:50",
                                    enabled = remainingLifelines["5050"] == true,
                                    onClick = { viewModel.useLifeline5050() },
                                    modifier = Modifier.weight(1f)
                                )
                                EnhancedLifelineButton(
                                    text = "Audiénsia",
                                    enabled = remainingLifelines["audiencia"] == true,
                                    onClick = { viewModel.useAudiencia() },
                                    modifier = Modifier.weight(1f)
                                )
                                EnhancedLifelineButton(
                                    text = "Kolega",
                                    enabled = remainingLifelines["husu"] == true,
                                    onClick = { viewModel.useHusu() },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Back to Menu Button - Styled like lifelines
                            Button(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .height(45.dp)
                                    .width(180.dp)
                                    .shadow(4.dp, RoundedCornerShape(22.dp)),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3E3E5A),
                                    contentColor = Color.White
                                ),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "Fila ba Menu",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            PrizeLadder(currentQuestionIndex)
        }
    }

    // Audiencia dialog
    if (showAudienciaDialog) {
        currentQuestion?.let { question ->
            val poll = generateAudiencePoll(question.correctAnswerIndex)
            AlertDialog(
                onDismissRequest = { viewModel.dismissAudienciaDialog() },
                containerColor = Color(0xFF1E1E3A),
                title = { Text("Audiénsia Vota", color = NeonCyan, fontWeight = FontWeight.Bold) },
                text = {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        poll.forEach { (letter, percent) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("$letter:", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.width(24.dp))
                                Box(modifier = Modifier.weight(1f).height(20.dp).clip(CircleShape).background(Color.DarkGray)) {
                                    Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(percent/100f).background(Color(0xFFD4AF37)))
                                }
                                Text("$percent%", color = Color.White, modifier = Modifier.padding(start = 8.dp), fontSize = 12.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.dismissAudienciaDialog() }) {
                        Text("OK", color = NeonCyan)
                    }
                }
            )
        }
    }

    // Husu hint dialog (Telefone Kolega)
    husuHint?.let { hint ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissHusuHint() },
            containerColor = Color(0xFF1E1E3A),
            title = { Text("Telefone Kolega", color = NeonCyan, fontWeight = FontWeight.Bold) },
            text = { Text(hint, color = Color.White, fontSize = 18.sp) },
            confirmButton = {
                Button(onClick = { viewModel.dismissHusuHint() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37))) {
                    Text("OK", color = Color.Black)
                }
            }
        )
    }
}

@Composable
fun AnswerButton(index: Int, text: String, onClick: () -> Unit) {
    val letter = indexToLetter(index)
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp)
            .border(1.dp, Color(0xFFD4AF37).copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1E1E3A),
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$letter:",
                color = Color(0xFFD4AF37),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun EnhancedLifelineButton(text: String, enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bgColor = if (enabled) Color(0xFFD4AF37) else Color(0xFF333333)
    val contentColor = if (enabled) Color.Black else Color.Gray

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(50.dp)
            .shadow(if (enabled) 4.dp else 0.dp, RoundedCornerShape(25.dp)),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            disabledContainerColor = Color(0xFF222222)
        ),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PrizeLadder(currentIndex: Int) {
    val prizes = Constants.PRIZE_AMOUNTS.drop(1).reversed()
    Surface(
        modifier = Modifier
            .width(100.dp)
            .fillMaxHeight(),
        color = Color(0xFF1E1E3A).copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            itemsIndexed(prizes) { idx, amount ->
                val reverseIdx = prizes.size - 1 - idx
                val isCurrent = reverseIdx == currentIndex
                val isSafe = amount == 1000 || amount == 32000 || amount == 1000000

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isCurrent) Color(0xFFD4AF37) else Color.Transparent)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$$amount",
                        color = when {
                            isCurrent -> Color.Black
                            isSafe -> Color.White
                            else -> Color(0xFFD4AF37).copy(alpha = 0.7f)
                        },
                        fontSize = if (isCurrent) 14.sp else 12.sp,
                        fontWeight = if (isCurrent || isSafe) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

fun indexToLetter(index: Int): String = when (index) {
    0 -> "A"
    1 -> "B"
    2 -> "C"
    3 -> "D"
    else -> ""
}

fun generateAudiencePoll(correctIndex: Int): List<Pair<String, Int>> {
    val letters = listOf("A", "B", "C", "D")
    val percentages = MutableList(4) { 0 }
    val correctPercent = (50..80).random()
    percentages[correctIndex] = correctPercent
    
    var remaining = 100 - correctPercent
    val others = (0..3).filter { it != correctIndex }.shuffled()
    
    for (i in 0..1) {
        val p = (0..remaining).random()
        percentages[others[i]] = p
        remaining -= p
    }
    percentages[others.last()] = remaining
    
    return letters.zip(percentages)
}

class GameViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val questionRepo = QuestionRepository(context)
        val highScoreRepo = HighScoreRepository(context)
        return GameViewModel(questionRepo, highScoreRepo) as T
    }
}
