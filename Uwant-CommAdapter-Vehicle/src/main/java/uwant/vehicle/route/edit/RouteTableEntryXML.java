/**
 * Copyright (c) The openTCS Authors.
 *
 * This program is free software and subject to the MIT license. (For details,
 * see the licensing information (LICENSE.txt) you should have received with
 * this copy of the software.)
 */
package uwant.vehicle.route.edit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.jdom2.Element;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import uwant.vehicle.telegrams.NodeAction;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/** @author zhuchang */
public class RouteTableEntryXML {
  private List<RouteTableEntry> routeTableEntries;

  public void saveRouteFile(List<List<RouteTableEntry>> routeTableEntries) {
    Element root = new Element("root");

    routeTableEntries.forEach(
        (List<RouteTableEntry> u) ->
            u.forEach(
                e -> {
                  Element routeTableEntry = new Element("routeTableEntry");
                  routeTableEntry.setAttribute("routeId", Integer.toString(e.getRouteId()));
                  routeTableEntry.setAttribute("nodeId", Integer.toString(e.getNodeId()));
                  routeTableEntry.setAttribute("count", "4");

                  for (int i = 1; i < 5; i++) {
                    Element nodeAction = new Element("nodeAction");
                    nodeAction.setAttribute(
                        "id", Integer.toString(e.getNodeActionsMap().get(i).getActionId()));
                    nodeAction.setAttribute(
                        "param1", Integer.toString(e.getNodeActionsMap().get(i).getActionParam1()));
                    nodeAction.setAttribute(
                        "param2", Integer.toString(e.getNodeActionsMap().get(i).getActionParam2()));
                    routeTableEntry.addContent(nodeAction);
                  }

                  root.addContent(routeTableEntry);
                }));

    // 保存xml文件
    try {
      FileNameExtensionFilter filter = new FileNameExtensionFilter("*.xml", "xml");
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setFileFilter(filter);
      fileChooser.setMultiSelectionEnabled(false);
      int result = fileChooser.showSaveDialog(null);
      if (result == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        if (!file.getPath().endsWith(".xml")) {
          file = new File(file.getPath() + ".xml");
          if (!file.exists()) {
            file.createNewFile();
          }
        }
        Document doc = new Document(root);
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(doc, new FileOutputStream(file));
      }
    } catch (IOException exception) {
    }
  }

  public List<RouteTableEntry> importRouteFile() {
    routeTableEntries = new ArrayList<>();

    FileNameExtensionFilter filter = new FileNameExtensionFilter("*.xml", "xml");
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(filter);
    fileChooser.setMultiSelectionEnabled(false);
    int result = fileChooser.showOpenDialog(null);

    if (result == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      // 创建sax解析器
      SAXBuilder saxBuilder = new SAXBuilder();
      try {
        // 获取Document
        Document document = saxBuilder.build(file);
        Element classElement = document.getRootElement();
        // 递归读取xml节点信息
        parseElement(classElement, null);

        return routeTableEntries;
      } catch (JDOMException | IOException ex) {
        Logger.getLogger(RouteTableConfigXML.class.getName()).log(Level.SEVERE, null, ex);
        return null;
      }
    }
    return null;
  }

  public void parseElement(Element classElement, RouteTableEntry routeTableEntry1) {
    RouteTableEntry routeTableEntry = routeTableEntry1;
    List<Element> elementList = classElement.getChildren();
    Element element;
    for (int i = 0; i < elementList.size(); i++) {
      element = elementList.get(i);
      if ("routeTableEntry".equals(element.getName())) {
        routeTableEntry = new RouteTableEntry();
        routeTableEntry.setRouteId(Integer.valueOf(element.getAttributeValue("routeId")));
        routeTableEntry.setNodeId(Integer.valueOf(element.getAttributeValue("nodeId")));
      }

      if ("nodeAction".equals(element.getName())) {
        NodeAction nodeAction =
            new NodeAction(
                Integer.valueOf(element.getAttributeValue("id")),
                Integer.valueOf(element.getAttributeValue("param1")),
                Integer.valueOf(element.getAttributeValue("param2")));
        routeTableEntry.getNodeActionsMap().put(i + 1, nodeAction);
      }

      if (!element.getChildren().isEmpty()) {
        parseElement(element, routeTableEntry);
      } else if (i == elementList.size() - 1) {
        routeTableEntries.add(routeTableEntry);
      }
    }
  }
}
