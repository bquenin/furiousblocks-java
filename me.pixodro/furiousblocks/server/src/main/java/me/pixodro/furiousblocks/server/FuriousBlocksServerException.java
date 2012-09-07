package me.pixodro.furiousblocks.server;

public class FuriousBlocksServerException extends Exception {
  private static final long serialVersionUID = -6929500465776294313L;

  public FuriousBlocksServerException() {
  }

  public FuriousBlocksServerException(final String message) {
    super(message);
  }

  public FuriousBlocksServerException(final Throwable cause) {
    super(cause);
  }

  public FuriousBlocksServerException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
