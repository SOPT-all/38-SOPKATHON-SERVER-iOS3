package org.sopt.global.storage.exception;

import org.sopt.global.exception.BaseException;

public class ObjectStorageRequestFailedException extends BaseException {

    public ObjectStorageRequestFailedException() {
        super(StorageErrorCode.OBJECT_STORAGE_REQUEST_FAILED);
    }
}
