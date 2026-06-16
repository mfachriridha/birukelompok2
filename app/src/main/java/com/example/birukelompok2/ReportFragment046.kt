package com.example.birukelompok2

import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.birukelompok2.api.VolleyClient046
import com.example.birukelompok2.databinding.FragmentReport046Binding
import com.example.birukelompok2.models.ApiResponse
import com.example.birukelompok2.utils.SessionManager046
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import com.google.gson.JsonObject

class ReportFragment046 : Fragment() {
    private var _binding: FragmentReport046Binding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager046
    private val gson = Gson()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReport046Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = SessionManager046(requireContext())

        setupCharts()
        loadReport()

        binding.btnExportPdf.setOnClickListener {
            exportPdf()
        }
    }

    private fun setupCharts() {
        binding.pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            setEntryLabelTextSize(12f)
            isDrawHoleEnabled = true
            holeRadius = 40f
            setHoleColor(Color.TRANSPARENT)
            transparentCircleRadius = 45f
            legend.isEnabled = true
        }

        binding.barChart.apply {
            description.isEnabled = false
            setFitBars(true)
            setScaleEnabled(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
            legend.isEnabled = true
        }
    }

    private fun loadReport() {
        val token = session.getToken() ?: return
        val url = VolleyClient046.getBaseUrl() + "report046.php"
        val request = object : StringRequest(Request.Method.POST, url,
            { response ->
                try {
                    val apiResp = gson.fromJson(response, ApiResponse::class.java)
                    if (apiResp.success && apiResp.data != null) {
                        val data = apiResp.data
                        binding.tvTotal.text = "Total: ${data.get("total_bookings")?.asInt ?: 0}"

                        val statusDist = data.getAsJsonArray("status_distribution")
                        if (statusDist != null && statusDist.size() > 0) {
                            val entries = mutableListOf<PieEntry>()
                            for (i in 0 until statusDist.size()) {
                                val item = statusDist[i].asJsonObject
                                val label = item.get("status")?.asString ?: "unknown"
                                val count = item.get("count")?.asInt ?: 0
                                entries.add(PieEntry(count.toFloat(), label))
                            }
                            val dataSet = PieDataSet(entries, "Status Booking")
                            dataSet.colors = listOf(
                                Color.parseColor("#F59E0B"),
                                Color.parseColor("#22C55E"),
                                Color.parseColor("#EF4444")
                            )
                            dataSet.valueTextSize = 12f
                            binding.pieChart.data = PieData(dataSet)
                            binding.pieChart.invalidate()
                        }

                        val perRoom = data.getAsJsonArray("per_room")
                        if (perRoom != null && perRoom.size() > 0) {
                            val entries = mutableListOf<BarEntry>()
                            val labels = mutableListOf<String>()
                            for (i in 0 until perRoom.size()) {
                                val item = perRoom[i].asJsonObject
                                val nama = item.get("nama")?.asString ?: ""
                                val count = item.get("count")?.asInt ?: 0
                                entries.add(BarEntry(i.toFloat(), count.toFloat()))
                                labels.add(nama)
                            }
                            val dataSet = BarDataSet(entries, "Booking per Ruangan")
                            dataSet.color = Color.parseColor("#2563EB")
                            dataSet.valueTextSize = 12f

                            binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                            binding.barChart.xAxis.granularity = 1f
                            binding.barChart.xAxis.setLabelCount(labels.size, false)
                            binding.barChart.data = BarData(dataSet)
                            binding.barChart.invalidate()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams() = hashMapOf("api_token" to token)
        }
        VolleyClient046.getRequestQueue().add(request)
    }

    private fun exportPdf() {
        try {
            val pdfDoc = android.graphics.pdf.PdfDocument()
            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

            val titlePaint = android.graphics.Paint().apply {
                color = Color.parseColor("#2563EB")
                textSize = 24f
                isFakeBoldText = true
            }
            val bodyPaint = android.graphics.Paint().apply {
                color = Color.BLACK
                textSize = 12f
            }

            canvas.drawText("Biru - Laporan Booking Ruangan", 40f, 50f, titlePaint)
            canvas.drawText("Generated: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("id")).format(java.util.Date())}", 40f, 75f, bodyPaint)

            // Draw pie chart bitmap
            val pieBitmap = binding.pieChart.chartBitmap
            val scaledPie = android.graphics.Bitmap.createScaledBitmap(pieBitmap, 400, 300, true)
            canvas.drawBitmap(scaledPie, 95f, 95f, null)

            // Draw bar chart bitmap
            val barBitmap = binding.barChart.chartBitmap
            val scaledBar = android.graphics.Bitmap.createScaledBitmap(barBitmap, 500, 300, true)
            canvas.drawBitmap(scaledBar, 45f, 415f, null)

            pdfDoc.finishPage(page)

            val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val file = java.io.File(dir, "biru_laporan.pdf")
            java.io.FileOutputStream(file).use { out ->
                pdfDoc.writeTo(out)
            }
            pdfDoc.close()

            Toast.makeText(requireContext(), "PDF saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "PDF error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
