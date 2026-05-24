package com.devprofileproject.devprofileaast.exception;

//class bygeeb el error bzbt mn github
public class GitHubApiException extends RuntimeException {

    private final GitHubErrorType type;

    public GitHubApiException(String message, GitHubErrorType type) {
        super(message);
        this.type = type;
    }

    
    public GitHubApiException(String message, GitHubErrorType type, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

//3lshan ngeb sabb el error
    public GitHubErrorType getType() {
        return type;
    }
}
