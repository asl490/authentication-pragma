package com.pragma.bootcamp.model.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_EMAIL_FORMAT("USR_001"),
    SALARY_NEGATIVE("USR_002"),
    SALARY_TOO_HIGH("USR_003"),
    NAME_EMPTY("USR_004"),
    LASTNAME_EMPTY("USR_005"),
    EMAIL_EMPTY("USR_006"),
    SALARY_NULL("USR_007"),
    EMAIL_DUPLICATED("USR_008"),
    INVALID_DOCUMENT("USR_009"),
    INVALID_PHONE("USR_010"),
    BIRTHDATE_NULL("USR_011"),
    BIRTHDATE_IN_FUTURE("USR_012"),
    UNDERAGE("USR_013"),
    ID_NULL("USR_014"),
    USER_NOT_FOUND("USR_015"),
    DOCUMENT_DUPLICATED("USR_016"),
    DATA_INTEGRITY_VIOLATION("USR_017"),
    ROLE_NOT_FOUND("USR_018"),
    AUTHENTICATION_FAILED("USR_019");


    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

}