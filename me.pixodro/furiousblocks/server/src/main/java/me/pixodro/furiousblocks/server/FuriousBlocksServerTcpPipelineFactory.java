package me.pixodro.furiousblocks.server;

import java.io.IOException;

import me.pixodro.furiousblocks.core.network.codec.FuriousBlocksDecoder;
import me.pixodro.furiousblocks.core.network.codec.FuriousBlocksEncoder;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import static org.jboss.netty.channel.Channels.pipeline;

class FuriousBlocksServerTcpPipelineFactory implements ChannelPipelineFactory {

  @Override
  public ChannelPipeline getPipeline() throws IOException {
    final ChannelPipeline p = pipeline();
    p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
    p.addLast("FuriousBlocksDecoder", FuriousBlocksDecoder.getInstance());

    p.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
    p.addLast("FuriousBlocksEncoder", FuriousBlocksEncoder.getInstance());

    p.addLast("FuriousBlocksServerTcpHandler", FuriousBlocksServerTcpHandler.getInstance());
    return p;
  }
}
