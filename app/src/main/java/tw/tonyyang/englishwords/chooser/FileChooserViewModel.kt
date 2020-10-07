package tw.tonyyang.englishwords.chooser

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import tw.tonyyang.englishwords.App
import tw.tonyyang.englishwords.Logger
import tw.tonyyang.englishwords.util.FileChooserUtils
import kotlin.system.measureTimeMillis

class FileChooserViewModel(application: Application) : AndroidViewModel(application) {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _showResult = MutableLiveData<Boolean>()
    val showResult: LiveData<Boolean>
        get() = _showResult

    fun importWords(fileUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val spendTime = measureTimeMillis {
                App.db?.userDao()?.deleteAll()
                try {
                    FileChooserUtils.importExcelDataToDb(getApplication<Application>(), fileUrl)
                    _showResult.value = true
                } catch (exception: Exception) {
                    _showResult.value = false
                }
            }

            Logger.d(TAG, "spendTime: $spendTime ms")
            _isLoading.value = false
        }
    }

    companion object {
        private val TAG = FileChooserViewModel::class.java.simpleName
    }
}