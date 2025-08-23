package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dhobikart.app.OnboardingPage
import com.dhobikart.app.R
import com.dhobikart.app.databinding.FragmentOnboardingPageBinding

class OnboardingPageFragment : Fragment(R.layout.fragment_onboarding_page) {

    private var _binding: FragmentOnboardingPageBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_PAGE_DATA = "page_data"
        fun newInstance(page: OnboardingPage): OnboardingPageFragment {
            return OnboardingPageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PAGE_DATA, page)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnboardingPageBinding.bind(view)

        arguments?.getParcelable<OnboardingPage>(ARG_PAGE_DATA)?.let { page ->
            binding.onboardingImage.setImageResource(page.imageResId)
            binding.onboardingTitle.text = page.title
            binding.onboardingDescription.text = page.description
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
