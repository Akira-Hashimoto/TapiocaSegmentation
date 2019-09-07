package tapioca.tapiocasegmentation

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc


class MainActivity : AppCompatActivity() {

    val takeIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val RESULT_CAMERA = 100

    val BUNDLE_IMG = "TAPI_IMG"
    val BUNDLE_NUM = "TAPI_NUM"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        OpenCVLoader.initDebug()
        Handler().postDelayed({
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startActivityForResult(takeIntent, RESULT_CAMERA)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.CAMERA
                    )
                ) {
                    AlertDialog.Builder(this)
                        .setMessage("許可してくれないんで画面を閉じます。")
                        .setPositiveButton("閉じる") { _, _ -> finish() }
                        .show()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CAMERA),
                        0
                    )
                }
            }
        }, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CAMERA) {
            data?.extras?.get("data").let {
                if (it is Bitmap) {
                    splash.visibility = View.GONE
                    loading.visibility = View.VISIBLE
                    val matrix = Matrix()
                    matrix.postRotate(90F)
                    val rotatedBitmap =
                        Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)
                    val inputFrame = Mat()
                    Utils.bitmapToMat(rotatedBitmap, inputFrame)
                    inputFrame.reshape(2)
                    Imgproc.cvtColor(inputFrame, inputFrame, Imgproc.COLOR_RGB2GRAY)
                    val circles = Mat()
                    Imgproc.HoughCircles(
                        inputFrame, circles, Imgproc.CV_HOUGH_GRADIENT,
                        1.0, 100.0, 24.0, 40.0, 40, 110
                    )
                    println(circles.cols())
                    val pt = Point()
// 検出した直線上を緑線で塗る
                    Imgproc.cvtColor(inputFrame, inputFrame, Imgproc.COLOR_GRAY2BGR)
                    for (i in 0 until circles.cols()) {
                        val data = circles.get(0, i)
                        pt.x = data[0]
                        pt.y = data[1]
                        val rho = data[2]
                        Imgproc.circle(
                            inputFrame,
                            pt,
                            rho.toInt(),
                            Scalar(0.0, 200.0, 0.0),
                            5
                        )
                    }
                    Utils.bitmapToMat(rotatedBitmap, inputFrame)
                    val intent = Intent(application, ResultActivity::class.java)
                    intent.putExtra(BUNDLE_NUM, circles.cols())
                    intent.putExtra(BUNDLE_IMG, rotatedBitmap)
                    Handler().postDelayed({
                        startActivity(intent)
                    }, 4000)
//                    splash.setImageBitmap(rotatedBitmap)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
