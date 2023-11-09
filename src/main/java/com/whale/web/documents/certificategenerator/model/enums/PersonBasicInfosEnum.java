package com.whale.web.documents.certificategenerator.model.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum PersonBasicInfosEnum {
    PERSON_NAME("personName"), PERSON_NAME_BODY("personNameTwo");

    private final String tagId;

    PersonBasicInfosEnum(String tagId) {
        this.tagId = tagId;
    }
}
