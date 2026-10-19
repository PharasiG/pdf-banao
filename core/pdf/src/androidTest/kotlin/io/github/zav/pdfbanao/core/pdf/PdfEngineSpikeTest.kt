package io.github.zav.pdfbanao.core.pdf

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.Debug
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory
import java.io.File
import java.util.Locale
import kotlin.system.measureNanoTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Task 0.4 spike: which PDF engine should make our PDFs?
 *
 * Makes the same 10-photo PDF two ways and logs file size, time and memory:
 * - PdfBox-Android, with each photo stored in the PDF as a JPEG.
 * - Android's built-in [PdfDocument], with each photo drawn on the page.
 *
 * Results and the decision: docs/decisions/001-pdf-engine.md.
 * Run `./gradlew :core:pdf:connectedDebugAndroidTest`, then read the numbers with
 * `adb logcat -d -s PdfEngineSpike`.
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class PdfEngineSpikeTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val outDir = File(instrumentation.targetContext.cacheDir, "pdf-engine-spike")

    @Before
    fun setUp() {
        PDFBoxResourceLoader.init(instrumentation.targetContext)
        outDir.mkdirs()
    }

    @After
    fun tearDown() {
        // To look at the PDFs, run with the instrumentation argument keepPdfs=true.
        if (InstrumentationRegistry.getArguments().getString("keepPdfs") != "true") {
            outDir.deleteRecursively()
        }
    }

    @Test
    fun pdfBoxMakesSmallerFilesThanPdfDocument() {
        val pdfBox = measure("PdfBox (JPEG)", File(outDir, "pdfbox.pdf"), ::writeWithPdfBox)
        val pdfDocument = measure(
            "Android PdfDocument",
            File(outDir, "pdfdocument.pdf"),
            ::writeWithPdfDocument,
        )
        val ratio = pdfDocument.bytes.toDouble() / pdfBox.bytes
        Log.i(TAG, "PdfDocument file is %.1f times the PdfBox file".format(Locale.ROOT, ratio))

        assertTrue(pdfBox.bytes < pdfDocument.bytes)
    }

    /** One untimed run that also checks memory, then [TIMED_RUNS] timed runs. */
    private fun measure(
        engine: String,
        output: File,
        write: (output: File, beforeSave: () -> Unit) -> Unit,
    ): Measurement {
        // The first run also warms up the code, so its time is not counted.
        val baseline = usedMemory()
        var heldBytes = 0L
        write(output) { heldBytes = usedMemory() - baseline }

        val millis = List(TIMED_RUNS) { measureNanoTime { write(output) {} } / NANOS_PER_MILLI }
        assertEquals(PAGE_COUNT, pageCount(output))

        val result = Measurement(output.length(), millis.sorted()[TIMED_RUNS / 2], heldBytes)
        Log.i(
            TAG,
            "$engine: ${result.bytes / KB} KB, ${result.medianMillis} ms (runs: $millis ms), " +
                "memory held before saving: ${result.heldBytes / KB / KB} MB",
        )
        return result
    }

    private fun writeWithPdfBox(
        output: File,
        beforeSave: () -> Unit,
    ) {
        PDDocument().use { document ->
            repeat(PAGE_COUNT) { index ->
                val photo = decodePhoto(index)
                val (pageWidth, pageHeight) = pageSizeFor(photo)
                val page = PDPage(PDRectangle(pageWidth.toFloat(), pageHeight.toFloat()))
                document.addPage(page)
                val image = JPEGFactory.createFromImage(document, photo, JPEG_QUALITY)
                // The box is centred, so its top edge is also its distance from the bottom,
                // where PDF coordinates start.
                val box = photoBox(photo, pageWidth, pageHeight)
                PDPageContentStream(document, page).use {
                    it.drawImage(image, box.left, box.top, box.width(), box.height())
                }
                photo.recycle()
            }
            beforeSave()
            output.outputStream().buffered().use { document.save(it) }
        }
    }

    private fun writeWithPdfDocument(
        output: File,
        beforeSave: () -> Unit,
    ) {
        val document = PdfDocument()
        try {
            val paint = Paint(Paint.FILTER_BITMAP_FLAG)
            repeat(PAGE_COUNT) { index ->
                val photo = decodePhoto(index)
                val (pageWidth, pageHeight) = pageSizeFor(photo)
                val page = document.startPage(
                    PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create(),
                )
                page.canvas.drawBitmap(photo, null, photoBox(photo, pageWidth, pageHeight), paint)
                document.finishPage(page)
                photo.recycle()
            }
            beforeSave()
            output.outputStream().buffered().use { document.writeTo(it) }
        } finally {
            document.close()
        }
    }

    /**
     * The sample photos are already smaller than Medium quality (1654 × 2339 px), so they are
     * used at full size. EXIF rotation is not fixed here: task 1.2's ImageProcessor does that.
     */
    private fun decodePhoto(index: Int): Bitmap {
        val name = SAMPLE_PHOTOS[index % SAMPLE_PHOTOS.size]
        return instrumentation.context.assets.open(name).use {
            checkNotNull(BitmapFactory.decodeStream(it)) { "Could not decode $name" }
        }
    }

    /** A4 in points (1/72 inch), turned sideways for a landscape photo (PRD F4). */
    private fun pageSizeFor(photo: Bitmap): Pair<Int, Int> =
        if (photo.width > photo.height) A4_HEIGHT to A4_WIDTH else A4_WIDTH to A4_HEIGHT

    /** The photo as big as fits inside the margin, centred on the page. */
    private fun photoBox(
        photo: Bitmap,
        pageWidth: Int,
        pageHeight: Int,
    ): RectF {
        val scale = minOf(
            (pageWidth - 2 * MARGIN) / photo.width,
            (pageHeight - 2 * MARGIN) / photo.height,
        )
        val width = photo.width * scale
        val height = photo.height * scale
        val left = (pageWidth - width) / 2
        val top = (pageHeight - height) / 2
        return RectF(left, top, left + width, top + height)
    }

    private fun pageCount(file: File): Int =
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
            PdfRenderer(fd).use { it.pageCount }
        }

    /** Memory in use right now: Java objects plus native memory (where bitmap pixels live). */
    private fun usedMemory(): Long {
        val runtime = Runtime.getRuntime()
        runtime.gc()
        return runtime.totalMemory() - runtime.freeMemory() + Debug.getNativeHeapAllocatedSize()
    }

    private data class Measurement(
        val bytes: Long,
        val medianMillis: Long,
        val heldBytes: Long,
    )

    private companion object {
        const val TAG = "PdfEngineSpike"
        const val PAGE_COUNT = 10
        const val TIMED_RUNS = 3
        const val NANOS_PER_MILLI = 1_000_000L
        const val KB = 1024

        /** Medium quality (ARCHITECTURE §6). */
        const val JPEG_QUALITY = 0.75f

        /** A4 page size in points. */
        const val A4_WIDTH = 595
        const val A4_HEIGHT = 842

        /** "Small" margin (PRD F4), in points: about 6 mm. */
        const val MARGIN = 18f

        val SAMPLE_PHOTOS = listOf(
            "photo_portrait.jpg",
            "photo_landscape.jpg",
            "photo_exif_rotate90.jpg",
            "photo_document.jpg",
        )
    }
}
