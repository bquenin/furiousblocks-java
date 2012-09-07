package me.pixodro.furiousblocks.core.network.codec.messages;

abstract public class FuriousBlocksMessage {
  static final int bufferLength = 1024;
  private final FuriousBlocksMessageType type;

  FuriousBlocksMessage(final FuriousBlocksMessageType type) {
    this.type = type;
  }

  public FuriousBlocksMessageType getType() {
    return type;
  }

  abstract public byte[] encode();
}
