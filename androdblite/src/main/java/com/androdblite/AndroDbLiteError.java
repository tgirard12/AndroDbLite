package com.androdblite;

/**
 * Created by tgirard on 30/06/15
 */
public class AndroDbLiteError extends Error {

    public AndroDbLiteError(String detailMessage) {
        super(detailMessage);
    }

    public AndroDbLiteError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
