package com.activiti.oa.exceptions;

import java.util.Map;

public class ApplicationException extends Exception {
    private Map<String, Object> data;

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(Throwable cause) {
        super(cause.getMessage(), cause);

        this.setStackTrace(cause.getStackTrace());
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
