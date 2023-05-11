package com.bursa.taksi.ui.view.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bursa.taksi.R
import com.bursa.taksi.databinding.FragmentSettingsBinding
import com.bursa.taksi.model.menuitem.MenuItem
import com.bursa.taksi.model.menuitem.MenuItemAdapter
import com.bursa.taksi.util.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings), MenuItemAdapter.OnItemClickListener {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { MenuItemAdapter(this) }
    private val auth = Firebase.auth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)

            buttonNavBack.setOnClickListener {
                findNavController().popBackStack()
            }

            helloText.text = if (auth.currentUser != null) {
                getString(R.string.hello_text) + " " + auth.currentUser!!.displayName
            } else {
                getString(R.string.hello_text).replace(", ", "")
            }
        }

        adapter.setData(Constants.SETTINGS_MENU_ITEMS)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(menuItem: MenuItem) {
        when (menuItem.type) {
            MenuItem.MenuItemTypes.Options -> {
                val action = SettingsFragmentDirections.actionSettingsFragmentToOptionsFragment()
                findNavController().navigate(action)
            }
            MenuItem.MenuItemTypes.Account -> {
                val action = SettingsFragmentDirections.actionSettingsFragmentToAccountFragment()
                findNavController().navigate(action)
            }
        }
    }
}