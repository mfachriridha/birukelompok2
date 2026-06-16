package com.example.birukelompok2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.example.birukelompok2.api.VolleyClient046
import com.example.birukelompok2.databinding.FragmentRooms046Binding
import com.example.birukelompok2.databinding.ItemRoom046Binding
import com.example.birukelompok2.models.Room
import com.example.birukelompok2.models.ApiResponse
import com.example.birukelompok2.utils.SessionManager046
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomsFragment046 : Fragment() {
    private var _binding: FragmentRooms046Binding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager046
    private val gson = Gson()
    private val rooms = mutableListOf<Room>()
    private lateinit var adapter: RoomAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRooms046Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = SessionManager046(requireContext())

        adapter = RoomAdapter(rooms)
        binding.rvRooms.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvRooms.adapter = adapter

        if (session.isAdmin()) {
            binding.fabAdd.visibility = View.VISIBLE
            binding.fabAdd.setOnClickListener {
                startActivity(Intent(requireContext(), RoomFormActivity046::class.java))
            }
        }

        binding.swipeRefresh.setOnRefreshListener { loadRooms() }
        loadRooms()
    }

    override fun onResume() {
        super.onResume()
        loadRooms()
    }

    private fun loadRooms() {
        val token = session.getToken() ?: return
        val url = VolleyClient046.getBaseUrl() + "rooms046.php"
        val request = object : StringRequest(Method.POST, url,
            { response ->
                binding.swipeRefresh.isRefreshing = false
                try {
                    val apiResp = gson.fromJson(response, ApiResponse::class.java)
                    if (apiResp.success && apiResp.data != null) {
                        val data = apiResp.data
                        val roomsArray = data.getAsJsonArray("rooms")
                        val type = object : TypeToken<List<Room>>() {}.type
                        val list: List<Room> = gson.fromJson(roomsArray, type)
                        rooms.clear()
                        rooms.addAll(list)
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

    inner class RoomAdapter(private val items: List<Room>) : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemBinding = ItemRoom046Binding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val room = items[position]
            holder.bind(room)
        }

        override fun getItemCount() = items.size

        inner class ViewHolder(private val itemBinding: ItemRoom046Binding) : RecyclerView.ViewHolder(itemBinding.root) {
            fun bind(room: Room) {
                itemBinding.tvNama.text = room.nama
                itemBinding.tvLokasi.text = room.lokasi
                itemBinding.tvKapasitas.text = "Kapasitas: ${room.kapasitas} orang"

                if (room.fotoUrl.isNotEmpty()) {
                    val imageUrl = VolleyClient046.getBaseUrl() + room.fotoUrl
                    val request = ImageRequest(imageUrl,
                        { response ->
                            itemBinding.ivPhoto.setImageBitmap(response)
                        }, 0, 0, ImageRequest.ScaleType.CENTER_CROP, null,
                        { error ->
                            // leave default background
                        })
                    VolleyClient046.getRequestQueue().add(request)
                }

                itemBinding.root.setOnClickListener {
                    if (session.isAdmin()) {
                        val options = arrayOf("Edit", "Hapus")
                        AlertDialog.Builder(requireContext())
                            .setTitle(room.nama)
                            .setItems(options) { _, which ->
                                when (which) {
                                    0 -> {
                                        val intent = Intent(requireContext(), RoomFormActivity046::class.java)
                                        intent.putExtra("room_id", room.id)
                                        intent.putExtra("room_nama", room.nama)
                                        intent.putExtra("room_lokasi", room.lokasi)
                                        intent.putExtra("room_kapasitas", room.kapasitas)
                                        intent.putExtra("room_fasilitas", room.fasilitas)
                                        intent.putExtra("room_deskripsi", room.deskripsi)
                                        intent.putExtra("room_foto_url", room.fotoUrl)
                                        startActivity(intent)
                                    }
                                    1 -> deleteRoom(room.id)
                                }
                            }
                            .show()
                    }
                }
            }
        }

        private fun deleteRoom(roomId: Int) {
            AlertDialog.Builder(requireContext())
                .setTitle("Hapus Ruangan")
                .setMessage("Yakin ingin menghapus ruangan ini?")
                .setPositiveButton("Ya") { _, _ ->
                    val token = session.getToken() ?: return@setPositiveButton
                    val url = VolleyClient046.getBaseUrl() + "delete_room046.php"
                    val request = object : StringRequest(Method.POST, url,
                        { response ->
                            try {
                                val apiResp = gson.fromJson(response, ApiResponse::class.java)
                                if (apiResp.success) {
                                    Toast.makeText(requireContext(), "Ruangan dihapus", Toast.LENGTH_SHORT).show()
                                    loadRooms()
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
                        override fun getParams() = hashMapOf("api_token" to token, "id" to roomId.toString())
                    }
                    VolleyClient046.getRequestQueue().add(request)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
