package me.pixodro.furiousblocks.client;

public class FuriousBlocksClientException extends Exception {
  private static final long serialVersionUID = 5483981805797292108L;

  public FuriousBlocksClientException() {
  }

  public FuriousBlocksClientException(final String message) {
    super(message);
  }

  public FuriousBlocksClientException(final Throwable cause) {
    super(cause);
  }

  public FuriousBlocksClientException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
