package au.com.pjwin.landmarkremark.repo.kumulos

import au.com.pjwin.landmarkremark.model.Note
import au.com.pjwin.landmarkremark.repo.NoteRepository
import com.kumulos.android.Kumulos
import com.kumulos.android.ResponseHandler

open class NoteKumulosRepo : NoteRepository {

    override fun saveNote(description: String, lat: String, lng: String,
                          success: (Int) -> Unit, failure: (Throwable) -> Unit) {
        val params = hashMapOf<String, String>()
        params["description"] = description
        params["userID"] = "1"//todo retrieve from login Pref
        params["lat"] = lat
        params["lng"] = lng

        //todo we really need to create a mapping as string array res for the methods and fields
        Kumulos.call("saveNote", params, object : ResponseHandler() {
            override fun didCompleteWithResult(result: Any?) {
                super.didCompleteWithResult(result)
                success(result as Int)
            }

            override fun didFailWithError(message: String?) {
                super.didFailWithError(message)
                failure(Throwable(message))
            }
        })
    }

    override fun updateNote() {

    }

    override fun getNotesByUser(userId: String, success: (List<Note>) -> Unit, failure: (Throwable) -> Unit) {
        val params = hashMapOf<String, String>()
        params["userId"] = userId

        Kumulos.call("getNotesByUser", params, object : ResponseHandler() {
            override fun didCompleteWithResult(result: Any?) {
                super.didCompleteWithResult(result)
                val rawList = result as List<LinkedHashMap<String, Any>>
                val noteList = mutableListOf<Note>()

                rawList.forEach {
                    noteList.add(Note(it["noteID"].toString(), it["description"].toString(), it["userID"].toString(), it["lat"].toString(), it["lng"].toString()))
                }
                success(noteList)
            }

            override fun didFailWithError(message: String?) {
                super.didFailWithError(message)
                failure(Throwable(message))
            }
        })
    }

    override fun getAllNotesByProximity(lat: String, lng: String, distance: Int,
                                        success: (List<Note>) -> Unit, failure: (Throwable) -> Unit) {
        //accurate findings cannot be done without a real backend, to calculate the radius
        val latFloat = lat.toFloat()
        val lngFloat = lng.toFloat()
        val params = hashMapOf<String, String>()
        params["minLat"] = (latFloat - distance).toString()
        params["maxLat"] = (latFloat + distance).toString()
        params["minLng"] = (lngFloat - distance).toString()
        params["maxLng"] = (lngFloat + distance).toString()

        Kumulos.call("getNotesByProximity", params, object : ResponseHandler() {
            override fun didCompleteWithResult(result: Any?) {
                super.didCompleteWithResult(result)
                //success(result as Int)
            }

            override fun didFailWithError(message: String?) {
                super.didFailWithError(message)
                failure(Throwable(message))
            }
        })
    }
}