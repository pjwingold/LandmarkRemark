package au.com.pjwin.landmarkremark.viewmodel

import au.com.pjwin.commonlib.viewmodel.mbaas.MBaasViewModel
import au.com.pjwin.landmarkremark.model.Note
import au.com.pjwin.landmarkremark.model.NoteResponse
import au.com.pjwin.landmarkremark.repo.kumulos.NoteKumulosRepo
import com.google.android.gms.maps.model.LatLng

open class NoteViewModel : MBaasViewModel<NoteResponse>() {

    //todo DI to setup repo
    private val noteRepo by lazy {
        NoteKumulosRepo()
    }

    fun saveNote(description: String, latLng: LatLng) {
        val lat = latLng.latitude.toString()
        val lng = latLng.longitude.toString()

        noteRepo.saveNote(description, lat, lng,
                { newId ->
                    onData(NoteResponse().apply {
                        this.newId = newId
                        noteList = mutableListOf(Note(newId.toString(), description, "1", lat, lng))
                    })
                },
                this::onError)
    }

    fun updateNote() {

    }

    fun getNotesByUser() {
        noteRepo.getNotesByUser("1",
                {
                    onData(NoteResponse().apply {
                        noteList = it
                    })
                },
                this::onError)
    }

    fun getAllNotesByProximity(latLng: LatLng) {
        noteRepo.getAllNotesByProximity(latLng.latitude.toString(), latLng.longitude.toString(), 100,
                { },
                this::onError)
    }
}