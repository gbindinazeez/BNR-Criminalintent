package com.bignerdranch.android.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import java.io.File


private const val ARG_PHOTO = "photo"
class PictureDisplayFragment: DialogFragment() {
    private lateinit var crimePicture: ImageView
    private lateinit var photoFile: File


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_photo_viewed,container,false)
        photoFile = arguments?.getSerializable(ARG_PHOTO) as File
        crimePicture = view.findViewById(R.id.photo_view)
        if (photoFile.exists()){
            val bitmap = getScaledBitmap(photoFile.path,requireActivity())
            crimePicture.setImageBitmap(bitmap)
        }
        return view
    }

    companion object{
        fun newInstance(file: File):PictureDisplayFragment{
            val args = Bundle().apply {
                putSerializable(ARG_PHOTO,file)
            }
            return PictureDisplayFragment().apply { arguments = args }
        }
    }
}