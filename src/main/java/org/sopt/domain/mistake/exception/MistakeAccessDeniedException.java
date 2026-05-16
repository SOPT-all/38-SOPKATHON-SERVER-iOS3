package org.sopt.domain.mistake.exception;

import org.sopt.global.exception.BaseException;

public class MistakeAccessDeniedException extends BaseException {

    public MistakeAccessDeniedException() {
        super(MistakeErrorCode.MISTAKE_ACCESS_DENIED);
    }
}
