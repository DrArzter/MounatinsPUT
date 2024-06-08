package com.example.mounatinsput

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mountainsput.MountainRecyclerViewAdapter
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

class MainFragment : Fragment(), MountainRecyclerViewAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Read data from JSON file
        val jsonString = readJsonFromFile(requireContext(), "mountains.json")
        val gson = Gson()
        val mountainList = gson.fromJson(jsonString, Array<Mountain>::class.java).toList()

        val adapter = MountainRecyclerViewAdapter(requireActivity(), mountainList, this)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(mountain: Mountain) {
        val detailFragment = MountainDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable("selectedMountain", mountain)
            }
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
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
