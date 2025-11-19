package com.pawtner.WithPet.domain.chat.exception;

public class ChatRoomAccessDeniedException extends RuntimeException{

    public ChatRoomAccessDeniedException(String message) {
        super(message);
    }

}
