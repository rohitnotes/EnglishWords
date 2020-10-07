package tw.tonyyang.englishwords.util

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import tw.tonyyang.englishwords.chooser.FileChooserFragment
import tw.tonyyang.englishwords.RequestCodeStore
import tw.tonyyang.englishwords.preference.GlobalPreference

object PermissionManager {
    private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    interface PermissionCallback {
        fun onPermissionGranted()
        fun onShowGuide()
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun verifyStoragePermissions(activity: Activity, fragment: FileChooserFragment, permissionCallback: PermissionCallback) {
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (needShowGuide(activity)) {
                permissionCallback.onShowGuide()
                return
            }
            setFlag(activity)
            fragment.requestPermissions(PERMISSIONS_STORAGE, RequestCodeStore.REQUEST_EXTERNAL_STORAGE)
        } else {
            permissionCallback.onPermissionGranted()
        }
    }

    private fun getFlag(context: Context): Boolean {
        return GlobalPreference.getBoolean(context, GlobalPreference.FLAG_PERMISSIONS_STORAGE)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun setFlag(activity: Activity) {
        val flag = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        GlobalPreference.put(context = activity, key = GlobalPreference.FLAG_PERMISSIONS_STORAGE, value = flag)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun needShowGuide(activity: Activity): Boolean {
        return getFlag(activity) && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}