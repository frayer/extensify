package org.extensify.transform.xalan.extensions.groovy;

public class InvalidClosureParametersException extends RuntimeException {

    public InvalidClosureParametersException() {
        super();
    }

    public InvalidClosureParametersException(String message) {
        super(message);
    }

    public InvalidClosureParametersException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public InvalidClosureParametersException(Throwable throwable) {
        super(throwable);
    }

}
