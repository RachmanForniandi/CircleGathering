package rachman.forniandi.circlegathering.actuator

import okhttp3.RequestBody

interface UploadInputImpl {

    suspend fun doUploadData(desc:String,Img:RequestBody)
}