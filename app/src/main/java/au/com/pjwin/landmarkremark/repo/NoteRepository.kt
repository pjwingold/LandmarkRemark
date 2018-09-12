package au.com.pjwin.landmarkremark.repo

import au.com.pjwin.landmarkremark.model.Note

interface NoteRepository {

    fun saveNote(description: String, lat: String, lng: String,
                 success: (Int) -> Unit, failure: (Throwable) -> Unit)

    fun updateNote() {

    }

    //get all user notes
    fun getNotesByUser(userId: String, success: (List<Note>) -> Unit, failure: (Throwable) -> Unit) {

    }

    //search note
    fun getNoteByDescription() {

    }

    /**
     * distance: in km
     */
    fun getAllNotesByProximity(lat: String, lng: String, distance: Int,
                               success: (List<Note>) -> Unit, failure: (Throwable) -> Unit) {

    }
}