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
import org.opencv.imgproc.GeneralizedHough

class MainActivity : AppCompatActivity() {

    val takeIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val RESULT_CAMERA = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                val createScaledBitmap = Bitmap.createScaledBitmap(bitmap, 270, 480, true)
//                480 * 270

                GlobalScope.launch(Dispatchers.Main) {
                    imageView.setImageBitmap(createScaledBitmap)
                }

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
