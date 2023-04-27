package dev.cisnux.mystory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.cisnux.mystory.R
import dev.cisnux.mystory.databinding.FragmentHomeBinding
import dev.cisnux.mystory.domain.Story
import dev.cisnux.mystory.ui.adapters.RecyclerViewSpacer
import dev.cisnux.mystory.ui.adapters.StoryAdapter
import dev.cisnux.mystory.utils.ApplicationState
import dev.cisnux.mystory.utils.Failure
import dev.cisnux.mystory.viewmodels.AuthViewModel
import dev.cisnux.mystory.viewmodels.HomeViewModel
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var adapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHomeView()
        homeViewModel.storyStates.observe(viewLifecycleOwner, ::subscribeGetStoriesProgress)
    }

    private fun setupHomeView() = with(binding) {
        root.visibility = View.GONE
        authViewModel.isAlreadyLogin.observe(viewLifecycleOwner) {
            if (!it) {
                val toLoginFragment = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                findNavController().navigate(toLoginFragment)
            } else {
                root.visibility = View.VISIBLE
                homeViewModel.refreshStories()
            }
        }
        fabPosting.setOnClickListener {
            val toPostStoryFragment = HomeFragmentDirections.actionHomeFragmentToPostStoryFragment()
            findNavController().navigate(toPostStoryFragment)
        }
        materialToolbar.inflateMenu(R.menu.home_action_menu)
        materialToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_logout -> {
                    authViewModel.logout()
                    true
                }

                else -> false
            }
        }
        val layoutManager = LinearLayoutManager(requireActivity())
        val divider = RecyclerViewSpacer()
        storyRecyclerView.layoutManager = layoutManager
        storyRecyclerView.addItemDecoration(divider)
        storyRecyclerView.adapter = adapter
        storyRecyclerView.setHasFixedSize(true)
    }

    private fun subscribeGetStoriesProgress(applicationState: ApplicationState<List<Story>>?) =
        with(binding) {
            when (applicationState) {
                is ApplicationState.Success -> {
                    val stories = applicationState.data
                    progressBar.visibility = View.GONE
                    if (stories?.isNotEmpty() == true) {
                        storyRecyclerView.smoothScrollToPosition(0)
                        adapter.submitList(stories)
                        emptyStories.visibility = View.GONE
                    } else {
                        emptyStories.visibility = View.VISIBLE
                    }
                }

                is ApplicationState.Failed -> {
                    progressBar.visibility = View.VISIBLE
                    if (applicationState.failure is Failure.ConnectionFailure) {
                        applicationState.failure.message =
                            getString(R.string.no_internet_connection)
                    }
                    val errorMessage = applicationState.failure.message
                    errorMessage?.let {
                        Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }

                else -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}