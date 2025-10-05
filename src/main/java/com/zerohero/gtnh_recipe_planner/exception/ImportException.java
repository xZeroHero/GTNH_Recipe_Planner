package com.zerohero.gtnh_recipe_planner.exception;

public class ImportException extends RuntimeException {
  public ImportException(String message) {
    super(message);
  }

  public ImportException(String message, Throwable cause) {
    super(message, cause);
  }
}