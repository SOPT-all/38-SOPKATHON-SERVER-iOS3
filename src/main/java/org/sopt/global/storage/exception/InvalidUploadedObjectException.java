package org.sopt.global.storage.exception;

import org.sopt.global.exception.BaseException;

public class InvalidUploadedObjectException extends BaseException {

    public InvalidUploadedObjectException() {
        super(StorageErrorCode.INVALID_UPLOADED_OBJECT);
    }
}
