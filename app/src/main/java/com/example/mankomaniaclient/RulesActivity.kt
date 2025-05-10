package com.example.mankomaniaclient

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class RulesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backgroundColor = Color.parseColor("#5E9C8D")

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
        }

        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
        }

        scrollView.addView(linearLayout)
        setContentView(scrollView)

        val file = File(cacheDir, "rules.pdf")
        assets.open("rules.pdf").use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)

        val windowMetrics = windowManager.currentWindowMetrics
        val screenWidth = windowMetrics.bounds.width()

        for (i in 0 until pdfRenderer.pageCount) {
            val page = pdfRenderer.openPage(i)

            val scale = screenWidth.toFloat() / page.width
            val height = (page.height * scale).toInt()

            val bitmap = Bitmap.createBitmap(screenWidth, height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()

            val imageView = ImageView(this).apply {
                setImageBitmap(bitmap)
                adjustViewBounds = true
                scaleType = ImageView.ScaleType.FIT_XY
                setBackgroundColor(backgroundColor)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            linearLayout.addView(imageView)
        }

        pdfRenderer.close()
        fileDescriptor.close()
    }
}
