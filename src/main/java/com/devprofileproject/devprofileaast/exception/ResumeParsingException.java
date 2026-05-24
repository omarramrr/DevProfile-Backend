package com.devprofileproject.devprofileaast.exception;

//class fe qra't el pdf lw hasal ay moshkela hayrmy elexeptions de
public class ResumeParsingException extends RuntimeException{
    public ResumeParsingException(String message){
        super(message);
    }

    public ResumeParsingException(String message,Throwable cause){
        super(message,cause);
    }
}
