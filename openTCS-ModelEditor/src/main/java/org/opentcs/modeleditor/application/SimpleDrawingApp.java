// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.modeleditor.application;

import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JFrame;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.DefaultDrawingView;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.RectangleFigure;

public class SimpleDrawingApp {

  private SimpleDrawingApp() {
  }

  public static void main(String[] args) {
    // Create the DrawingEditor and DrawingView
//    SDIApplication app = new SDIApplication();
//    DrawingEditor editor = new DefaultDrawingEditor();
    DefaultDrawingView view = new DefaultDrawingView();
    Drawing drawing = new DefaultDrawing();
    //view.setDrawing(drawing);

    // Create a simple figure, like a rectangle
    RectangleFigure rectangle = new RectangleFigure(50, 50, 100, 100);
//
//    // Add the rectangle to the drawing
    drawing.add(rectangle);

    // Set the drawing for the view
    view.setDrawing(drawing);

    // Set up the JFrame for the GUI
    JFrame frame = new JFrame("JHotDraw 7.6 Simple Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(view);
    frame.setSize(400, 400);
    frame.setVisible(true);
  }
}
