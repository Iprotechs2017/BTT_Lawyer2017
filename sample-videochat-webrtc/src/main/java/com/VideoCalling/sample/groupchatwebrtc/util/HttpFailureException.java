package com.VideoCalling.sample.groupchatwebrtc.util;

/**
 * Created by Harsha on 14/12/2015.
 */
public class HttpFailureException extends Exception {

    /**
     * Constructs a new {@code Exception} that includes the current stack trace.
     */
    public HttpFailureException() {
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace and the
     * specified detail message.
     *
     * @param detailMessage the detail message for this exception.
     */
    public HttpFailureException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace, the
     * specified detail message and the specified cause.
     *
     * @param detailMessage the detail message for this exception.
     * @param throwable
     */
    public HttpFailureException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace and the
     * specified cause.
     *
     * @param throwable the cause of this exception.
     */
    public HttpFailureException(Throwable throwable) {
        super(throwable);
    }


}
