package hu.itatti.saunamaster.data

data class Users(
    var uid: String = "",
    var name: String,
    var email: String,
    var guest: Boolean,
    var master: Boolean
)
