// ReportsActivity.kt
package com.example.teamschat.ui.reports

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teamschat.databinding.ActivityReportsBinding
import com.example.teamschat.vm.ReportsViewModel
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import java.util.Locale

class ReportsActivity : AppCompatActivity() {

    private lateinit var b: ActivityReportsBinding
    private val vm: ReportsViewModel by viewModels()
    private val adapter = ReportsAdapter()

    private val reqPermLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op, we try again when printing */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.recycler.layoutManager = LinearLayoutManager(this)
        b.recycler.adapter = adapter
        b.recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        b.btnFrom.setOnClickListener { pickDate { d -> vm.setFrom(d); showDates() } }
        b.btnTo.setOnClickListener { pickDate { d -> vm.setTo(d); showDates() } }

        b.btnGetReport.setOnClickListener { vm.fetch() }
        b.btnPrint.setOnClickListener { tryPrintPdf() }

        lifecycleScope.launchWhenStarted {
            vm.ui.collectLatest { st ->
                b.progress.visibility = if (st.loading) View.VISIBLE else View.GONE
                if (st.error != null) {
                    Toast.makeText(this@ReportsActivity, st.error, Toast.LENGTH_LONG).show()
                }
                adapter.submit(st.data)
                b.btnPrint.visibility = if (st.data.isNotEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showDates() {
        val st = vm.ui.value
        val label = if (st.from.isNotBlank() && st.to.isNotBlank())
            "From ${st.from}  To ${st.to}" else "Select a date range"
        b.tvDates.text = label
    }

    // Simple DatePickerDialog -> YYYY-MM-DD
    private fun pickDate(onPicked: (String) -> Unit) {
        val now = Calendar.getInstance()
        val dp = android.app.DatePickerDialog(
            this,
            { _: DatePicker, y: Int, m: Int, d: Int ->
                val str = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)
                onPicked(str)
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )
        dp.show()
    }

    private fun tryPrintPdf() {
        // For Android < Q, write permission for external storage
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val perm = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(perm), 1001)
                return
            }
        }
        val chats = vm.ui.value.data
        if (chats.isEmpty()) {
            Toast.makeText(this, "No chats to print", Toast.LENGTH_SHORT).show()
            return
        }
        val from = vm.ui.value.from
        val to = vm.ui.value.to
        val uri = makePdf(chats, from, to)
        if (uri != null) {
            Toast.makeText(this, "PDF saved: $uri", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_LONG).show()
        }
    }

    private fun makePdf(chats: List<com.example.teamschat.data.model.Chat>, from: String, to: String): Uri? {
        val pdf = PdfDocument()
        val pageWidth = 595  // A4 width in points (approx at 72dpi)
        val pageHeight = 842 // A4 height in points
        val left = 40
        val topStart = 60
        val lineGap = 18

        val titlePaint = Paint().apply { textSize = 16f; isFakeBoldText = true }
        val textPaint = Paint().apply { textSize = 12f }

        var y = topStart
        var pageNum = 1
        var page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create())
        var canvas: Canvas = page.canvas

        fun newPage() {
            pdf.finishPage(page)
            pageNum++
            page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create())
            canvas = page.canvas
            y = topStart
        }

        // Header
        canvas.drawText("Chat Report ($from to $to)", left.toFloat(), y.toFloat(), titlePaint)
        y += lineGap * 2

        chats.forEach { c ->
            val user = c.user?.name ?: "Unknown"
            val time = try {
                c.created_at.substring(0, 19).replace('T', ' ')
            } catch (_: Exception) { c.created_at }

            val lines = listOf(
                "[$time] ${user}:",
                "   ${c.message}"
            )

            lines.forEach { line ->
                // auto-wrap long lines
                val wrapped = wrapText(line, textPaint, pageWidth - left * 2)
                wrapped.forEach { wLine ->
                    if (y + lineGap > pageHeight - 40) newPage()
                    canvas.drawText(wLine, left.toFloat(), y.toFloat(), textPaint)
                    y += lineGap
                }
            }
            y += lineGap / 2
        }

        // Save PDF
        return try {
            val name = "chat_report_${from}_to_${to}.pdf"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = contentResolver
                val cv = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, name)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv)
                if (uri != null) {
                    resolver.openOutputStream(uri).use { out -> pdf.writeTo(out) }
                    cv.clear()
                    cv.put(MediaStore.Downloads.IS_PENDING, 0)
                    resolver.update(uri, cv, null, null)
                }
                pdf.close()
                uri
            } else {
                val dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val file = File(dir, name)
                FileOutputStream(file).use { out -> pdf.writeTo(out) }
                pdf.close()
                Uri.fromFile(file)
            }
        } catch (e: Exception) {
            pdf.close()
            null
        }
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var cur = StringBuilder()
        for (w in words) {
            val test = if (cur.isEmpty()) w else cur.toString() + " " + w
            if (paint.measureText(test) <= maxWidth) {
                if (cur.isEmpty()) cur.append(w) else { cur.append(" "); cur.append(w) }
            } else {
                if (cur.isNotEmpty()) lines.add(cur.toString())
                cur = StringBuilder(w)
            }
        }
        if (cur.isNotEmpty()) lines.add(cur.toString())
        return lines
    }
}
