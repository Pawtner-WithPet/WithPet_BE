package com.pawtner.WithPet.domain.noseprint.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SearchType {
    LOST("lost"),
    FOUND("found");

    private final String value;

    SearchType(String value){
        this.value = value;
    }

    @JsonCreator
    public static SearchType from(String value){
        for(SearchType type : SearchType.values()){
            if(type.getValue().equals(value)){
                return type;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue(){
        return value;
    }
}
