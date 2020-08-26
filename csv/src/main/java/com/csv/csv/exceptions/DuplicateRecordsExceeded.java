package com.csv.csv.exceptions;

public class DuplicateRecordsExceeded extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DuplicateRecordsExceeded(String cause) {
        super(cause);
    }
}
