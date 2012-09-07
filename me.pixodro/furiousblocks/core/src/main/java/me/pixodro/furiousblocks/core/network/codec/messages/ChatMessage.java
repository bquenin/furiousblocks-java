package me.pixodro.furiousblocks.core.network.codec.messages;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class ChatMessage extends FuriousBlocksMessage {
  public String senderName;
  public int roomId;
  public String message;

  public ChatMessage() {
    super(FuriousBlocksMessageType.CHAT_MESSAGE);
  }

  public ChatMessage(final int roomId, final String message) {
    super(FuriousBlocksMessageType.CHAT_MESSAGE);
    this.roomId = roomId;
    this.message = message;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(ChatMessage.class), LinkedBuffer.allocate(bufferLength));
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("ChatMessage [senderName=");
    builder.append(senderName);
    builder.append(", roomId=");
    builder.append(roomId);
    builder.append(", message=");
    builder.append(message);
    builder.append("]");
    return builder.toString();
  }
}
