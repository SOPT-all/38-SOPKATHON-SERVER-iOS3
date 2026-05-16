package org.sopt.global.storage.exception;

import org.sopt.global.exception.BaseException;

public class UnsupportedContentTypeException extends BaseException {

    public UnsupportedContentTypeException() {
        super(StorageErrorCode.UNSUPPORTED_CONTENT_TYPE);
    }
}
