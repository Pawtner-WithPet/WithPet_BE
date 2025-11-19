package com.pawtner.WithPet.domain.chat.exception;

public class ChatRoomOriginNotFoundException extends RuntimeException{
    public ChatRoomOriginNotFoundException(String message) {
        super(message);
    }
}
