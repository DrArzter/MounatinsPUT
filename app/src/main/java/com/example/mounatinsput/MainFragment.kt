import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.mounatinsput.Mountain
import com.example.mounatinsput.R
import com.example.mountainsput.MountainAdapter
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

class MainFragment : Fragment() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isTablet = resources.getBoolean(R.bool.isTablet) // Дебуговое сообщение
        println("[MyTag] isTablet = $isTablet")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val isTablet = resources.getBoolean(R.bool.isTablet)
        val view: View

        if (!isTablet) {
            view = inflater.inflate(R.layout.fragment_main, container, false)
            listView = view.findViewById(R.id.listView)
        } else {
            view = inflater.inflate(R.layout.fragment_main_tablet, container, false)
            listView = view.findViewById(R.id.listView)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isTablet = resources.getBoolean(R.bool.isTablet)

        // Чтение данных из JSON-файла
        val jsonString = readJsonFromFile(requireContext(), "mountains.json")
        val gson = Gson()
        val mountainList = gson.fromJson(jsonString, Array<Mountain>::class.java).toList()

        val adapter = MountainAdapter(requireActivity(), mountainList)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, view, position, _ ->
            val selectedMountain = mountainList[position]

            val detailFragment = MountainDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("selectedMountain", selectedMountain)
                }
            }

            // Если это не планшет, заменяем текущий фрагмент деталей
            if (!isTablet) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                // Если это планшет, заменяем фрагмент деталей справа от списка
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.detailFragmentContainerTablet, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }


    private fun readJsonFromFile(context: Context, fileName: String): String {
        val inputStream = context.assets.open(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String? = bufferedReader.readLine()
        while (line != null) {
            stringBuilder.append(line)
            line = bufferedReader.readLine()
        }
        return stringBuilder.toString()
    }
}
