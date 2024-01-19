package com.whale.web.documents.certificategenerator.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
public enum PersonBasicInfosEnum {
    PERSON_NAME("personName"), PERSON_NAME_BODY("personNameTwo");

    private final String tagId;

    PersonBasicInfosEnum(String tagId) {
        this.tagId = tagId;
    }
}
