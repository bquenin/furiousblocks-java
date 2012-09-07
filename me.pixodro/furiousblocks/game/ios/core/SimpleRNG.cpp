//
//  Created by tsug on 4/1/12.
//
// To change the template use AppCode | Preferences | File Templates.
//

#include "SimpleRNG.h"

// The random generator seed can be set three ways:
// 1) specifying two non-zero unsigned integers
// 2) specifying one non-zero unsigned integer and taking a default value for the second
// 3) setting the seed from the system time
SimpleRNG::SimpleRNG(const int u, const int v) {
    m_w = u;
    m_z = v;
}

// This is the heart of the generator.
// It uses George Marsaglia's MWC algorithm to produce an unsigned integer.
// See http://www.bobwheeler.com/statistics/Password/MarsagliaPost.txt
int SimpleRNG::nextInt() {
    m_z = 36969 * (m_z & 65535) + (m_z >> 16);
    m_w = 18000 * (m_w & 65535) + (m_w >> 16);
    return abs((m_z << 16) + m_w);
}

// Produce a uniform random sample from the open interval (0, 1).
// The method will not return either end point.
double SimpleRNG::nextDouble() {
    // 0 <= u < 2^32
    int u = nextInt();
    // The magic number below is 1/(2^32 + 2).
    // The result is strictly between 0 and 1.
    return (u + 1.0) * 2.328306435454494e-10;
}
