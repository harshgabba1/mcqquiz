package com.example.mcqquiz.data.remote


import com.example.mcqquiz.data.model.Question
import retrofit2.http.GET

interface ApiService {
    // The gist raw url returns the array directly
    @GET("dr-samrat/53846277a8fcb034e482906ccc0d12b2/raw")
    suspend fun getQuestionsRaw(): List<Question>
}
