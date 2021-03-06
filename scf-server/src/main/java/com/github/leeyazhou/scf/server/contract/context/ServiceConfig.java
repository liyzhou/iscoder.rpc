package com.github.leeyazhou.scf.server.contract.context;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ServiceConfig {

  private static final Logger logger = LoggerFactory.getLogger(ServiceConfig.class);

  private static Map<String, String> property = null;

  private ServiceConfig() {
    property = new HashMap<String, String>();
  }

  public void set(String key, String value) {
    property.put(key, value);
  }

  public String getString(String name) {
    return property.get(name);
  }

  public int getInt(String name) throws Exception {
    String value = property.get(name);
    if (value == null || value.equalsIgnoreCase("")) {
      return 0;
    }
    return Integer.parseInt(value);
  }

  public boolean getBoolean(String name) throws Exception {
    String value = property.get(name);
    if (value == null || value.equalsIgnoreCase("")) {
      throw new Exception("the property (" + name + ") is null");
    }
    return Boolean.parseBoolean(value);
  }

  public List<String> getList(String name, String split) {
    String value = property.get(name);
    if (value == null || value.equalsIgnoreCase("")) {
      return null;
    }

    List<String> list = new ArrayList<String>(3);
    String[] values = value.split(split);
    for (String v : values) {
      list.add(v);
    }
    return list;
  }

  /**
   * get service config from paths
   * 
   * @param paths
   * @return
   * @throws Exception
   */
  public static ServiceConfig getServiceConfig(String... paths) throws Exception {
    logger.info("load service config...");
    ServiceConfig instance = new ServiceConfig();

    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    XPathExpression exprProperty = xpath.compile("//configuration/property");
    XPathExpression exprName = xpath.compile("name");
    XPathExpression exprValue = xpath.compile("value");

    for (String p : paths) {
      File fServiceConfig = new File(p);
      if (!fServiceConfig.exists()) {
        continue;
      }

      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      domFactory.setNamespaceAware(true);
      DocumentBuilder builder = domFactory.newDocumentBuilder();
      Document doc = builder.parse(p);

      NodeList propertyNodes = (NodeList) exprProperty.evaluate(doc, XPathConstants.NODESET);
      for (int i = 0; i < propertyNodes.getLength(); i++) {
        Node node = propertyNodes.item(i);
        Node nameNode = (Node) exprName.evaluate(node, XPathConstants.NODE);
        Node valueNode = (Node) exprValue.evaluate(node, XPathConstants.NODE);
        Node append = valueNode.getAttributes().getNamedItem("append");
        String key, value;
        if (append != null && append.getNodeValue() != null && append.getNodeValue().equalsIgnoreCase("true")) {
          key = nameNode.getTextContent();
          value = property.get(nameNode.getTextContent());
          if (value != null) {
            value += "," + valueNode.getTextContent().trim().replaceAll("\n", "").trim();
          } else {
            value = valueNode.getTextContent().trim().replaceAll("\n", "").trim();
          }
        } else {
          key = nameNode.getTextContent().trim();
          value = valueNode.getTextContent().trim().replaceAll("\n", "").trim();
        }
        property.put(key, value);
      }
    }

    return instance;
  }

  /**
   * 读取授权文件
   * 
   * @param paths
   * @throws Exception
   */
  public static void getSecureConfig(String... paths) throws Exception {
    Global global = Global.getSingleton();
    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    XPathExpression exprProperty = xpath.compile("//secure");
    XPathExpression exprName = xpath.compile("key");
    XPathExpression exprValue = xpath.compile("method");

    for (String p : paths) {
      File fServiceConfig = new File(p);
      if (!fServiceConfig.exists()) {
        continue;
      }

      DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
      domFactory.setNamespaceAware(true);
      DocumentBuilder builder = domFactory.newDocumentBuilder();
      Document doc = builder.parse(p);

      NodeList propertyNodes = (NodeList) exprProperty.evaluate(doc, XPathConstants.NODESET);
      for (int i = 0; i < propertyNodes.getLength(); i++) {
        Node node = propertyNodes.item(i);
        Node nameNode = (Node) exprName.evaluate(node, XPathConstants.NODE);
        Node valueNode = (Node) exprValue.evaluate(node, XPathConstants.NODE);

        List<String> list = new ArrayList<String>();
        if (valueNode.getTextContent() != null) {
          list = Arrays.asList(valueNode.getTextContent().split(":"));
        }

        if (nameNode.getTextContent() != null) {
          global.addSecureMap(nameNode.getTextContent(), list);
        }

        list = null;
      }
    }
  }

  public String getServiceName() {
    return property.get("scf.service.name");
  }

  public void setServiceName(String serviceName) {
    set("scf.service.name", serviceName);
  }

  public boolean isHotDeploy() throws Exception {
    return getBoolean("scf.hotdeploy");
  }
}
