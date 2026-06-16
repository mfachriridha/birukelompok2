package com.example.birukelompok2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.example.birukelompok2.api.VolleyClient046
import com.example.birukelompok2.databinding.FragmentUsers046Binding
import com.example.birukelompok2.databinding.ItemUser046Binding
import com.example.birukelompok2.models.ApiResponse
import com.example.birukelompok2.models.User
import com.example.birukelompok2.utils.SessionManager046
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UsersFragment046 : Fragment() {
    private var _binding: FragmentUsers046Binding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager046
    private val gson = Gson()
    private val users = mutableListOf<User>()
    private lateinit var adapter: UserAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUsers046Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = SessionManager046(requireContext())

        adapter = UserAdapter(users)
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { loadUsers() }
        loadUsers()
    }

    override fun onResume() {
        super.onResume()
        loadUsers()
    }

    private fun loadUsers() {
        val token = session.getToken() ?: return
        val url = VolleyClient046.getBaseUrl() + "users046.php"
        val request = object : StringRequest(Method.POST, url,
            { response ->
                binding.swipeRefresh.isRefreshing = false
                try {
                    val apiResp = gson.fromJson(response, ApiResponse::class.java)
                    if (apiResp.success && apiResp.data != null) {
                        val data = apiResp.data
                        val usersArray = data.getAsJsonArray("users")
                        val type = object : TypeToken<List<User>>() {}.type
                        val list: List<User> = gson.fromJson(usersArray, type)
                        users.clear()
                        users.addAll(list)
                        adapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(requireContext(), "Network error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams() = hashMapOf("api_token" to token)
        }
        VolleyClient046.getRequestQueue().add(request)
    }

    inner class UserAdapter(private val items: List<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemBinding = ItemUser046Binding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount() = items.size

        inner class ViewHolder(private val itemBinding: ItemUser046Binding) : RecyclerView.ViewHolder(itemBinding.root) {
            fun bind(user: User) {
                itemBinding.tvName.text = user.name
                itemBinding.tvEmail.text = user.email
                itemBinding.tvRole.text = user.role
                itemBinding.tvAvatar.text = user.name.firstOrNull()?.uppercase() ?: "?"

                val avatarColor = if (user.role == "admin") R.color.biru_primary else R.color.cadet_blue
                itemBinding.tvAvatar.setBackgroundColor(resources.getColor(avatarColor, null))

                itemBinding.root.setOnClickListener {
                    if (user.role != "admin") {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Hapus User")
                            .setMessage("Yakin ingin menghapus ${user.name}?")
                            .setPositiveButton("Ya") { _, _ -> deleteUser(user.id) }
                            .setNegativeButton("Batal", null)
                            .show()
                    }
                }
            }
        }

        private fun deleteUser(userId: Int) {
            val token = session.getToken() ?: return
            val url = VolleyClient046.getBaseUrl() + "delete_user046.php"
            val request = object : StringRequest(Method.POST, url,
                { response ->
                    try {
                        val apiResp = gson.fromJson(response, ApiResponse::class.java)
                        if (apiResp.success) {
                            Toast.makeText(requireContext(), "User dihapus", Toast.LENGTH_SHORT).show()
                            loadUsers()
                        } else {
                            Toast.makeText(requireContext(), apiResp.message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getParams() = hashMapOf("api_token" to token, "id" to userId.toString())
            }
            VolleyClient046.getRequestQueue().add(request)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
