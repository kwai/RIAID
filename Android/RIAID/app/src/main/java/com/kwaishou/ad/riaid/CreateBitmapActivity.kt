package com.kwaishou.ad.riaid

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.kuaishou.riaid.adbrowser.ADBrowser
import com.kuaishou.riaid.adbrowser.canvas.ADCanvas
import com.kuaishou.riaid.proto.nano.Node
import com.kuaishou.riaid.proto.nano.RiaidModel
import com.kuaishou.riaid.render.config.DSLRenderCreator
import com.kuaishou.riaid.render.util.ToolHelper
import com.kwaishou.ad.demo.DemoRiaidFactory
import com.kwaishou.ad.demo.WhiteWithButton
import com.kwaishou.ad.riaid.service.DefaultContainer
import com.kwaishou.ad.riaid.service.DemoBrowserService
import com.kwaishou.ad.riaid.service.DemoOutputEventListener

/**
 * 生成bitmap的示例
 */
class CreateBitmapActivity : AppCompatActivity() {
  private var adBrowser: ADBrowser? = null
  private val mainHandler: Handler = Handler(Looper.getMainLooper())

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_create_bitmap)
    val container = findViewById<FrameLayout>(R.id.container);
    // 这是使用画布生成的view的方式
    // val canvas = CreateBitmapCanvas(this)
    // initRiaid(canvas)
    // 这是直接使用Node去创建view
    val nodeView = createRenderCreator(getNode())
    container.addView(nodeView)
    mainHandler.postDelayed({
//      val bitmap = canvasConversionBitmap(canvas)
      val bitmap = viewConversionBitmap(nodeView!!)
      findViewById<ImageView>(R.id.bitmap_img).setImageBitmap(bitmap)
    }, 2000)
  }

  private fun viewConversionBitmap(view: View): Bitmap? {
    val bmp = Bitmap.createBitmap(300, 200, Bitmap.Config.ARGB_8888)
    val c = Canvas(bmp)
    c.drawColor(Color.WHITE)
    /** 如果不设置canvas画布为白色，则生成透明  */
    view.layout(0, 0, 300, 200)
    view.draw(c)
    return bmp
  }


  /**
   * 通过Node生成一个view
   */
  private fun createRenderCreator(render: Node?): View? {
    // 需要一些基础服务，其中loadImage和databinding是需要的
    val defaultContainer = DefaultContainer(this)
    val dslRenderCreator = DSLRenderCreator.Builder(defaultContainer)
      // 指定最大宽高约束，实际生成的view不一定是这个宽高。
      .withMaxWidth(ToolHelper.dip2px(this, 300f))
      .withMaxHeight(ToolHelper.dip2px(this, 200f))
      .withPbData(render)
      .build()
    return dslRenderCreator?.render(this)
  }

  fun canvasConversionBitmap(canvas: ADCanvas): Bitmap? {
    val bmp = Bitmap.createBitmap(canvas.canvasWidth, canvas.canvasHeight, Bitmap.Config.ARGB_8888)
    val c = Canvas(bmp)
    c.drawColor(Color.WHITE)
    /** 如果不设置canvas画布为白色，则生成透明  */
    canvas.canvas.layout(0, 0, canvas.canvasWidth, canvas.canvasHeight)
    canvas.canvas.draw(c)
    return bmp
  }


  /**
   * 注意，RiaidModel有两个包名，客户端应该用这个com.kuaishou.riaid.proto.nano包名下的。
   */
  private fun getRiaidModel(): RiaidModel? {
    // 这个其实应该是打成json，交给服务做变量的databinding的，这里demo示例就简单来吧
    val bytes = DemoRiaidFactory().create().toByteArray()
    var riaidModel: RiaidModel? = null
    try {
      riaidModel = RiaidModel.parseFrom(bytes)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return riaidModel
  }

  /**
   * 需要生成Node
   */
  private fun getNode(): Node? {
    // 这个其实应该是打成json，交给服务做变量的databinding的，这里demo示例就简单来吧
    // 这个bytes应该是服务下发的
    val bytes =  WhiteWithButton().createNode().toByteArray()
    var node: Node? = null
    try {
      // 拿到服务下发的bytes解析成真正的Node对象。
      node = Node.parseFrom(bytes)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return node
  }

  private fun initRiaid(canvas: ADCanvas) {
    adBrowser = ADBrowser(this, getRiaidModel()!!, canvas, DemoBrowserService())
    adBrowser?.addBrowserMetricsEventListener(DemoOutputEventListener(this))
    adBrowser?.onDidLoad()
  }
}