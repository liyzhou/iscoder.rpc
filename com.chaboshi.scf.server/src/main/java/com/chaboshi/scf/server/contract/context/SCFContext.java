/*
 * Copyright 2010 58.com, Inc.
 */
package com.chaboshi.scf.server.contract.context;

import com.chaboshi.scf.protocol.sdp.RequestProtocol;
import com.chaboshi.scf.server.contract.http.HttpContext;
import com.chaboshi.scf.server.core.ServerHandler;

/**
 * SCF request/response context
 * 
 * @author Service Platform Architecture Team (spat@58.com)
 * 
 *         <a href="http://blog.58.com/spat/">blog</a> <a href="http://www.58.com">website</a>
 * 
 */
public class SCFContext {

  private boolean monitor;

  private StopWatch stopWatch = new StopWatch();

  private SCFRequest scfRequest = new SCFRequest();

  private SCFResponse scfResponse;

  private ServerType serverType;

  private Throwable error;

  private SCFChannel channel;

  private ServerHandler serverHandler;

  private boolean isDoInvoke = true;

  private boolean isAsyn = false;

  private boolean isDel = false;

  private int sessionID;

  private HttpContext httpContext;// http 上下文内容

  private ExecFilterType execFilter = ExecFilterType.All;

  public SCFContext() {

  }

  public SCFContext(SCFChannel channel) {
    this.setChannel(channel);
  }

  public SCFContext(byte[] requestBuffer, SCFChannel channel, ServerType serverType, ServerHandler handler) throws Exception {

    this.scfRequest.setRequestBuffer(requestBuffer);
    this.setChannel(channel);
    this.setServerType(serverType);
    this.setServerHandler(handler);
  }

  /**
   * 从ThreadLocal里获取SCFContext
   * 
   * @return
   */
  public static SCFContext getFromThreadLocal() {
    return Global.getSingleton().getThreadLocal().get();
  }

  public static void setThreadLocal(SCFContext context) {
    Global.getSingleton().getThreadLocal().set(context);
  }

  public static void removeThreadLocal() {
    Global.getSingleton().getThreadLocal().remove();
  }

  /**
   * 取出protocol中的sessionID
   */
  public static int getThreadLocalID() {
    SCFContext context = Global.getSingleton().getThreadLocal().get();
    Global.getSingleton().getThreadLocal().remove();
    return context.getSessionID();
  }

  // public static int getThreadLocalID () {
  // int sessionID = 0;
  // SCFContext context = Global.getSingleton().getThreadLocal().get();
  // sessionID = context.getScfRequest().getProtocol().getSessionID();
  // Global.getSingleton().getThreadLocal().remove();
  // return sessionID;
  // }

  public boolean isMonitor() {
    return monitor;
  }

  public void setMonitor(boolean monitor) {
    this.monitor = monitor;
  }

  public SCFRequest getScfRequest() {
    return scfRequest;
  }

  public void setScfRequest(SCFRequest scfRequest) {
    this.scfRequest = scfRequest;

    RequestProtocol r = (RequestProtocol) scfRequest.getProtocol().getSdpEntity();
    this.stopWatch.setLookup(r.getLookup());
    this.stopWatch.setMethodName(r.getMethodName());
  }

  public SCFResponse getScfResponse() {
    return scfResponse;
  }

  public void setScfResponse(SCFResponse scfResponse) {
    this.scfResponse = scfResponse;
  }

  public StopWatch getStopWatch() {
    return stopWatch;
  }

  public void setDoInvoke(boolean isDoInvoke) {
    this.isDoInvoke = isDoInvoke;
  }

  public boolean isDoInvoke() {
    return isDoInvoke;
  }

  public void setError(Throwable error) {
    this.error = error;
  }

  public Throwable getError() {
    return error;
  }

  public void setServerType(ServerType requestType) {
    this.serverType = requestType;
  }

  public ServerType getServerType() {
    return serverType;
  }

  public void setServerHandler(ServerHandler responseHandler) {
    this.serverHandler = responseHandler;
  }

  public ServerHandler getServerHandler() {
    return serverHandler;
  }

  public void setChannel(SCFChannel channel) {
    this.channel = channel;
  }

  public SCFChannel getChannel() {
    return channel;
  }

  public void setExecFilter(ExecFilterType execFilter) {
    this.execFilter = execFilter;
  }

  public ExecFilterType getExecFilter() {
    return execFilter;
  }

  public HttpContext getHttpContext() {
    return httpContext;
  }

  public void setHttpContext(HttpContext httpContext) {
    this.httpContext = httpContext;
  }

  public boolean isAsyn() {
    return isAsyn;
  }

  public void setAsyn(boolean isAsyn) {
    this.isAsyn = isAsyn;
  }

  public boolean isDel() {
    return isDel;
  }

  public void setDel(boolean isDel) {
    this.isDel = isDel;
  }

  public int getSessionID() {
    return sessionID;
  }

  public void setSessionID(int sessionID) {
    this.sessionID = sessionID;
  }
}