package com.cupsofcode.photosearch.features.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.cupsofcode.photosearch.activity.MainActivity
import com.cupsofcode.photosearch.R
import com.cupsofcode.photosearch.databinding.FragmentDetailsBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject

class PhotoDetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val compositeDisposable = CompositeDisposable()
    private val intentsSubject = PublishSubject.create<PhotoDetailsIntent>()

    private val viewModel by lazy {
        (activity as MainActivity).getActivityComponent().photoDetailsViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.shared_image)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.bind(intentsSubject)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ uiState ->

                binding.detailsLoader.loaderView.isVisible = uiState.isLoading

                Glide.with(requireActivity().baseContext)
                    .load(uiState.photo?.imageUri)
                    .placeholder(R.drawable.background_empty)
                    .error(R.drawable.background_error)
                    .into(binding.img)

                uiState.error?.let {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }

                binding.title.text = uiState.photo?.title
                binding.bookmark.isSelected = uiState.isSaved

            }, {
                println("state onError: " + it)
                //Obs should not error out, because errors being passed as intent to viewstate. If compiler comes here, it means somewhere in vm stream is not being handled properly: check if you're converting to Observable when you get the error from single or completable
            }).addTo(compositeDisposable)

        arguments?.let {
            val photoId = it.getString(ARG_PARAM1)
            photoId?.let { id ->
                intentsSubject.onNext(PhotoDetailsIntent.PhotoIdReceived(id))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
    }

    companion object {
        const val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(photoId: String) =
            PhotoDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, photoId)
                }
            }
    }
}