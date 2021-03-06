package tw.tonyyang.englishwords

import android.os.Bundle
import android.view.Window
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(layoutResource)
        initToolbar()
        onViewCreated()
    }

    @CallSuper
    protected open fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar).apply {
            setLogo(R.drawable.ic_launcher)
            title = getString(R.string.app_name)
            setTitleTextColor(ContextCompat.getColor(this@BaseActivity, R.color.white))
        }
        setSupportActionBar(toolbar)
    }

    abstract val layoutResource: Int
    abstract fun onViewCreated()
}