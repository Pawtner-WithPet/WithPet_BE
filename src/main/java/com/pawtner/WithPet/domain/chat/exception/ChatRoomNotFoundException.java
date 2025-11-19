package com.pawtner.WithPet.domain.chat.exception;

public class ChatRoomNotFoundException extends RuntimeException{
    public ChatRoomNotFoundException(String message) {
        super(message);
    }
}
