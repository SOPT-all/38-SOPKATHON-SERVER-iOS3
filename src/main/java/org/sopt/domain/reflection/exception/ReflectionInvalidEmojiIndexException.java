package org.sopt.domain.reflection.exception;

import org.sopt.global.exception.BaseException;

public class ReflectionInvalidEmojiIndexException extends BaseException {

    public ReflectionInvalidEmojiIndexException() {
        super(ReflectionErrorCode.REFLECTION_INVALID_EMOJI_INDEX);
    }
}
