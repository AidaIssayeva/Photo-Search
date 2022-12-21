package com.cupsofcode.photosearch.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cupsofcode.photosearch.activity.MainActivity
import com.cupsofcode.photosearch.databinding.FragmentHomeBinding
import com.cupsofcode.photosearch.repository.model.Photo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject


class HomeFragment : Fragment(), PhotoAdapter.Listener, SearchListAdapter.Listener,
    androidx.appcompat.widget.SearchView.OnQueryTextListener,
    androidx.appcompat.widget.SearchView.OnCloseListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val compositeDisposable = CompositeDisposable()
    private val intentsSubject = PublishSubject.create<HomeIntent>()

    private val viewModel by lazy {
        (activity as MainActivity).getActivityComponent().homeViewModel()
    }

    private val photoAdapter by lazy {
        PhotoAdapter(this)
    }

    private val searchListAdapter by lazy {
        SearchListAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.photoRecyclerview.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = photoAdapter
        }
        binding.searchRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchListAdapter
        }

        binding.searchView.setOnQueryTextListener(this)
        binding.searchView.setIconifiedByDefault(false)
        binding.searchView.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                intentsSubject.onNext(HomeIntent.SearchFocused)
            }
        }

        viewModel.bind(intentsSubject.hide())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ uiState ->
                binding.loader.loaderView.isVisible = uiState.showLoading

                photoAdapter.addPhotos(uiState.photos)
                searchListAdapter.addAll(uiState.savedSearches)

                binding.emptyView.isVisible = uiState.showEmptyView
                binding.searchRecyclerview.isVisible = uiState.showPreviousSearches
                binding.photoRecyclerview.isVisible = !uiState.showPreviousSearches

                uiState.error?.let {
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }

            }, {
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                //Obs should not error out, because errors being passed as intent to viewstate. If compiler comes here, it means somewhere in vm stream is not being handled properly: check if you're converting to Observable when you get the error from single or completable
            }).addTo(compositeDisposable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
    }

    override fun onPhotoClicked(photo: Photo) {
        intentsSubject.onNext(HomeIntent.PhotoDetailsClicked(photo))
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        intentsSubject.onNext(HomeIntent.Search(query))
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if (newText.isEmpty()) {
            intentsSubject.onNext(HomeIntent.SearchCleared)
        }
        return true
    }

    override fun onClose(): Boolean {
        // this is not being called due to bug in Android, when setIconified is set to false
        intentsSubject.onNext(HomeIntent.SearchCleared)
        return false
    }

    override fun closeClicked(query: String) {
        intentsSubject.onNext(HomeIntent.RemoveSearchQueryClicked(query))
    }

    override fun searchItemClicked(query: String) {
        binding.searchView.setQuery(query, true)
        intentsSubject.onNext(HomeIntent.Search(query))
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}