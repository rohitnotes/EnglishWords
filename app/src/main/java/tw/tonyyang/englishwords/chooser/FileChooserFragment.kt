package tw.tonyyang.englishwords.chooser

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.greenrobot.eventbus.EventBus
import tw.tonyyang.englishwords.Logger
import tw.tonyyang.englishwords.R
import tw.tonyyang.englishwords.RealTimeUpdateEvent
import tw.tonyyang.englishwords.RequestCodeStore
import tw.tonyyang.englishwords.databinding.FragmentDropboxchooserBinding
import tw.tonyyang.englishwords.util.PermissionManager
import tw.tonyyang.englishwords.util.PermissionManager.PermissionCallback
import tw.tonyyang.englishwords.util.UiUtils

class FileChooserFragment : Fragment() {

    private lateinit var binding: FragmentDropboxchooserBinding

    private val viewModel: FileChooserViewModel by activityViewModels()

    private var fileUrl: String? = null

    private var progress: AlertDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDropboxchooserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    private fun initViews() {
        activity?.let {
            progress = UiUtils.getProgressDialog(it, it.getString(R.string.loading_message))
        }
        binding.btnChooser.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                activity?.let {
                    PermissionManager.verifyStoragePermissions(it, this@FileChooserFragment, object : PermissionCallback {
                        override fun onPermissionGranted() {
                            chooseFileFromLocal()
                        }

                        override fun onShowGuide() {
                            Toast.makeText(activity, "需要取得您的讀寫權限才能正常存取手機檔案，請至\"設定\"開啟讀寫權限。", Toast.LENGTH_LONG).show()
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", activity?.packageName, null)
                            intent.data = uri
                            activity?.startActivityForResult(intent, RequestCodeStore.REQUEST_EXTERNAL_STORAGE)
                        }
                    })
                }
            } else {
                chooseFileFromLocal()
            }
        }
        binding.btnSubmit.setOnClickListener {
            val fileUrl = fileUrl ?: run {
                Logger.d(TAG, "fileUrl is null.")
                return@setOnClickListener
            }
            viewModel.importWords(fileUrl)
        }
        binding.btnSubmit.isEnabled = false
    }

    private fun initObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner, {
            progress?.run {
                if (it) {
                    show()
                } else {
                    hide()
                }
            }
        })
        viewModel.showResult.observe(viewLifecycleOwner, {
            if (it) {
                Toast.makeText(activity, activity?.getString(R.string.loading_complete), Toast.LENGTH_LONG).show()
                val realTimeUpdateEvent = RealTimeUpdateEvent(RealTimeUpdateEvent.Type.UPDATE_WORD_LIST).apply {
                    this.message = "update word list"
                }
                EventBus.getDefault().post(realTimeUpdateEvent)
            } else {
                Toast.makeText(activity, activity?.getString(R.string.loading_failure), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun chooseFileFromLocal() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/vnd.ms-excel"
        }
        val destIntent = Intent.createChooser(intent, "檔案目錄選擇")
        if (activity == null || activity?.isFinishing == true || activity?.isDestroyed == true) {
            return
        }
        startActivityForResult(destIntent, RequestCodeStore.FILE_CHOOSER_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCodeStore.FILE_CHOOSER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Logger.d(TAG, "file chooser")
                val uri = data?.data
                if (uri != null) {
                    fileUrl = uri.toString()
                    binding.tvFilename.text = fileUrl
                    binding.tvFileSize.text = ""
                    binding.btnSubmit.isEnabled = true
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RequestCodeStore.REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                chooseFileFromLocal()
            } else {
                Toast.makeText(activity, "請取得內部檔案讀取權限，否則無法讀取本地端檔案。", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private val TAG = FileChooserFragment::class.java.simpleName
    }
}