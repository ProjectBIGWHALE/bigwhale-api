package com.whale.web.documents.certificategenerator.model.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum CertificateBasicInfosEnum {
    EVENT_NAME("basicInfoEventName"), EVENT_NAME_BODY("basicInfoEventNameTwo"), SPEAKER_NAME("basicInfoSpeakerName"),
    SPEAKER_ROLE("basicInfoSpeakerRole"), EVENT_DATE("basicInfoEventDate"), WORKLOAD("basicInfoEventWorkload"),
    EVENT_LOCALE("basicInfoEventLocale"), EVENT_DATE_BODY("basicInfoEventDateTwo");

    private final String tagId;

    CertificateBasicInfosEnum(String tagId) {
        this.tagId = tagId;
    }
}
