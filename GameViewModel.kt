package com.example.howtobeamillionaire.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.howtobeamillionaire.data.model.Difficulty
import com.example.howtobeamillionaire.data.model.Question
import com.example.howtobeamillionaire.data.repository.HighScoreRepository
import com.example.howtobeamillionaire.data.repository.QuestionRepository
import com.example.howtobeamillionaire.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class GameState {
    object Active : GameState()
    object Decision : GameState()
    object GameOver : GameState()
    object Won : GameState()
}

class GameViewModel(
    private val repository: QuestionRepository,
    private val highScoreRepository: HighScoreRepository
) : ViewModel() {

    private val _gameState = MutableStateFlow<GameState>(GameState.Active)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _currentPrize = MutableStateFlow(0)
    val currentPrize: StateFlow<Int> = _currentPrize.asStateFlow()

    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion: StateFlow<Question?> = _currentQuestion.asStateFlow()

    private val _remainingLifelines = MutableStateFlow(
        mapOf("5050" to true, "audiencia" to true, "husu" to true)
    )
    val remainingLifelines: StateFlow<Map<String, Boolean>> = _remainingLifelines.asStateFlow()

    private val _timeLeft = MutableStateFlow(30)
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private val _visibleAnswerIndices = MutableStateFlow(listOf(0, 1, 2, 3))
    val visibleAnswerIndices: StateFlow<List<Int>> = _visibleAnswerIndices.asStateFlow()

    private val _showAudienciaDialog = MutableStateFlow(false)
    val showAudienciaDialog: StateFlow<Boolean> = _showAudienciaDialog.asStateFlow()

    private val _husuHint = MutableStateFlow<String?>(null)
    val husuHint: StateFlow<String?> = _husuHint.asStateFlow()

    private var timerJob: kotlinx.coroutines.Job? = null

    private val _currentUserId = MutableStateFlow<Int?>(null)
    val currentUserId: StateFlow<Int?> = _currentUserId.asStateFlow()

    private val _currentUserName = MutableStateFlow<String>("Player")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    init {
        startNewGame()
    }

    fun setUser(userId: Int, userName: String) {
        _currentUserId.value = userId
        _currentUserName.value = userName
    }

    private fun startNewGame() {
        _gameState.value = GameState.Active
        _currentQuestionIndex.value = 0
        _currentPrize.value = 0
        _remainingLifelines.value = mapOf("5050" to true, "audiencia" to true, "husu" to true)
        resetVisibleAnswers()
        _husuHint.value = null
        _showAudienciaDialog.value = false
        loadNextQuestion()
    }

    private fun loadNextQuestion() {
        val index = _currentQuestionIndex.value
        if (index >= Constants.TOTAL_QUESTIONS) {
            _gameState.value = GameState.Won
            saveScore()
            return
        }

        val difficulty = when {
            index < Constants.DIFFICULTY_EASY_MAX -> Difficulty.EASY
            index < Constants.DIFFICULTY_NORMAL_MAX -> Difficulty.NORMAL
            else -> Difficulty.HARD
        }

        val question = repository.getRandomQuestion(difficulty)
        if (question == null) {
            _gameState.value = GameState.GameOver
            saveScore()
            return
        }
        _currentQuestion.value = question
        resetVisibleAnswers()
        _husuHint.value = null
        resetTimer()
    }

    private fun resetTimer() {
        timerJob?.cancel()
        _timeLeft.value = 30
        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0 && _gameState.value == GameState.Active) {
                delay(1000)
                _timeLeft.value -= 1
            }
            if (_timeLeft.value == 0 && _gameState.value == GameState.Active) {
                gameOver()
            }
        }
    }

    fun onAnswerSelected(selectedIndex: Int) {
        if (_gameState.value != GameState.Active) return
        val question = _currentQuestion.value ?: return
        if (selectedIndex == question.correctAnswerIndex) {
            val nextPrizeIndex = _currentQuestionIndex.value + 1
            _currentPrize.value = Constants.PRIZE_AMOUNTS.getOrNull(nextPrizeIndex) ?: 0
            
            timerJob?.cancel()

            if (nextPrizeIndex >= Constants.TOTAL_QUESTIONS) {
                _currentQuestionIndex.value = nextPrizeIndex
                _gameState.value = GameState.Won
                saveScore()
            } else {
                _gameState.value = GameState.Decision
            }
        } else {
            // Player lost - risk not paid off
            _currentPrize.value = 0
            gameOver()
        }
    }

    fun continueGame() {
        _currentQuestionIndex.value += 1
        _gameState.value = GameState.Active
        loadNextQuestion()
    }

    fun stopGame() {
        timerJob?.cancel()
        _gameState.value = GameState.Won
        saveScore()
    }

    private fun gameOver() {
        timerJob?.cancel()
        _gameState.value = GameState.GameOver
        saveScore()
    }

    fun useLifeline5050() {
        if (_gameState.value != GameState.Active) return
        if (_remainingLifelines.value["5050"] != true) return
        val question = _currentQuestion.value ?: return
        val correctIndex = question.correctAnswerIndex
        val incorrectIndices = (0..3).filter { it != correctIndex }.shuffled()
        val toRemove = incorrectIndices.take(2)
        val newVisibleIndices = (0..3).filter { it !in toRemove }.sorted()
        _visibleAnswerIndices.value = newVisibleIndices
        _remainingLifelines.value = _remainingLifelines.value.toMutableMap().apply { this["5050"] = false }
    }

    fun useAudiencia() {
        if (_gameState.value != GameState.Active) return
        if (_remainingLifelines.value["audiencia"] != true) return
        _remainingLifelines.value = _remainingLifelines.value.toMutableMap().apply { this["audiencia"] = false }
        _showAudienciaDialog.value = true
    }

    fun dismissAudienciaDialog() {
        _showAudienciaDialog.value = false
    }

    fun useHusu() {
        if (_gameState.value != GameState.Active) return
        if (_remainingLifelines.value["husu"] != true) return
        _remainingLifelines.value = _remainingLifelines.value.toMutableMap().apply { this["husu"] = false }
        val question = _currentQuestion.value ?: return
        val correctLetter = when (question.correctAnswerIndex) {
            0 -> "A"
            1 -> "B"
            2 -> "C"
            else -> "D"
        }
        _husuHint.value = "Amigu fiar hateten: Resposta $correctLetter"
    }

    fun dismissHusuHint() {
        _husuHint.value = null
    }

    fun resetGame() {
        startNewGame()
    }

    private fun resetVisibleAnswers() {
        _visibleAnswerIndices.value = listOf(0, 1, 2, 3)
    }

    private fun saveScore() {
        val prize = _currentPrize.value
        val userId = _currentUserId.value
        val playerName = _currentUserName.value
        if (prize > 0 && userId != null) {
            viewModelScope.launch {
                highScoreRepository.insertHighScore(userId, playerName, prize.toLong())
            }
        }
    }
}

class GameViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val questionRepo = QuestionRepository(context)
        val highScoreRepo = HighScoreRepository(context)
        return GameViewModel(questionRepo, highScoreRepo) as T
    }
}