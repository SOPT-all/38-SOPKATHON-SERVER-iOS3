package org.sopt.domain.mistake.exception;

import org.sopt.global.exception.BaseException;

public class MistakeNotFoundException extends BaseException {

    public MistakeNotFoundException() {
        super(MistakeErrorCode.MISTAKE_NOT_FOUND);
    }
}
