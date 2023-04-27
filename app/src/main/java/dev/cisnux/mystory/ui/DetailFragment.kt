package dev.cisnux.mystory.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import coil.request.CachePolicy
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.cisnux.mystory.R
import dev.cisnux.mystory.databinding.FragmentDetailBinding
import dev.cisnux.mystory.domain.DetailStory
import dev.cisnux.mystory.utils.ApplicationState
import dev.cisnux.mystory.utils.Failure
import dev.cisnux.mystory.utils.withDateFormat
import dev.cisnux.mystory.viewmodels.DetailViewModel

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var id: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = DetailFragmentArgs.fromBundle(arguments as Bundle).id
        viewModel.getDetailStory(id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.detailStory.observe(viewLifecycleOwner, ::subscribeGetDetailStoryProgress)
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun subscribeGetDetailStoryProgress(applicationState: ApplicationState<DetailStory>?) =
        with(binding) {
            when (applicationState) {
                is ApplicationState.Success -> {
                    val detailStory = applicationState.data
                    detailStory?.let {
                        progressBar.visibility = View.GONE
                        description.text = detailStory.description
                        username.text = detailStory.username
                        storyPicture.load(detailStory.photoUrl) {
                            placeholder(R.drawable.loading_placeholder)
                            crossfade(true)
                            networkCachePolicy(CachePolicy.ENABLED)
                            diskCachePolicy(CachePolicy.ENABLED)
                            memoryCachePolicy(CachePolicy.ENABLED)
                        }
                        createdAt.text = detailStory.createdAt.withDateFormat()
                        storyPicture.contentDescription = getString(
                            R.string.the_story_from,
                            detailStory.username,
                            detailStory.createdAt
                        )
                    }
                }

                is ApplicationState.Failed -> {
                    progressBar.visibility = View.GONE
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