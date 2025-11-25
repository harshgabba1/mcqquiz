package com.example.mcqquiz.ui.quiz

import androidx.lifecycle.*
import com.example.mcqquiz.data.model.Question
import com.example.mcqquiz.data.repo.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repo: QuestionRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> = _loading

    private val _questions = MutableLiveData<List<Question>>(emptyList())
    private val questions: List<Question> get() = _questions.value ?: emptyList()

    private val _currentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> = _currentIndex

    private val _currentQuestion = MediatorLiveData<Question?>().apply {
        addSource(_questions) { value = pickQuestion(_currentIndex.value ?: 0) }
        addSource(_currentIndex) { value = pickQuestion(it) }
    }
    val currentQuestion: LiveData<Question?> = _currentQuestion

    private val _selectedOptionIndex = MutableLiveData<Int?>(null)
    val selectedOptionIndex: LiveData<Int?> = _selectedOptionIndex

    private val _revealAnswer = MutableLiveData<Boolean>(false)
    val revealAnswer: LiveData<Boolean> = _revealAnswer

    private val _correctCount = MutableLiveData(0)
    val correctCount: LiveData<Int> = _correctCount

    private val _skippedCount = MutableLiveData(0)
    val skippedCount: LiveData<Int> = _skippedCount

    private val _longestStreak = MutableLiveData(0)
    val longestStreak: LiveData<Int> = _longestStreak

    private val _currentStreak = MutableLiveData(0)
    val currentStreak: LiveData<Int> = _currentStreak

    private val _streakBadgeOn = MutableLiveData(false)
    val streakBadgeOn: LiveData<Boolean> = _streakBadgeOn

    // navigation event to results (single event)
    private val _navigateToResult = MutableLiveData<Boolean>(false)
    val navigateToResult: LiveData<Boolean> = _navigateToResult

    private var advanceJob: Job? = null

    init {
        fetch()
    }

    private fun pickQuestion(index: Int): Question? {
        return questions.getOrNull(index)
    }

    fun fetch() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val data = repo.fetchQuestions()
                _questions.value = data
            } catch (e: Exception) {
                // handle error - for now empty list
                _questions.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun onOptionClicked(optionIndex: Int) {
        // if already revealed for this question, ignore
        if (_revealAnswer.value == true) return

        _selectedOptionIndex.value = optionIndex
        _revealAnswer.value = true

        val q = _currentQuestion.value ?: return
        if (optionIndex == q.answerIndex) {
            _correctCount.value = (_correctCount.value ?: 0) + 1
            _currentStreak.value = (_currentStreak.value ?: 0) + 1
            // update longest streak
            val cur = _currentStreak.value ?: 0
            if (cur > (_longestStreak.value ?: 0)) {
                _longestStreak.value = cur
            }
            // streak badge logic
            _streakBadgeOn.value = (_currentStreak.value ?: 0) >= 3
        } else {
            // wrong -> reset streak
            _currentStreak.value = 0
            _streakBadgeOn.value = false
        }

        // schedule advance after 2 seconds
        advanceJob?.cancel()
        advanceJob = viewModelScope.launch {
            delay(2000)
            goToNext()
        }
    }

    fun onSkipClicked() {
        // increment skipped and move immediately
        _skippedCount.value = (_skippedCount.value ?: 0) + 1
        // reset streak on skip? (not specified) — we'll NOT reset streak on skip
        advanceJob?.cancel()
        goToNext()
    }

    private fun goToNext() {
        _revealAnswer.value = false
        _selectedOptionIndex.value = null

        val next = (_currentIndex.value ?: 0) + 1
        if (next >= questions.size || questions.isEmpty()) {
            // finished quiz
            _navigateToResult.value = true
        } else {
            _currentIndex.value = next
        }
    }

    fun onRestart() {
        _currentIndex.value = 0
        _selectedOptionIndex.value = null
        _revealAnswer.value = false
        _correctCount.value = 0
        _skippedCount.value = 0
        _longestStreak.value = 0
        _currentStreak.value = 0
        _streakBadgeOn.value = false
        _navigateToResult.value = false
    }

    // clear navigation single event after handled
    fun onResultNavigated() {
        _navigateToResult.value = false
    }

    // helpful getters for results
    fun totalQuestions(): Int = questions.size
}
