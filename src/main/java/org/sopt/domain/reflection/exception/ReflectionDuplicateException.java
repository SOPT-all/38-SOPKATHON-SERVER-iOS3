package org.sopt.domain.reflection.exception;

import org.sopt.global.exception.BaseException;

public class ReflectionDuplicateException extends BaseException {

    public ReflectionDuplicateException() {
        super(ReflectionErrorCode.REFLECTION_DUPLICATE);
    }
}
