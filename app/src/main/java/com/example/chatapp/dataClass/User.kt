package com.example.chatapp.dataClass

class User{
    var name:String ?= null
    var imageUrl :String ?= null
    var id:String ?= null

    constructor(){}

    constructor(name:String,imageUrl: String,id: String){
        this.name = name
        this.imageUrl = imageUrl
        this.id = id
    }
}

/*
// not compatible with firebase
data class User(
    val name:String,
    val imageUrl :String,
    val id:String
)
*/