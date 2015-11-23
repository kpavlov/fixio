/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sample;

import fixio.netty.codec.FixMessageDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.mock;

public class DecoderBenchmark {

    public static final String MESSAGE = "8=FIX.4.29=39635=BZ34=148949=CME50=G52=20141210-04:12:58.68956=17ACPON57=DUMMY369=36701180=0K41181=42811350=428011=ACP141818477867860=20141210-04:12:58.686533=3797=Y893=Y1028=Y1300=991369=9971:21373=31374=91375=1453=2448=000447=D452=7448=US,IL447=D452=54534=341=ACP141818477617384=60535=99499752041=ACP141818477621484=60535=99499752141=ACP141818477625384=180535=99499752210=228";
    private static ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);

    @State(Scope.Thread)
    public static class ThreadState {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(MESSAGE.getBytes(StandardCharsets.US_ASCII));
        FixMessageDecoder decoder;

        @Setup(Level.Invocation)
        public void setup() {
            decoder = new FixMessageDecoder();
            byteBuf.resetReaderIndex();
            byteBuf.retain();
        }
    }

    @Benchmark
    public void testDecodeMessage(ThreadState state) throws Exception {
        state.decoder.channelRead(ctx, state.byteBuf);
    }

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(DecoderBenchmark.class.getSimpleName())
//                .warmupIterations(500)
//                .measurementIterations(50)
                .threads(4)
                .forks(1)
                .build();

        new Runner(opt).run();

    }

}
