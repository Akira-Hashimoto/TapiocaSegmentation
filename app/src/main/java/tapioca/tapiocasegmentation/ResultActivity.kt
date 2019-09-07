package tapioca.tapiocasegmentation

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_result.*
import tapioca.tapiocasegmentation.MainActivity.Companion.BUNDLE_IMG
import tapioca.tapiocasegmentation.MainActivity.Companion.BUNDLE_NUM

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val tapiocaNum = intent.getIntExtra(BUNDLE_NUM, 0)
        val bitmap = intent.getParcelableExtra<Bitmap>(BUNDLE_IMG)

        tapioca_img.setImageBitmap(bitmap)
        tapioca_quantity.text = tapiocaNum.toString() + "個"
        calory.text = calory.toString() + "cal"


    }
}
