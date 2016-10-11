package com.chaboshi.scf.server.core.communication.telnet;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;

/**
 * netty Pipeline Factory
 * 
 * @author Service Platform Architecture Team (spat@58.com)
 * 
 *         <a href="http://blog.58.com/spat/">blog</a> <a href="http://www.58.com">website</a>
 * 
 */
public class TelnetPipelineFactory implements ChannelPipelineFactory {

  private final ChannelHandler handler;
  private int frameMaxLength;

  public TelnetPipelineFactory(ChannelHandler handler, int frameMaxLength) {
    this.handler = handler;
    this.frameMaxLength = frameMaxLength;
  }

  public ChannelPipeline getPipeline() throws Exception {
    ChannelPipeline pipeline = pipeline();
    pipeline.addLast("framer", new DelimiterBasedFrameDecoder(frameMaxLength, Delimiters.lineDelimiter()));
    pipeline.addLast("handler", handler);
    return pipeline;
  }
}