package me.pixodro.furiousblocks.core.network.codec;

import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import me.pixodro.furiousblocks.core.network.codec.messages.*;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

public class FuriousBlocksDecoder extends OneToOneDecoder {
  private static final FuriousBlocksDecoder INSTANCE = new FuriousBlocksDecoder();

  private FuriousBlocksDecoder() {
    super();
  }

  public static FuriousBlocksDecoder getInstance() {
    return INSTANCE;
  }

  @Override
  protected Object decode(@SuppressWarnings("unused") final ChannelHandlerContext ctx, @SuppressWarnings("unused") final Channel channel, final Object msg) throws FuriousBlocksCodecException {
    if (!(msg instanceof ChannelBuffer)) {
      return msg;
    }

    final ChannelBuffer buffer = (ChannelBuffer) msg;

    // Decode message type
    final FuriousBlocksMessageType messageType = FuriousBlocksMessageType.fromValue(buffer.readByte());

    // Decode message
    switch (messageType) {
      case PLAYER_HELLO:
        final PlayerHello clientHello = new PlayerHello();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), clientHello, RuntimeSchema.getSchema(PlayerHello.class));
        return clientHello;
      case SERVER_HELLO:
        final ServerHello serverHello = new ServerHello();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), serverHello, RuntimeSchema.getSchema(ServerHello.class));
        return serverHello;
      case PING:
        final Ping ping = new Ping();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), ping, RuntimeSchema.getSchema(Ping.class));
        return ping;
      case PONG:
        final Pong pong = new Pong();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), pong, RuntimeSchema.getSchema(Pong.class));
        return pong;
      case PLAYER_JOINED:
        final PlayerJoined playerJoined = new PlayerJoined();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), playerJoined, RuntimeSchema.getSchema(PlayerJoined.class));
        return playerJoined;
      case PLAYER_LEFT:
        final PlayerLeft clientRemoved = new PlayerLeft();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), clientRemoved, RuntimeSchema.getSchema(PlayerLeft.class));
        return clientRemoved;
      case CHAT_MESSAGE:
        final ChatMessage chatMessage = new ChatMessage();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), chatMessage, RuntimeSchema.getSchema(ChatMessage.class));
        return chatMessage;
      case ROOM_CREATIONREQUEST:
        final RoomCreationRequest roomCreationRequest = new RoomCreationRequest();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), roomCreationRequest, RuntimeSchema.getSchema(RoomCreationRequest.class));
        return roomCreationRequest;
      case ROOM_CREATED:
        final RoomCreated roomCreated = new RoomCreated();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), roomCreated, RuntimeSchema.getSchema(RoomCreated.class));
        return roomCreated;
      case ROOM_UPDATED:
        final RoomUpdated roomUpdated = new RoomUpdated();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), roomUpdated, RuntimeSchema.getSchema(RoomUpdated.class));
        return roomUpdated;
      case ROOM_JOINREQUEST:
        final RoomJoinRequest roomJoinRequest = new RoomJoinRequest();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), roomJoinRequest, RuntimeSchema.getSchema(RoomJoinRequest.class));
        return roomJoinRequest;
      case ROOM_LEAVEREQUEST:
        final RoomLeaveRequest roomLeaveRequest = new RoomLeaveRequest();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), roomLeaveRequest, RuntimeSchema.getSchema(RoomLeaveRequest.class));
        return roomLeaveRequest;
      case ROOM_REMOVED:
        final RoomRemoved roomRemoved = new RoomRemoved();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), roomRemoved, RuntimeSchema.getSchema(RoomRemoved.class));
        return roomRemoved;
      case ROOM_UPDATEREQUEST:
        final RoomUpdateRequest roomUpdateRequest = new RoomUpdateRequest();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), roomUpdateRequest, RuntimeSchema.getSchema(RoomUpdateRequest.class));
        return roomUpdateRequest;
      case ROOM_STARTREQUEST:
        final RoomStartRequest roomStartRequest = new RoomStartRequest();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), roomStartRequest, RuntimeSchema.getSchema(RoomStartRequest.class));
        return roomStartRequest;
      case ROOM_GETREADY:
        final RoomGetReady roomGetReady = new RoomGetReady();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), roomGetReady, RuntimeSchema.getSchema(RoomGetReady.class));
        return roomGetReady;
      case ROOM_PLAYERREADY:
        final RoomPlayerReady roomPlayerReady = new RoomPlayerReady();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), roomPlayerReady, RuntimeSchema.getSchema(RoomPlayerReady.class));
        return roomPlayerReady;
      case GAME_START:
        final GameStart gameStart = new GameStart();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), gameStart, RuntimeSchema.getSchema(GameStart.class));
        return gameStart;
      case GAME_DELTAUPDATE:
        final GameDeltaUpdate gameDeltaUpdate = new GameDeltaUpdate();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), gameDeltaUpdate, RuntimeSchema.getSchema(GameDeltaUpdate.class));
        return gameDeltaUpdate;
      case GAME_GARBAGEEVENTLIST:
        final GameGarbageEventList gameGarbageEventList = new GameGarbageEventList();
        ProtostuffIOUtil.mergeFrom(buffer.array(), buffer.arrayOffset() + buffer.readerIndex(), buffer.readableBytes(), gameGarbageEventList, RuntimeSchema.getSchema(GameGarbageEventList.class));
        return gameGarbageEventList;
    }
    throw new IllegalStateException("Unsupported message type: " + messageType);
  }
}
