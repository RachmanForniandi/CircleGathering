package rachman.forniandi.circlegathering.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import rachman.forniandi.circlegathering.repositories.MainRepository
import rachman.forniandi.circlegathering.utils.DataStoreRepository
import javax.inject.Inject

@HiltViewModel
class DetailStoryViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dataStoreRepository: DataStoreRepository,
    application: Application
): AndroidViewModel(application) {


}