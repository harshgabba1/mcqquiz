package com.example.mcqquiz.data.repo


import com.example.mcqquiz.data.model.Question
import com.example.mcqquiz.data.remote.ApiService
import javax.inject.Inject

class QuestionRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun fetchQuestions(): List<Question> {
        return api.getQuestionsRaw()
    }
}
