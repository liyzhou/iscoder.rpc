/*
 * Copyright 2010 58.com, Inc. SPAT team blog: http://blog.58.com/spat/ website: http://www.58.com
 */
package com.chaboshi.scf.protocol.serializer;

import java.nio.charset.Charset;

import com.chaboshi.scf.protocol.sfp.enumeration.SerializeType;

/**
 * SerializeBase
 *
 * @author Service Platform Architecture Team (spat@58.com)
 */
public abstract class SerializeBase {

  private static SCFSerialize scfSerialize = new SCFSerialize();

  private static JSONSerialize jsonSerialize = new JSONSerialize();
  private static KryoSerialize kryoSerialize = new KryoSerialize();

  private Charset encoder;

  public Charset getEncoder() {
    return encoder;
  }

  public void setEncoder(Charset encoder) {
    this.encoder = encoder;
  }

  public static SerializeBase getInstance(SerializeType serializeType) throws Exception {
    if (serializeType == SerializeType.SCFBinary) {
      return scfSerialize;
    } else if (serializeType == SerializeType.JSON) {
      return jsonSerialize;
    } else if (serializeType == SerializeType.KRYO) {
      return kryoSerialize;
    }

    throw new Exception("末知的序列化算法");
  }

  public abstract byte[] serialize(Object obj) throws Exception;

  public abstract <T> T deserialize(byte[] data, Class<T> cls) throws Exception;
}