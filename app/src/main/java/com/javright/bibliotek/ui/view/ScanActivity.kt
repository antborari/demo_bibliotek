package com.javright.bibliotek.ui.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.javright.bibliotek.databinding.ActivityScanQrBinding
import com.javright.bibliotek.ui.contract.ScanningResultListener
import com.javright.bibliotek.ui.widget.MLBarcodeAnalyzer
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class ScanActivity : AppCompatActivity() {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private val binding: ActivityScanQrBinding by lazy {
        ActivityScanQrBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {
        if (isDestroyed || isFinishing) {
            return
        }

        cameraProvider?.unbindAll()

        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val orientationEventListener = object : OrientationEventListener(this as Context) {
            override fun onOrientationChanged(orientation : Int) {
                val rotation : Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageAnalysis.targetRotation = rotation
            }
        }

        orientationEventListener.enable()

        val scanningListener =  object : ScanningResultListener{
            override fun onScanned(result: String) {
                runOnUiThread {
                    imageAnalysis.clearAnalyzer()
                    cameraProvider?.unbindAll()
                    setBook(result)
                }
            }
        }

        val analyzer: ImageAnalysis.Analyzer = MLBarcodeAnalyzer(scanningListener)
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)
        preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
        cameraProvider?.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)
    }

    private fun setBook(text: String) {
        val intent = Intent(this, AddBookActivity::class.java).apply {
            putExtra(EXTRA_ADD, text)
        }
        startActivity(intent)
    }
}