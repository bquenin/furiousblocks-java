package me.pixodro.furiousblocks.core.network.codec;

import me.pixodro.furiousblocks.core.network.codec.messages.FuriousBlocksMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class FuriousBlocksEncoder extends OneToOneEncoder {
  private static final FuriousBlocksEncoder INSTANCE = new FuriousBlocksEncoder();

  private FuriousBlocksEncoder() {
    super();
  }

  public static FuriousBlocksEncoder getInstance() {
    return INSTANCE;
  }

  @Override
  protected Object encode(@SuppressWarnings("unused") final ChannelHandlerContext ctx, @SuppressWarnings("unused") final Channel channel, final Object msg) {
    if (!(msg instanceof FuriousBlocksMessage)) {
      return msg;
    }
    final FuriousBlocksMessage FuriousBlocksMessage = (FuriousBlocksMessage) msg;
    final ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();

    // Encode message type
    buffer.writeByte(FuriousBlocksMessage.getType().getValue());

    // Encode actual message
    buffer.writeBytes(FuriousBlocksMessage.encode());
    return buffer;
  }
}
