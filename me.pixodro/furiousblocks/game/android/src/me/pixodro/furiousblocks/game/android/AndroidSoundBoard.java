package me.pixodro.furiousblocks.game.android;

import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import me.pixodro.furiousblocks.game.mixer.OggSample;
import me.pixodro.furiousblocks.game.mixer.SoundBoard;

public class AndroidSoundBoard implements SoundBoard {
  private List<SoundboardSample> soundBoardSamples = new ArrayList<SoundboardSample>();

  @Override
  public void addSample(final OggSample oggSample) {
    SoundboardSample soundboardSample = new SoundboardSample(oggSample);
    soundBoardSamples.add(soundboardSample);
    Thread thread = new Thread(soundboardSample);
    thread.setPriority(Thread.MAX_PRIORITY);
    thread.start();
  }

  @Override
  public void play() {
    for (SoundboardSample soundboardSample : soundBoardSamples) {
      soundboardSample.play();
    }
  }

  //  public void pause() {
  //    if (audioTrack == null) {
  //      return;
  //    }
  //
  //    audioTrack.pause();
  //  }
  //
  //  public void resume() {
  //    if (audioTrack == null) {
  //      return;
  //    }
  //
  //    audioTrack.play();
  //  }

  @Override
  public void dispose() {
    for (SoundboardSample soundboardSample : soundBoardSamples) {
      soundboardSample.release();
    }
  }

  class AndroidAudioTrack extends AudioTrack {
    private final int frameSize;

    public AndroidAudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes, int mode) throws IllegalArgumentException {
      super(streamType, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes, mode);

      if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
        frameSize = 2 * this.getChannelCount();
      } else {
        frameSize = this.getChannelCount();
      }
    }

    @Override
    public void play() throws IllegalStateException {
      super.play();
      // Audio starts playing after the buffer has been filled once.
      // Manually fill the buffer at start */
      initBuffer();
    }

    public void initBuffer() {
      byte[] audioData = new byte[getNativeFrameCount() * frameSize];
      write(audioData, 0, audioData.length);
    }
  }

  class SoundboardSample implements Runnable, AudioTrack.OnPlaybackPositionUpdateListener {
    private int numChannel = 1;
    private int sampleFreq = 22050; //AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC);
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int bufferSize = numChannel * AudioTrack.getMinBufferSize(sampleFreq, channelConfig, audioFormat);
    private short[] audioBuffer = new short[bufferSize / 16];
    private ShortBuffer sampleBuffer;
    private AndroidAudioTrack audioTrack;
    private final OggSample oggSample;
    private volatile boolean playing;

    public SoundboardSample(final OggSample oggSample) {
      this.oggSample = oggSample;
      // convert to 16 bit pcm sound array
      // assumes the sample buffer is normalised.

      sampleBuffer = oggSample.samples.asShortBuffer();
      //            for (int i = 0; i < oggSample.samples.asShortBuffer().capacity(); i++) {
      //              sampleBuffer[i] =oggSample.samples.asShortBuffer().get();
      //      scale to maximum amplitude
      //              final short val = (short) ((dVal * 32767));
      //      in 16 bit wav PCM, first byte is the low order byte
      //              audioBuffer[idx++] = (short) ((dVal * 32767)); //(short) ((short) (val & 0x00ff) + ((short) (val & 0xff00) >>> 8));
      //            }
    }

    @Override
    public void run() {
      if (audioTrack != null) {
        return;
      }

      audioTrack = new AndroidAudioTrack(AudioManager.STREAM_MUSIC, sampleFreq, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM);
      audioTrack.play();
      audioTrack.setPositionNotificationPeriod(bufferSize / 16); // In frames
      audioTrack.setPlaybackPositionUpdateListener(this);
    }

    int j = 0;
    boolean latch = false;
    int position = 0;

    @Override
    public void onPeriodicNotification(final AudioTrack audioTrack) {
      if (!playing) {
        for (int i = 0; i < audioBuffer.length; i++) {
          audioBuffer[i] = 0;
        }
      } else {
        if (sampleBuffer.capacity() - sampleBuffer.position() < audioBuffer.length) {
          for (int i = 0; i < audioBuffer.length; i++) {
            audioBuffer[i] = 0;
          }
          sampleBuffer.get(audioBuffer, 0, sampleBuffer.capacity() - sampleBuffer.position());
          sampleBuffer.rewind();
          playing = false;
        } else {
          sampleBuffer.get(audioBuffer);
        }
      }


      //      final int sampleRate = 22050;
      //      //        final int numSamples = (int) duration * sampleRate + 100;
      //      final double sample[] = new double[bufferSize / 8];
      //      double freqOfTone = latch ? 880 : 440; // hz
      //
      //      // fill out the array
      //      for (int i = 0; i < sample.length; ++i, j++) {
      //        sample[i] = Math.sin(2 * Math.PI * j / (sampleRate / freqOfTone));
      //      }
      //      latch = !latch;

      int written = audioTrack.write(audioBuffer, 0, audioBuffer.length);

    }

    @Override
    public void onMarkerReached(final AudioTrack audioTrack) {
      //To change body of implemented methods use File | Settings | File Templates.
    }

    public void release() {
      audioTrack.release();
    }

    public void play() {
      sampleBuffer.rewind();
      if (!playing) {
        playing = true;
      } else {
      }
    }
  }
}
