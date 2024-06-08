// MountainDetailFragment.kt
package com.example.mounatinsput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MountainDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mountain_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mountain = arguments?.getParcelable<Mountain>("selectedMountain")

        val mountainImageView = view.findViewById<ImageView>(R.id.mountainImage)
        mountain?.mountainImage?.let {
            val resourceId = resources.getIdentifier(it, "drawable", requireContext().packageName)
            Glide.with(requireContext()).load(resourceId).into(mountainImageView)
        }

        val mountainNameTextView = view.findViewById<TextView>(R.id.mountainNameTextView)
        mountainNameTextView.text = mountain?.name

        val heightTextView = view.findViewById<TextView>(R.id.length)
        heightTextView.text = mountain?.length + " m"

        val descriptionTextView = view.findViewById<TextView>(R.id.description)
        descriptionTextView.text = mountain?.description

        childFragmentManager.commit {
            replace(R.id.stopwatchContainer, StopwatchFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("selectedMountain", mountain)
                }
            })
        }

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            // Placeholder action
            android.widget.Toast.makeText(requireContext(), "Selfie feature coming soon!", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}
