import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.bumptech.glide.Glide
import com.example.mounatinsput.Mountain
import com.example.mounatinsput.R
import com.example.mounatinsput.StopwatchFragment

class MountainDetailFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mountain_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve selected mountain from arguments
        val mountain = arguments?.getParcelable<Mountain>("selectedMountain")

        // Set mountain image
        val mountainImageView = view.findViewById<ImageView>(R.id.mountainImage)
        mountain?.mountainImage?.let {
            val resourceId =
                resources.getIdentifier(it, "drawable", requireContext().packageName)
            Glide.with(requireContext()).load(resourceId).into(mountainImageView)
        }

        // Set mountain name
        val mountainNameTextView = view.findViewById<TextView>(R.id.mountainNameTextView)
        mountainNameTextView.text = mountain?.name

        // Set height
        val heightTextView = view.findViewById<TextView>(R.id.length)
        heightTextView.text = mountain?.length.toString() + " m"

        // Set description
        val descriptionTextView = view.findViewById<TextView>(R.id.description)
        descriptionTextView.text = mountain?.description

        childFragmentManager.commit {
            replace(R.id.stopwatchContainer, StopwatchFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("selectedMountain", mountain)
                }
            })
        }
    }
}
