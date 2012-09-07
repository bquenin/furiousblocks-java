package me.pixodro.furiousblocks.core.network.codec.messages;

import me.pixodro.furiousblocks.core.network.codec.FuriousBlocksCodecException;

public enum FuriousBlocksMessageType {
  PLAYER_HELLO((byte) 1), //
  SERVER_HELLO((byte) 2), //
  PING((byte) 3), //
  PONG((byte) 4), //
  PLAYER_JOINED((byte) 5), //
  PLAYER_LEFT((byte) 6), //
  CHAT_MESSAGE((byte) 7), //
  ROOM_CREATIONREQUEST((byte) 8), //
  ROOM_CREATED((byte) 9), //
  ROOM_UPDATED((byte) 10), //
  ROOM_JOINREQUEST((byte) 11), //
  ROOM_LEAVEREQUEST((byte) 12), //
  ROOM_REMOVED((byte) 13), //
  ROOM_UPDATEREQUEST((byte) 14), //
  ROOM_STARTREQUEST((byte) 15), //
  ROOM_GETREADY((byte) 16), //
  ROOM_PLAYERREADY((byte) 17), //
  GAME_START((byte) 18), //
  GAME_DELTAUPDATE((byte) 19), //
  GAME_GARBAGEEVENTLIST((byte) 20), //
  ;

  private final byte value;

  private FuriousBlocksMessageType(final byte value) {
    this.value = value;
  }

  public byte getValue() {
    return value;
  }

  public static FuriousBlocksMessageType fromValue(final byte value) throws FuriousBlocksCodecException {
    for (final FuriousBlocksMessageType FuriousBlocksMessageType : values()) {
      if (FuriousBlocksMessageType.value == value) {
        return FuriousBlocksMessageType;
      }
    }
    throw new FuriousBlocksCodecException("unsupported message type " + value);
  }
}
