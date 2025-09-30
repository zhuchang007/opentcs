/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uwant.vehicle.route.edit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/** @author zhuchang */
public class RouteTableConfigXML {

  private final File xmlFile;
  private final Element root = new Element("root");
  private final Map<Integer, RouteDisplayAction> mapRouteDisplayAction = new HashMap<>();

  public RouteTableConfigXML(File xmlFile) {
    this.xmlFile = xmlFile;
  }

  public void createUwtConfigXMLFile() {

    // 创建一个action"前进"
    createOneActionElement(
        0, "检测", true, new String[][]{{"20", "020"}}, true, new String[][]{{"0", "0"}}
    );
    createOneActionElement(
        1,
        "前进",
        false,
        new String[][]{
            {"1", "1档"},
            {"2", "2档"},
            {"3", "3档"},
            {"4", "4档"},
            {"5", "5档"},
            {"6", "6档"},
            {"7", "7档"},
            {"8", "8档"},
            {"9", "9档"},
            {"10", "10档"}
        },
        true,
        new String[][]{{"0", "0"}}
    );
    createOneActionElement(
        2,
        "后退",
        false,
        new String[][]{
            {"1", "1档"},
            {"2", "2档"},
            {"3", "3档"},
            {"4", "4档"},
            {"5", "5档"},
            {"6", "6档"},
            {"7", "7档"},
            {"8", "8档"},
            {"9", "9档"},
            {"10", "10档"}
        },
        true,
        new String[][]{{"0", "0"}}
    );
    createOneActionElement(
        3,
        "左转",
        false,
        new String[][]{
            {"1", "1档"},
            {"2", "2档"},
            {"3", "3档"},
            {"4", "4档"},
            {"5", "5档"},
            {"6", "6档"},
            {"7", "7档"},
            {"8", "8档"},
            {"9", "9档"},
            {"10", "10档"}
        },
        true,
        new String[][]{{"0", "0"}}
    );
    createOneActionElement(
        4,
        "右转",
        false,
        new String[][]{
            {"1", "1档"},
            {"2", "2档"},
            {"3", "3档"},
            {"4", "4档"},
            {"5", "5档"},
            {"6", "6档"},
            {"7", "7档"},
            {"8", "8档"},
            {"9", "9档"},
            {"10", "10档"}
        },
        true,
        new String[][]{{"0", "0"}}
    );
    createOneActionElement(
        5,
        "前巡",
        false,
        new String[][]{
            {"1", "1档"},
            {"2", "2档"},
            {"3", "3档"},
            {"4", "4档"},
            {"5", "5档"},
            {"6", "6档"},
            {"7", "7档"},
            {"8", "8档"},
            {"9", "9档"},
            {"10", "10档"}
        },
        true,
        new String[][]{{"0", "0"}}
    );
    createOneActionElement(
        6,
        "后巡",
        false,
        new String[][]{
            {"1", "1档"},
            {"2", "2档"},
            {"3", "3档"},
            {"4", "4档"},
            {"5", "5档"},
            {"6", "6档"},
            {"7", "7档"},
            {"8", "8档"},
            {"9", "9档"},
            {"10", "10档"}
        },
        true,
        new String[][]{{"0", "0"}}
    );
    createOneActionElement(
        7, "直行分叉", false, new String[][]{{"0", "0"}}, true, new String[][]{{"83", "83"}}
    );
    createOneActionElement(
        8, "左叉分叉", false, new String[][]{{"0", "0"}}, true, new String[][]{{"83", "83"}}
    );
    createOneActionElement(
        9, "右叉分叉", false, new String[][]{{"0", "0"}}, true, new String[][]{{"83", "83"}}
    );
    createOneActionElement(
        10,
        "停止/延时",
        false,
        new String[][]{{"0", "不记录"}, {"1", "原速原向"}, {"2", "缓慢停止"}},
        true,
        new String[][]{{"83", "83"}}
    );

    Document doc = new Document(root);
    // 创建xml输出流操作类
    XMLOutputter xmlOutput = new XMLOutputter();
    // 格式化xml内容
    xmlOutput.setFormat(Format.getPrettyFormat().setEncoding("UTF-8"));

    try {
      // 把xml输出到指定位置
      xmlOutput.output(doc, new FileOutputStream(xmlFile));
    }
    catch (IOException e) {
    }
  }

  private void createOneActionElement(
      int id,
      String name,
      boolean param1Edit,
      String[][] param1s,
      boolean param2Edit,
      String[][] param2s
  ) {
    Element action = new Element("action");

    action
        .setAttribute("id", Integer.toString(id))
        .setAttribute("name", name)
        .setAttribute("param1_edit", Boolean.toString(param1Edit))
        .setAttribute("param2_edit", Boolean.toString(param2Edit));

    for (String[] str : param1s) {
      Element param1 = new Element("param1");
      param1.setAttribute("id", str[0]);
      param1.addContent(str[1]);
      action.addContent(param1);
    }

    for (String[] str : param2s) {
      Element param2 = new Element("param2");
      param2.setAttribute("id", str[0]);
      param2.addContent(str[1]);
      action.addContent(param2);
    }

    root.addContent(action);
  }

  public Map<Integer, RouteDisplayAction> parseUwtConfigXMLFile() {
    // 创建sax解析器
    SAXBuilder saxBuilder = new SAXBuilder();
    mapRouteDisplayAction.clear();
    try {
      // 获取Document
      Document document = saxBuilder.build(xmlFile);
      Element classElement = document.getRootElement();
      // 递归读取xml节点信息
      parseElement(classElement, null);

      return mapRouteDisplayAction;
    }
    catch (JDOMException | IOException ex) {
      Logger.getLogger(RouteTableConfigXML.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
  }

  public void parseElement(Element classElement, RouteDisplayAction routeDisplayAction1) {
    RouteDisplayAction routeDisplayAction = routeDisplayAction1;
    List<Element> elementList = classElement.getChildren();
    Element element;
    for (int i = 0; i < elementList.size(); i++) {
      element = elementList.get(i);
      if ("action".equals(element.getName())) {
        routeDisplayAction = new RouteDisplayAction();
        routeDisplayAction.setActionId(Integer.valueOf(element.getAttributeValue("id")));
        routeDisplayAction.setActionName(element.getAttributeValue("name"));
        routeDisplayAction.setParam1Edit(Boolean.valueOf(element.getAttributeValue("param1_edit")));
        routeDisplayAction.setParam2Edit(Boolean.valueOf(element.getAttributeValue("param2_edit")));
      }

      if ("param1".equals(element.getName())) {
        routeDisplayAction
            .getParam1()
            .put(Integer.valueOf(element.getAttributeValue("id")), element.getText());
      }

      if ("param2".equals(element.getName())) {
        routeDisplayAction
            .getParam2()
            .put(Integer.valueOf(element.getAttributeValue("id")), element.getText());
      }

      if (!element.getChildren().isEmpty()) {
        parseElement(element, routeDisplayAction);
      }
      else if (i == elementList.size() - 1) {
        mapRouteDisplayAction.put(routeDisplayAction.getActionId(), routeDisplayAction);
      }
    }
  }
}
