package hoods.com.newsy.features_presentations.home.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import hoods.com.newsy.features_components.discover.domain.use_cases.DiscoverUseCases
import hoods.com.newsy.features_components.headline.domain.use_cases.HeadlineUseCases
import hoods.com.newsy.utils.Utils
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val headlineUseCases: HeadlineUseCases,
    private val discoverUseCases: DiscoverUseCases,
) : ViewModel() {
    var homeState by mutableStateOf(HomeState())
        private set

    init {
        loadArticles()
    }


    private fun loadArticles() {
        homeState = homeState.copy(
            headlineArticles =
            headlineUseCases.fetchHeadlineArticleUseCase(
                category = homeState.selectedHeadlineCategory.category,
                countryCode = Utils.countryCodeList[0].code,
                languageCode = Utils.languageCodeList[0].code
            ).cachedIn(viewModelScope),
            discoverArticles =
            discoverUseCases.fetchDiscoverArticleUseCase(
                category = homeState.selectedDiscoverCategory.category,
                language = "en",
                country = "us"
            ).cachedIn(viewModelScope)
        )
    }


    fun onHomeUIEvents(homeUIEvents: HomeUIEvents) {
        when (homeUIEvents) {
            HomeUIEvents.ViewMoreClicked -> {}
            is HomeUIEvents.ArticleClicked -> {}
            is HomeUIEvents.CategoryChange -> {
                updateCategory(homeUIEvents)
                updateDiscoverArticles()
            }

            is HomeUIEvents.PreferencePanelToggle -> {}
            is HomeUIEvents.OnHeadLineFavouriteChange -> {
                viewModelScope.launch {
                    val isFavourite = homeUIEvents.article.favourite
                    val update = homeUIEvents.article.copy(
                        favourite = !isFavourite
                    )
                    headlineUseCases.updateHeadlineFavouriteUseCase(
                        update
                    )
                }
            }

            is HomeUIEvents.OnDiscoverFavouriteChange -> {
                val isFavourite = homeUIEvents.article.favourite
                homeUIEvents.article.copy(
                    favourite = !isFavourite
                ).also {
                    viewModelScope.launch {
                        discoverUseCases.updateFavouriteDiscoverArticleUseCase(
                            article = it
                        )
                    }
                }
            }
        }
    }

    private fun updateCategory(homeUIEvents: HomeUIEvents.CategoryChange) {
        homeState = homeState.copy(
            selectedDiscoverCategory = homeUIEvents.category
        )

    }

    private fun updateDiscoverArticles() {
        val data = discoverUseCases.fetchDiscoverArticleUseCase(
            category = homeState.selectedDiscoverCategory.category,
            language = "en",
            country = "us"
        ).cachedIn(viewModelScope)
        homeState = homeState.copy(
            discoverArticles = data
        )

    }


}