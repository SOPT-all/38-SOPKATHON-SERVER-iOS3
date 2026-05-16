package org.sopt.global.storage.exception;

import org.sopt.global.exception.BaseException;

public class FileSizeExceededException extends BaseException {

    public FileSizeExceededException() {
        super(StorageErrorCode.FILE_SIZE_EXCEEDED);
    }
}
