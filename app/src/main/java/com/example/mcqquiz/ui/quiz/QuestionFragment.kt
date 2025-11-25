package com.example.mcqquiz.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.mcqquiz.R
import com.example.mcqquiz.databinding.FragmentQuestionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionFragment : Fragment() {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!

    private val vm: QuizViewModel by viewModels()

    companion object {
        fun newInstance() = QuestionFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var optionsAdapter: OptionsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        optionsAdapter = OptionsAdapter { idx ->
            vm.onOptionClicked(idx)
        }
        binding.recyclerOptions.adapter = optionsAdapter

        vm.loading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.contentGroup.visibility = if (loading) View.GONE else View.VISIBLE
        }

        vm.currentQuestion.observe(viewLifecycleOwner) { q ->
            if (q == null) return@observe
            binding.textQuestion.text = q.question
            optionsAdapter.submitList(q.options)
            // simple question-change animation
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
            binding.cardQuestion.startAnimation(anim)

            binding.textCounter.text = "${(vm.currentIndex.value ?: 0) + 1} / ${vm.totalQuestions()}"
        }

        vm.revealAnswer.observe(viewLifecycleOwner) { reveal ->
            optionsAdapter.setRevealState(reveal, vm.currentQuestion.value?.answerIndex)
        }

        vm.selectedOptionIndex.observe(viewLifecycleOwner) {
            // handled by adapter
        }

        vm.streakBadgeOn.observe(viewLifecycleOwner) { on ->
            binding.streakBadge.visibility = if (on) View.VISIBLE else View.GONE
        }

        vm.navigateToResult.observe(viewLifecycleOwner, Observer { go ->
            if (go == true) {
                // Navigate to ResultFragment with necessary data
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ResultFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
                vm.onResultNavigated()
            }
        })

        binding.btnSkip.setOnClickListener {
            vm.onSkipClicked()
        }

        // swipe gesture: quick simple implementation: left -> next
        binding.root.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeRight() {
                // nothing
            }

            override fun onSwipeLeft() {
                vm.onSkipClicked()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
