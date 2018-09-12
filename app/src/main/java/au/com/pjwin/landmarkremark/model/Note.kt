package au.com.pjwin.landmarkremark.model

data class Note(var description: String, var userId: String, var lat: String, var lng: String) {

    var id: String? = null

    constructor(id: String, description: String, userId: String, lat: String, lng: String) :
            this(description, userId, lat, lng) {
        this.id = id
    }
}