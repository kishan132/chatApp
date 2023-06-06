package com.example.chatapp.dataClass

class Message{

    var senderID :String ?= null
    var msg : String ?= null

    constructor(){}

    constructor(senderID : String, msg : String){
        this.senderID = senderID
        this.msg = msg
    }

}