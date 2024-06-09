// MountainDetailFragment.kt
package com.example.mounatinsput

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MountainDetailFragment : Fragment() {

    private lateinit var mountainImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mountain_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mountain = arguments?.getParcelable<Mountain>("selectedMountain")

        mountainImageView = view.findViewById(R.id.mountainImage)
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

        mountainImageView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Анимация увеличения изображения при нажатии
                    animateImage(true)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Анимация возврата изображения к обычному размеру
                    animateImage(false)
                    true
                }
                else -> false
            }
        }

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            // Placeholder action for FAB button
            android.widget.Toast.makeText(requireContext(), "Selfie feature coming soon!", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun animateImage(isPressed: Boolean) {
        val scale = if (isPressed) 1.5f else 1f
        val scaleX = ObjectAnimator.ofFloat(mountainImageView, "scaleX", scale)
        val scaleY = ObjectAnimator.ofFloat(mountainImageView, "scaleY", scale)

        scaleX.duration = 300
        scaleY.duration = 300

        scaleX.start()
        scaleY.start()
    }
}
