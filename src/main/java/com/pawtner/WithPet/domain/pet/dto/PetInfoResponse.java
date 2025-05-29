package com.pawtner.WithPet.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.*;


@JsonRootName(value = "response")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PetInfoResponse {
    private Header header;
    private Body body;

    @Data
    public static class Header {
        private String reqNo;
        private String resultCode;
        private String resultMsg;
        private String errorMsg;
    }

    @Data
    public static class Body {
        private Item item;

        @Data
        public static class Item {
            private String officeTel;
            private String aprGbNm;
            private String dogRegNo;
            private String rfidCd;
            private String rfidGubun;
            private String birthDt;
            private String dogNm;
            private String sexNm;
            private String kindNm;
            private String neuterYn;
            private String orgNm;
        }
    }
}