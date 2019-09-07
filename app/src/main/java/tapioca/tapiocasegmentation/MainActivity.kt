package tapioca.tapiocasegmentation

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.imgproc.GeneralizedHough
import org.opencv.imgproc.Imgproc
import android.R.attr.y
import android.R.attr.x
import org.opencv.core.*


class MainActivity : AppCompatActivity() {

    val takeIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val RESULT_CAMERA = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println(OpenCVLoader.OPENCV_VERSION)

        takeButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startActivityForResult(takeIntent, RESULT_CAMERA)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
                    AlertDialog.Builder(this)
                        .setMessage("許可してくれないんで画面を閉じます。")
                        .setPositiveButton("閉じる") { _, _ -> finish() }
                        .show()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 0)
                }
            }
        }

        httpButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
//                var tapimap: Bitmap? = null
//                withContext(Dispatchers.IO) {
//                    tapimap = HttpUtil.getTapiocaImage(
//                        "https://us-central1-tapiocachallenge.cloudfunctions.net/getTapioca",
//                        bitmap
//                    )
//                }
//                val createScaledBitmap = Bitmap.createScaledBitmap(bitmap, 270, 480, true)
//                480 * 270
                println("initDebug ${OpenCVLoader.initDebug()}")
                val inputFrame = Mat()
                Utils.bitmapToMat(bitmap, inputFrame)
                inputFrame.reshape(2)
//                val frame480 = Mat.zeros(480, 640, CvType.CV_8UC3)
//                Imgproc.resize(inputFrame, frame480, Size(), 1.0, 1.0, Imgproc.INTER_NEAREST)
                Imgproc.cvtColor(inputFrame, inputFrame, Imgproc.COLOR_RGB2GRAY)
//                Imgproc.threshold(
//                    frame480, frame480, 0.0, 255.0,
//                    Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU
//                )
                val circles = Mat()
                Imgproc.HoughCircles(inputFrame, circles, Imgproc.CV_HOUGH_GRADIENT,
                    1.0, 100.0,24.0,40.0,40,110)
                println(circles.cols())
                val pt = Point()
// 検出した直線上を緑線で塗る
                Imgproc.cvtColor(inputFrame, inputFrame, Imgproc.COLOR_GRAY2BGR)
                for (i in 0 until circles.cols()) {
                    val data = circles.get(0, i)
                    pt.x = data[0]
                    pt.y = data[1]
                    val rho = data[2]
                    Imgproc.circle(inputFrame, pt, rho.toInt(), Scalar(0.0, 200.0, 0.0), 5)
                }
                Utils.matToBitmap(inputFrame, bitmap)
                imageView.setImageBitmap(bitmap)
//                GlobalScope.launch(Dispatchers.Main) {
//                    imageView.setImageBitmap(createScaledBitmap)
//                }

//                val tapimap = HttpUtil.getTapiocaImage("https://us-central1-logical-waters-250102.cloudfunctions.net/tapicathon_python37", bitmap)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CAMERA) {
            data?.extras?.get("data").let {
                if (it is Bitmap) {
                    val matrix = Matrix()
                    matrix.postRotate(90F)
                    val rotatedBitmap = Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)
                    GlobalScope.launch(Dispatchers.IO) {
                        var tapimap: Bitmap? = null
                        withContext(Dispatchers.IO) {
                            tapimap = HttpUtil.getTapiocaImage(
                                "https://us-central1-tapiocachallenge.cloudfunctions.net/getTapioca",
                                rotatedBitmap
                            )
                        }
                        GlobalScope.launch(Dispatchers.Main) {
                            imageView.setImageBitmap(tapimap)
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(takeIntent, RESULT_CAMERA)
                } else {
                    AlertDialog.Builder(this)
                        .setMessage("許可してくれないんで画面を閉じます。")
                        .setPositiveButton("閉じる") { _, _ -> finish() }
                        .show()
                }
            }
            else -> {

            }
        }
    }

}
