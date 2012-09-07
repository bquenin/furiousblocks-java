package me.pixodro.furiousblocks.core.network.codec;

public class FuriousBlocksCodecException extends Exception {
  private static final long serialVersionUID = 516503294058904512L;

  public FuriousBlocksCodecException() {
  }

  public FuriousBlocksCodecException(final String message) {
    super(message);
  }

  public FuriousBlocksCodecException(final Throwable cause) {
    super(cause);
  }

  public FuriousBlocksCodecException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
