package org.sopt.domain.mistake.exception;

import org.sopt.global.exception.BaseException;

public class MistakeDuplicateException extends BaseException {

    public MistakeDuplicateException() {
        super(MistakeErrorCode.MISTAKE_DUPLICATE);
    }
}
