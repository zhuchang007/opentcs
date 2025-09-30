package org.opentcs.kernelcontrolcenter.samples;

import org.opentcs.components.kernelcontrolcenter.ControlCenterPanel;

public class SamplesPanel
    extends
      ControlCenterPanel {
  public SamplesPanel() {
  }

  @Override
  public void initialize() {
    getAccessibleContext().setAccessibleName("SamplesPanels");
  }

  @Override
  public boolean isInitialized() {
    return false;
  }

  @Override
  public void terminate() {

  }
}
