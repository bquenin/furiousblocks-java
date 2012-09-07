package me.pixodro.furiousblocks.core.tools;

/**
 * Created with IntelliJ IDEA.
 * User: tsug
 * Date: 4/10/12
 * Time: 10:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleRNG {
  private int m_w = 521288629;
  private int m_z = 362436069;

  // The random generator seed can be set three ways:
  // 1) specifying two non-zero unsigned integers
  // 2) specifying one non-zero unsigned integer and taking a default value for the second
  // 3) setting the seed from the system time
  public SimpleRNG(final int u) {
    m_w = u;
  }

  //  public SimpleRNG(final int u, final int v) {
  //    m_w = u;
  //    m_z = v;
  //  }

  // This is the heart of the generator.
  // It uses George Marsaglia's MWC algorithm to produce an unsigned integer.
  // See http://www.bobwheeler.com/statistics/Password/MarsagliaPost.txt
  public int nextInt() {
    m_z = 36969 * (m_z & 65535) + (m_z >> 16);
    m_w = 18000 * (m_w & 65535) + (m_w >> 16);
    return Math.abs((m_z << 16) + m_w);
  }

  // Produce a uniform random sample from the open interval (0, 1).
  // The method will not return either end point.
  public double nextDouble() {
    // 0 <= u < 2^32
    int u = nextInt();
    // The magic number below is 1/(2^32 + 2).
    // The result is strictly between 0 and 1.
    return (u + 1.0) * 2.328306435454494e-10;
  }

}
