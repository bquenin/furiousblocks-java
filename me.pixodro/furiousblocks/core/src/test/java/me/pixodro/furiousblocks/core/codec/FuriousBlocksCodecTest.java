package me.pixodro.furiousblocks.core.codec;

import java.util.Random;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class FuriousBlocksCodecTest {
  private final Random random = new Random(System.nanoTime());

  @Ignore
  @Test
  public void arrayCodec() {
    class TestMessage {
      public String[][] twoDimensionsArray = new String[32][32];

      public TestMessage() {
        super();
      }

      public TestMessage(final String[][] TwoDimensionsArray) {
        twoDimensionsArray = TwoDimensionsArray;
      }
    }

    // Fill array randomly
    final String[][] testArray = new String[16][32];
    for (int x = 0; x < testArray.length; x++) {
      for (int y = 0; y < testArray[0].length; y++) {
        testArray[x][y] = ((random.nextInt() % 10) != 0) ? Integer.toString(random.nextInt()) : null;
      }
    }

    // Create test message
    final TestMessage original = new TestMessage(testArray);

    // Encode
    final byte[] binary = ProtostuffIOUtil.toByteArray(original, RuntimeSchema.getSchema(TestMessage.class), LinkedBuffer.allocate(8192));

    // Decode
    final TestMessage duplicata = new TestMessage();
    ProtostuffIOUtil.mergeFrom(binary, 0, binary.length, duplicata, RuntimeSchema.getSchema(TestMessage.class));

    // Test
    Assert.assertEquals(original.twoDimensionsArray.length, duplicata.twoDimensionsArray.length);
    Assert.assertEquals(original.twoDimensionsArray[0].length, duplicata.twoDimensionsArray[0].length);

    for (int x = 0; x < testArray.length; x++) {
      for (int y = 0; y < testArray[0].length; y++) {
        if (original.twoDimensionsArray[x][y] == null) {
          Assert.assertNull("Cell [" + x + "][" + y + "] should be null", duplicata.twoDimensionsArray[x][y]);
        } else {
          Assert.assertEquals(original.twoDimensionsArray[x][y], duplicata.twoDimensionsArray[x][y]);
        }
      }
    }
  }
}
