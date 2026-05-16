package org.sopt.global.storage.exception;

import org.sopt.global.exception.BaseException;

public class InvalidObjectKeyException extends BaseException {

    public InvalidObjectKeyException() {
        super(StorageErrorCode.INVALID_OBJECT_KEY);
    }
}
