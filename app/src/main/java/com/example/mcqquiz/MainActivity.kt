package com.example.mcqquiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mcqquiz.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // start with QuestionFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, com.example.mcqquiz.ui.quiz.QuestionFragment.newInstance())
                .commit()
        }
    }
}
