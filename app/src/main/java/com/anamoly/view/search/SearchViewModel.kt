package com.anamoly.view.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.anamoly.repository.ProjectRepository
import com.anamoly.response.CategoryResponse
import com.anamoly.response.SearchTagResponse


/**
 * Created on 11-02-2020.
 */
class SearchViewModel(application: Application) :
    AndroidViewModel(application) {
    private val projectRepository: ProjectRepository = ProjectRepository()
    var searchTagResponseLiveData: LiveData<SearchTagResponse?>? = null

    fun getCategoryList(): LiveData<CategoryResponse?> {
        return projectRepository.getCategoryList()
    }

    fun getSearchTag(params: Map<String, String>) {
        searchTagResponseLiveData = projectRepository.getSearchTagList(params)
    }

}