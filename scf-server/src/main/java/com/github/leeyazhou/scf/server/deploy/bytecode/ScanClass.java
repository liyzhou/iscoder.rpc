package com.github.leeyazhou.scf.server.deploy.bytecode;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.leeyazhou.scanner.ClassScanner;
import com.github.leeyazhou.scanner.DefaultClassScanner;
import com.github.leeyazhou.scanner.Scanner;
import com.github.leeyazhou.scf.core.annotation.AnnotationUtil;
import com.github.leeyazhou.scf.core.annotation.HttpPathParameter;
import com.github.leeyazhou.scf.core.annotation.HttpRequestMapping;
import com.github.leeyazhou.scf.core.annotation.OperationContract;
import com.github.leeyazhou.scf.core.annotation.ServiceBehavior;
import com.github.leeyazhou.scf.core.annotation.ServiceContract;
import com.github.leeyazhou.scf.core.loader.DynamicClassLoader;
import com.github.leeyazhou.scf.core.util.ClassUtil;
import com.github.leeyazhou.scf.server.deploy.bytecode.ContractInfo.SessionBean;

public class ScanClass {

  private static Logger logger = LoggerFactory.getLogger(ScanClass.class);

  /**
   * ContractInfo for create proxy
   */
  private static ContractInfo contractInfo = null;

  /**
   * contract ClassInfo
   */
  private static List<ClassInfo> contractClassInfos = new ArrayList<ClassInfo>();

  /**
   * behavior ClassInfo
   */
  private static List<ClassInfo> behaviorClassInfos = new ArrayList<ClassInfo>();

  private static Object lockHelper = new Object();

  /**
   * 
   * @param path
   * @param classLoader
   * @return
   * @throws Exception
   */
  public static ContractInfo getContractInfo(String path, DynamicClassLoader classLoader) {
    if (contractInfo == null) {
      synchronized (lockHelper) {
        if (contractInfo == null) {
          scan(path, classLoader);
        }
      }
    }

    return contractInfo;
  }

  /**
   * 
   * @param path
   * @param classLoader
   * @return
   * @throws Exception
   */
  public static List<ClassInfo> getContractClassInfos(String path, DynamicClassLoader classLoader) throws Exception {
    if (contractInfo == null) {
      synchronized (lockHelper) {
        if (contractInfo == null) {
          scan(path, classLoader);
        }
      }
    }

    return contractClassInfos;
  }

  /**
   * 
   * @param path
   * @param classLoader
   * @return
   * @throws Exception
   */
  public static List<ClassInfo> getBehaviorClassInfos(String path, DynamicClassLoader classLoader) throws Exception {
    if (contractInfo == null) {
      synchronized (lockHelper) {
        if (contractInfo == null) {
          scan(path, classLoader);
        }
      }
    }

    return behaviorClassInfos;
  }

  /**
   * scan jars create ContractInfo
   * 
   * @param path
   * @param classLoader
   * @return
   * @throws Exception
   */
  private static void scan(String path, DynamicClassLoader classLoader) {
    logger.info("begin scan jar from path : " + path);

    Set<Class<?>> clsSet = new LinkedHashSet<Class<?>>();
    ClassScanner classScanner = new DefaultClassScanner(Scanner.builder().setBasePackage(""));
    clsSet.addAll(classScanner.getByAnnotation(ServiceBehavior.class));
    clsSet.addAll(classScanner.getByAnnotation(ServiceContract.class));

    for (Class<?> cls : clsSet) {
      try {
        ServiceBehavior behavior = cls.getAnnotation(ServiceBehavior.class);
        ServiceContract contract = cls.getAnnotation(ServiceContract.class);
        if (behavior == null && contract == null) {
          continue;
        }

        if (contract != null) {
          ClassInfo ci = contract(cls);
          if (ci != null) {
            contractClassInfos.add(ci);
          }
        } else if (behavior != null) {
          ClassInfo ci = behavior(cls);
          if (ci != null) {
            behaviorClassInfos.add(ci);
          }
        }
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }

    contractInfo = createContractInfo(contractClassInfos, behaviorClassInfos);

    logger.info("finish scan jar");
  }

  /**
   * 
   * @param cls
   * @param ignoreAnnotation
   * @return
   */
  protected static ClassInfo contract(Class<?> cls, boolean ignoreAnnotation) {
    if (ignoreAnnotation) {
      ClassInfo ci = new ClassInfo();
      ci.setCls(cls);
      ci.setClassType(ClassInfo.ClassType.INTERFACE);

      Method[] methods = cls.getDeclaredMethods();
      List<ClassInfo.MethodInfo> methodInfos = new ArrayList<ClassInfo.MethodInfo>();

      for (Method m : methods) {
        if (Modifier.isPublic(m.getModifiers()) || Modifier.isProtected(m.getModifiers())) {
          ClassInfo.MethodInfo mi = new ClassInfo.MethodInfo();
          mi.setMethod(m);
          methodInfos.add(mi);
        }
      }
      ci.setMethodList(methodInfos);

      return ci;
    } else {
      return contract(cls);
    }
  }

  /**
   * 
   * @param cls
   * @return
   */
  protected static ClassInfo contract(Class<?> cls) {
    ServiceContract contractAnn = cls.getAnnotation(ServiceContract.class);

    ClassInfo ci = new ClassInfo();
    ci.setCls(cls);
    ci.setClassType(ClassInfo.ClassType.INTERFACE);

    List<Class<?>> interfaceList = getInterfaces(cls);
    List<ClassInfo.MethodInfo> methodInfos = new ArrayList<ClassInfo.MethodInfo>();

    for (Class<?> interfaceCls : interfaceList) {
      Method[] methods = interfaceCls.getDeclaredMethods();
      if (contractAnn != null && contractAnn.defaultAll()) {
        for (Method m : methods) {
          if (Modifier.isPublic(m.getModifiers()) || Modifier.isProtected(m.getModifiers())) {
            ClassInfo.MethodInfo mi = new ClassInfo.MethodInfo();
            mi.setMethod(m);
            methodInfos.add(mi);
          }
        }
      } else {
        for (Method m : methods) {
          if (Modifier.isPublic(m.getModifiers()) || Modifier.isProtected(m.getModifiers())) {
            OperationContract oc = m.getAnnotation(OperationContract.class);
            if (oc != null) {
              ClassInfo.MethodInfo mi = new ClassInfo.MethodInfo();
              mi.setMethod(m);
              methodInfos.add(mi);
            }
          }
        }
      }
    }

    ci.setMethodList(methodInfos);

    return ci;

  }

  /**
   * 
   * @param cls
   * @return
   * @throws Exception
   */
  protected static ClassInfo behavior(Class<?> cls) throws Exception {
    ServiceBehavior behaviorAnn = cls.getAnnotation(ServiceBehavior.class);

    ClassInfo ci = new ClassInfo();
    ci.setCls(cls);
    ci.setClassType(ClassInfo.ClassType.CLASS);

    if (behaviorAnn != null && !behaviorAnn.lookUP().equalsIgnoreCase(AnnotationUtil.DEFAULT_VALUE)) {
      ci.setLookUP(behaviorAnn.lookUP());
    } else {
      ci.setLookUP(cls.getSimpleName());
    }
    Method[] methods = cls.getDeclaredMethods();
    List<ClassInfo.MethodInfo> methodInfos = new ArrayList<ClassInfo.MethodInfo>();

    for (Method m : methods) {
      // only load public or protected method
      if (Modifier.isPublic(m.getModifiers()) || Modifier.isProtected(m.getModifiers())) {
        ClassInfo.MethodInfo mi = new ClassInfo.MethodInfo();
        mi.setMethod(m);

        HttpRequestMapping requestMappingAnn = m.getAnnotation(HttpRequestMapping.class);
        mi.setHttpRequestMapping(requestMappingAnn);

        Class<?>[] paramAry = m.getParameterTypes();
        Type[] types = m.getGenericParameterTypes();

        String[] paramNames = ClassUtil.getParamNames(cls, m);
        String[] mapping = new String[paramAry.length];
        HttpPathParameter[] paramAnnAry = new HttpPathParameter[paramAry.length];

        // load RequestMapping
        if (requestMappingAnn != null) {
          Object[][] annotations = ClassUtil.getParamAnnotations(cls, m);
          for (int i = 0; i < annotations.length; i++) {
            for (int j = 0; j < annotations[i].length; j++) {
              HttpPathParameter paramAnn = null;
              try {
                paramAnn = (HttpPathParameter) annotations[i][j];
              } catch (Exception ex) {

              }

              paramAnnAry[i] = paramAnn;
              if (paramAnn != null) {
                mapping[i] = paramAnn.mapping();
                break;
              } else {
                mapping[i] = paramNames[i];
              }
            }
          }

          for (int i = 0; i < paramAry.length; i++) {
            if (mapping[i] == null) {
              mapping[i] = paramNames[i];
            }
          }
        }

        ClassInfo.ParamInfo[] paramInfoAry = new ClassInfo.ParamInfo[paramAry.length];
        for (int i = 0; i < paramAry.length; i++) {
          paramInfoAry[i] =
              new ClassInfo.ParamInfo(i, paramAry[i], types[i], paramNames[i], mapping[i], paramAnnAry[i]);
        }
        mi.setParamInfoAry(paramInfoAry);

        methodInfos.add(mi);
      }
    }
    ci.setMethodList(methodInfos);

    return ci;
  }

  /**
   * create ContractInfo from contracts, behaviors
   * 
   * @param contracts
   * @param behaviors
   * @return
   */
  private static ContractInfo createContractInfo(List<ClassInfo> contracts, List<ClassInfo> behaviors) {

    ContractInfo contractInfo = new ContractInfo();
    List<SessionBean> sessionBeanList = new ArrayList<SessionBean>();
    for (ClassInfo c : contracts) {
      SessionBean bean = new SessionBean();
      bean.setInterfaceClass(c);
      bean.setInterfaceName(c.getCls().getName());
      Map<String, String> implMap = new HashMap<String, String>();

      for (ClassInfo b : behaviors) {
        Class<?>[] interfaceAry = b.getCls().getInterfaces();
        for (Class<?> item : interfaceAry) {
          if (item == c.getCls()) {
            implMap.put(b.getLookUP(), b.getCls().getName());
            break;
          }
        }
      }
      bean.setInstanceMap(implMap);
      sessionBeanList.add(bean);
    }

    contractInfo.setSessionBeanList(sessionBeanList);
    return contractInfo;
  }

  /**
   * get all interfaces
   * 
   * @param cls
   * @return
   */
  private static List<Class<?>> getInterfaces(Class<?> cls) {
    List<Class<?>> clsList = new ArrayList<Class<?>>();
    getInterfaces(cls, clsList);
    return clsList;
  }

  /**
   * get all interfaces
   * 
   * @param cls
   * @param clsList
   */
  private static void getInterfaces(Class<?> cls, List<Class<?>> clsList) {
    clsList.add(cls);
    Class<?>[] aryCls = cls.getInterfaces();

    if (aryCls != null && aryCls.length > 0) {
      for (Class<?> c : aryCls) {
        getInterfaces(c, clsList);
      }
    }
  }
}
