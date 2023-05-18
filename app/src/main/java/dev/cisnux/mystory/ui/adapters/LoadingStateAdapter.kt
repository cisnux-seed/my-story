package dev.cisnux.mystory.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.cisnux.mystory.R
import dev.cisnux.mystory.databinding.ItemLoadingBinding
import dev.cisnux.mystory.utils.Failure

class LoadingStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(binding, retry)
    }

    class LoadingStateViewHolder(
        private val binding: ItemLoadingBinding,
        private val retry: () -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) = with(binding) {
            retryBtn.setOnClickListener { retry() }
            if (loadState is LoadState.Error) {
                val failure = loadState.error
                if (failure is Failure.ConnectionFailure) {
                    failure.message =
                        binding.root.context.getString(R.string.no_internet_connection)
                }
                Toast.makeText(root.context, failure.message, Toast.LENGTH_SHORT)
                    .show()
            }
            progressBar.isVisible = loadState is LoadState.Loading
            binding.retryBtn.isVisible = loadState is LoadState.Error
        }
    }
}