package com.example.birukelompok2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.birukelompok2.api.VolleyClient046
import com.example.birukelompok2.databinding.FragmentBookings046Binding
import com.example.birukelompok2.databinding.ItemBooking046Binding
import com.example.birukelompok2.models.ApiResponse
import com.example.birukelompok2.models.Booking
import com.example.birukelompok2.utils.SessionManager046
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BookingsFragment046 : Fragment() {
    private var _binding: FragmentBookings046Binding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager046
    private val gson = Gson()
    private val bookings = mutableListOf<Booking>()
    private lateinit var adapter: BookingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBookings046Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = SessionManager046(requireContext())

        adapter = BookingAdapter(bookings)
        binding.rvBookings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBookings.adapter = adapter

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), BookingFormActivity046::class.java))
        }

        binding.swipeRefresh.setOnRefreshListener { loadBookings() }
        loadBookings()
    }

    override fun onResume() {
        super.onResume()
        loadBookings()
    }

    private fun loadBookings() {
        val token = session.getToken() ?: return
        val url = VolleyClient046.getBaseUrl() + "bookings046.php"
        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                binding.swipeRefresh.isRefreshing = false
                try {
                    val apiResp = gson.fromJson(response, ApiResponse::class.java)
                    if (apiResp.success && apiResp.data != null && apiResp.data.isJsonObject) {
                        val data = apiResp.data
                        val bookingsArray = data.getAsJsonArray("bookings")
                        val type = object : TypeToken<List<Booking>>() {}.type
                        val list: List<Booking> = gson.fromJson(bookingsArray, type)
                        bookings.clear()
                        bookings.addAll(list)
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

    inner class BookingAdapter(private val items: List<Booking>) : RecyclerView.Adapter<BookingAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemBinding = ItemBooking046Binding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount() = items.size

        inner class ViewHolder(private val itemBinding: ItemBooking046Binding) : RecyclerView.ViewHolder(itemBinding.root) {
            fun bind(booking: Booking) {
                itemBinding.tvRuangan.text = booking.roomName
                itemBinding.tvPeminjam.text = booking.userName
                itemBinding.tvWaktu.text = "${booking.tanggal} | ${booking.jamMulai} - ${booking.jamSelesai}"

                val statusText = when (booking.status) {
                    "approved" -> "Disetujui"
                    "rejected" -> "Ditolak"
                    else -> "Pending"
                }
                itemBinding.tvStatus.text = statusText
                val statusColor = when (booking.status) {
                    "approved" -> resources.getColor(R.color.status_approved, null)
                    "rejected" -> resources.getColor(R.color.status_rejected, null)
                    else -> resources.getColor(R.color.status_pending, null)
                }
                itemBinding.tvStatus.setBackgroundColor(statusColor)

                if (session.isAdmin() && booking.status == "pending") {
                    itemBinding.layoutAdminActions.visibility = View.VISIBLE
                    itemBinding.btnApprove.setOnClickListener {
                        updateStatus(booking.id, "approved")
                    }
                    itemBinding.btnReject.setOnClickListener {
                        showRejectDialog(booking.id)
                    }
                } else {
                    itemBinding.layoutAdminActions.visibility = View.GONE
                }
            }
        }

        private fun updateStatus(bookingId: Int, status: String, note: String = "") {
            val token = session.getToken() ?: return
            val url = VolleyClient046.getBaseUrl() + "update_booking046.php"
            val request = object : StringRequest(Request.Method.POST, url,
                { response ->
                    try {
                        val apiResp = gson.fromJson(response, ApiResponse::class.java)
                        if (apiResp.success) {
                            Toast.makeText(requireContext(), "Status diperbarui", Toast.LENGTH_SHORT).show()
                            loadBookings()
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
                override fun getParams() = hashMapOf("api_token" to token, "id" to bookingId.toString(), "status" to status, "admin_note" to note)
            }
            VolleyClient046.getRequestQueue().add(request)
        }

        private fun showRejectDialog(bookingId: Int) {
            val input = android.widget.EditText(requireContext())
            input.hint = "Alasan penolakan"
            AlertDialog.Builder(requireContext())
                .setTitle("Tolak Booking")
                .setView(input)
                .setPositiveButton("Tolak") { _, _ ->
                    updateStatus(bookingId, "rejected", input.text.toString().trim())
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
