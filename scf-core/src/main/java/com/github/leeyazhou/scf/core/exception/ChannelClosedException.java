package com.github.leeyazhou.scf.core.exception;

public class ChannelClosedException extends SCFException {
  private static final long serialVersionUID = 1L;

  public ChannelClosedException() {
    this("Channel连接已经断开了!");
  }

  public ChannelClosedException(String message) {
    super(message);
  }
}
