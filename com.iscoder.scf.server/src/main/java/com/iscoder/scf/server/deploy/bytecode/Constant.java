package com.iscoder.scf.server.deploy.bytecode;

import com.iscoder.scf.common.annotation.OperationContract;
import com.iscoder.scf.common.annotation.ServiceBehavior;
import com.iscoder.scf.common.entity.ErrorState;
import com.iscoder.scf.common.entity.KeyValuePair;
import com.iscoder.scf.common.entity.Out;
import com.iscoder.scf.protocol.sdp.RequestProtocol;
import com.iscoder.scf.server.contract.context.IProxyFactory;
import com.iscoder.scf.server.contract.context.IProxyStub;
import com.iscoder.scf.server.contract.context.SCFContext;
import com.iscoder.scf.server.contract.context.SCFResponse;
import com.iscoder.scf.server.core.convert.Convert;
import com.iscoder.scf.server.core.convert.ConvertFacotry;
import com.iscoder.scf.server.exception.ServiceFrameException;

/**
 * 
 * 
 */
public class Constant {

  /**
   * service contract config xml
   */
  public static final String SERVICE_CONTRACT = "serviceframe.xml";

  /**
   * out parameter name
   */
  public static final String OUT_PARAM = Out.class.getName();

  /**
   * IProxyStub class name
   */
  public static final String IPROXYSTUB_CLASS_NAME = IProxyStub.class.getName();

  /**
   * SCFContext class name
   */
  public static final String SCFCONTEXT_CLASS_NAME = SCFContext.class.getName();

  /**
   * SCFRequest class name
   */
  public static final String SCFRESPONSE_CLASS_NAME = SCFResponse.class.getName();

  /**
   * ServiceFrameException class name
   */
  public static final String SERVICEFRAMEEXCEPTION_CLASS_NAME = ServiceFrameException.class.getName();

  /**
   * Request protocol class name
   */
  public static final String REQUEST_PROTOCOL_CLASS_NAME = RequestProtocol.class.getName();

  /**
   * IConvert class name
   */
  public static final String ICONVERT_CLASS_NAME = Convert.class.getName();

  /**
   * ConvertFactory class name
   */
  public static final String CONVERT_FACTORY_CLASS_NAME = ConvertFacotry.class.getName();

  /**
   * KeyValuePair protocol class name
   */
  public static final String KEYVALUEPAIR_PROTOCOL_CLASS_NAME = KeyValuePair.class.getName();

  /**
   * ErrorState class name
   */
  public static final String ERRORSTATE_CLASS_NAME = ErrorState.class.getName();

  /**
   * IProxyFactory class name
   */
  public static final String IPROXYFACTORY_CLASS_NAME = IProxyFactory.class.getName();

  /**
   * OperationContract class name
   */
  public static final String OPERATIONCONTRACT_CLASS_NAME = OperationContract.class.getName();

  /**
   * ServiceBehavior class name
   */
  public static final String SERVICEBEHAVIOR_CLASS_NAME = ServiceBehavior.class.getName();

  /**
   * ServiceContract class name
   */
  public static final String SERVICECONTRACT_CLASS_NAME = ContractInfo.class.getName();
}