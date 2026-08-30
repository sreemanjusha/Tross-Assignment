package com.tross.linkedinapi.exception;

public final class LinkedInExceptions {
    private LinkedInExceptions() {}

    public static class AuthException extends RuntimeException {
        public AuthException(String message) { super(message); }
    }

    public static class RateLimitException extends RuntimeException {
        public RateLimitException(String message) { super(message); }
    }

    public static class UpstreamException extends RuntimeException {
        public UpstreamException(String message) { super(message); }
        public UpstreamException(String message, Throwable cause) { super(message, cause); }
    }
}
