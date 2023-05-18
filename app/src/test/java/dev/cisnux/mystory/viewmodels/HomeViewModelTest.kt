package dev.cisnux.mystory.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import dev.cisnux.mystory.DataDummy
import dev.cisnux.mystory.MainDispatcherRule
import dev.cisnux.mystory.domain.Story
import dev.cisnux.mystory.domain.StoryRepository
import dev.cisnux.mystory.getOrAwaitValue
import dev.cisnux.mystory.ui.adapters.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
internal class HomeViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Stories Should Not Null and Return data`() = runTest {
        val dummyStories = DataDummy.generateDummyStories()
        val data: PagingData<Story> = PagingData.from(dummyStories)
        val expectedStories = flow { emit(data) }

        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStories)
        val homeViewModel = HomeViewModel(storyRepository)
        val actualStories = homeViewModel.stories.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories.first(), differ.snapshot().first())
    }

    @Test
    fun `when Get Stories Empty Should return No Data`() = runTest {
        val data: PagingData<Story> = PagingData.from(emptyList())
        val expectedStories = flow { emit(data) }

        Mockito.`when`(storyRepository.getStories()).thenReturn(expectedStories)
        val homeViewModel = HomeViewModel(storyRepository)
        val actualStories = homeViewModel.stories.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertEquals(0, differ.snapshot().size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}

        override fun onRemoved(position: Int, count: Int) {}

        override fun onMoved(fromPosition: Int, toPosition: Int) {}

        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}