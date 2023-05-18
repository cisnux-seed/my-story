package dev.cisnux.mystory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.cisnux.mystory.R
import dev.cisnux.mystory.databinding.FragmentHomeBinding
import dev.cisnux.mystory.ui.adapters.LoadingStateAdapter
import dev.cisnux.mystory.ui.adapters.RecyclerViewSpacer
import dev.cisnux.mystory.ui.adapters.StoryAdapter
import dev.cisnux.mystory.utils.Failure
import dev.cisnux.mystory.viewmodels.AuthViewModel
import dev.cisnux.mystory.viewmodels.HomeViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

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
        homeViewModel.stories.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }
        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getLiveData<Boolean>(PostStoryFragment.ON_POST)
            ?.observe(viewLifecycleOwner) { isPosted ->
                if (isPosted) {
                    binding.storyRecyclerView.smoothScrollToPosition(0)
                }
            }
    }

    private fun setupHomeView() = with(binding) {
        root.visibility = View.GONE
        authViewModel.isLoggedIn.observe(viewLifecycleOwner) {
            if (!it) {
                findNavController().navigate(R.id.action_global_loginFragment)
            } else {
                root.visibility = View.VISIBLE
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
        lifecycleScope.launch {
            adapter.loadStateFlow.collect {
                val loadState = it.refresh
                progressBar.isVisible =
                    loadState is LoadState.Loading && adapter.itemCount == 0
                if (loadState is LoadState.Error) {
                    val failure = loadState.error
                    if (failure is Failure.ConnectionFailure) {
                        failure.message =
                            getString(R.string.no_internet_connection)
                    }
                    val errorMessage = failure.message
                    errorMessage?.let {
                        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
                emptyStories.isVisible = loadState is LoadState.NotLoading && adapter.itemCount == 0
            }
        }
        val layoutManager = LinearLayoutManager(requireActivity())
        val divider = RecyclerViewSpacer()
        storyRecyclerView.layoutManager = layoutManager
        storyRecyclerView.addItemDecoration(divider)
        storyRecyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        storyRecyclerView.setHasFixedSize(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}