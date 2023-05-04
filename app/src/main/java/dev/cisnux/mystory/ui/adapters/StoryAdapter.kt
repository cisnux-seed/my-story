package dev.cisnux.mystory.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import dev.cisnux.mystory.R
import dev.cisnux.mystory.databinding.StoryItemBinding
import dev.cisnux.mystory.domain.Story
import dev.cisnux.mystory.ui.HomeFragmentDirections
import dev.cisnux.mystory.utils.withDateFormat

class StoryAdapter : PagingDataAdapter<Story, StoryAdapter.ViewHolder>(DiffCallback) {
    class ViewHolder(internal val binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) = with(binding) {
            username.text = story.username
            createdAt.text = story.createdAt.withDateFormat()
            storyPicture.load(story.photoUrl) {
                placeholder(R.drawable.loading_placeholder)
                crossfade(true)
                networkCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
                memoryCachePolicy(CachePolicy.ENABLED)
            }
            storyPicture.contentDescription = binding.root.context.getString(
                R.string.the_story_from,
                story.username,
                story.createdAt
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        story?.let {
            holder.bind(story)
            holder.itemView.setOnClickListener {
                val extras = FragmentNavigatorExtras(
                    holder.binding.storyPicture to holder.binding.root.context.getString(R.string.transition_story_picture),
                    holder.binding.userProfile to holder.binding.root.context.getString(R.string.transition_user_profile),
                    holder.binding.username to holder.binding.root.context.getString(R.string.transition_username)
                )
                val toDetailFragment =
                    HomeFragmentDirections.actionHomeFragmentToDetailFragment(story.id)
                findNavController(holder.itemView).navigate(toDetailFragment, extras)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean =
            oldItem == newItem
    }
}