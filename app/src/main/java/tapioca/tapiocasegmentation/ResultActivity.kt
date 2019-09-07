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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_result.*
import org.opencv.core.*
import tapioca.tapiocasegmentation.MainActivity.Companion.BUNDLE_IMG
import tapioca.tapiocasegmentation.MainActivity.Companion.BUNDLE_NUM

class ResultActivity : AppCompatActivity() {

    val takeIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val RESULT_CAMERA = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val tapiocaNum = intent.getIntExtra(BUNDLE_NUM, 0)
        val bitmap = intent.getParcelableExtra<Bitmap>(BUNDLE_IMG)

        tapioca_img.setImageBitmap(bitmap)
        val quantityView = findViewById<TextView>(R.id.tapioca_quantity)
        val caloryView = findViewById<TextView>(R.id.calory)
        val againButton = findViewById<Button>(R.id.again_button)

        // TODO imageViewに受け取った画像をセット
        val quantity = 30
        val calory = 100
        quantityView.text = tapiocaNum.toString() + "個"
        caloryView.text = calory.toString() + "cal"


        againButton.setOnClickListener {
            // 飛ぶ
            println("もう一度")
        }

    }
}
