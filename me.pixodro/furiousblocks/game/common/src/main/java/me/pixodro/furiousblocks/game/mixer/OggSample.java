package me.pixodro.furiousblocks.game.mixer;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.badlogic.gdx.backends.openal.OggInputStream;
import com.badlogic.gdx.files.FileHandle;

public class OggSample {
  public final ByteBuffer samples;

  public OggSample(FileHandle oggFile) {
    OggInputStream input = new OggInputStream(oggFile.read());
    ByteArrayOutputStream output = new ByteArrayOutputStream(4096);
    byte[] buffer = new byte[2048];
    while (!input.atEnd()) {
      int length = input.read(buffer);
      if (length == -1) {
        break;
      }
      output.write(buffer, 0, length);
    }
    byte[] pcm = output.toByteArray();
    int bytes = pcm.length - (pcm.length % (input.getChannels() > 1 ? 4 : 2));
    samples = ByteBuffer.allocateDirect(bytes);
    samples.order(ByteOrder.nativeOrder());
    samples.put(pcm, 0, bytes);
    samples.flip();
  }
}
