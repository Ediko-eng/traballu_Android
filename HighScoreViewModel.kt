package com.example.howtobeamillionaire.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.howtobeamillionaire.data.entity.HighScoreEntity
import com.example.howtobeamillionaire.data.repository.HighScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HighScoreViewModel(private val repository: HighScoreRepository) : ViewModel() {
    private val _topScores = MutableStateFlow<List<HighScoreEntity>>(emptyList())
    val topScores: StateFlow<List<HighScoreEntity>> = _topScores

    fun loadScores(userId: Int?) {
        viewModelScope.launch {
            if (userId != null) {
                repository.getTopScoresForUser(userId).collectLatest { scores ->
                    _topScores.value = scores
                }
            } else {
                _topScores.value = emptyList()
            }
        }
    }

    fun deleteScore(highScore: HighScoreEntity) {
        viewModelScope.launch {
            repository.deleteHighScore(highScore)
        }
    }
}

class HighScoreViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = HighScoreRepository(context)
        return HighScoreViewModel(repository) as T
    }
}