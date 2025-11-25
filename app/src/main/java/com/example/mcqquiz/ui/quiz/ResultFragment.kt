package com.example.mcqquiz.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.mcqquiz.R
import com.example.mcqquiz.databinding.FragmentResultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val vm: QuizViewModel by activityViewModels() // share VM across fragments

    companion object {
        fun newInstance() = ResultFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnRestart.setOnClickListener {
            vm.onRestart()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, QuestionFragment.newInstance())
                .commit()
        }

        // observe for values
        binding.apply {
            vm.correctCount.observe(viewLifecycleOwner) { correct ->
                textCorrect.text = "Correct: $correct"
            }
            vm.totalQuestions().let { total ->
                textTotal.text = "Total: $total"
            }
            vm.skippedCount.observe(viewLifecycleOwner) { skipped ->
                textSkipped.text = "Skipped: $skipped"
            }
            vm.longestStreak.observe(viewLifecycleOwner) { ls ->
                textStreak.text = "Longest streak: $ls"
            }
        }

        // simple celebratory animation / micro-interaction
        binding.root.post {
            binding.resultCard.animate().rotationBy(360f).setDuration(600).start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
