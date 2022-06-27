package com.ggj.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author guogj
 * @date 2022/06/27
 */
public class GeneralRuntimeException extends RuntimeException {

  private Throwable nested = null;

  /**
   * Creates new <code>GeneralException</code> without detail message.
   */
  public GeneralRuntimeException() {
    super();
  }

  /**
   * Constructs an <code>GeneralException</code> with the specified detail message.
   *
   * @param msg the detail message.
   */
  public GeneralRuntimeException(String msg) {
    super(msg);
  }

  /**
   * Constructs an <code>GeneralException</code> with a nested Exception.
   *
   * @param nested the nested exception.
   */
  public GeneralRuntimeException(Throwable nested) {
    super();
    this.nested = nested;
  }

  /**
   * Constructs an <code>GeneralException</code> with the specified detail message and nested Exception.
   *
   * @param msg the detail message.
   */
  public GeneralRuntimeException(String msg, Throwable nested) {
    super(msg);
    this.nested = nested;
  }

  /**
   * Returns the detail message, including the message from the nested exception if there is one.
   */
  @Override
  public String getMessage() {
    if (nested != null) {
      return super.getMessage() + " (" + nested.getMessage() + ")";
    } else {
      return super.getMessage();
    }
  }

  /**
   * Returns the detail message, NOT including the message from the nested exception.
   */
  public String getNonNestedMessage() {
    return super.getMessage();
  }

  /**
   * Returns the nested exception if there is one, null if there is not.
   */
  public Throwable getNested() {
    return nested;
  }

  /**
   * Prints the composite message to System.err.
   */
  @Override
  public void printStackTrace() {
    super.printStackTrace();
    if (nested != null) {
      nested.printStackTrace();
    }
  }

  /**
   * Prints the composite message and the embedded stack trace to the specified stream ps.
   */
  @Override
  public void printStackTrace(PrintStream ps) {
    super.printStackTrace(ps);
    if (nested != null) {
      nested.printStackTrace(ps);
    }
  }

  /**
   * Prints the composite message and the embedded stack trace to the specified print writer pw.
   */
  @Override
  public void printStackTrace(PrintWriter pw) {
    super.printStackTrace(pw);
    if (nested != null) {
      nested.printStackTrace(pw);
    }
  }
}
