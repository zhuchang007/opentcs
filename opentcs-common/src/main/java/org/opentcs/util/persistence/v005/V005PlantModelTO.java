// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.util.persistence.v005;

import static java.util.Objects.requireNonNull;

import jakarta.annotation.Nonnull;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.opentcs.util.persistence.BasePlantModelTO;
import org.xml.sax.SAXException;

/**
 */
@XmlRootElement(name = "model")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(
    propOrder = {"version", "name", "points", "paths", "vehicles", "locationTypes",
        "locations", "blocks", "visualLayout", "properties"}
)
public class V005PlantModelTO
    extends
      BasePlantModelTO {

  /**
   * This plant model implementation's version string.
   */
  public static final String VERSION_STRING = "0.0.5";

  private String name = "";
  private List<PointTO> points = new ArrayList<>();
  private List<PathTO> paths = new ArrayList<>();
  private List<VehicleTO> vehicles = new ArrayList<>();
  private List<LocationTypeTO> locationTypes = new ArrayList<>();
  private List<LocationTO> locations = new ArrayList<>();
  private List<BlockTO> blocks = new ArrayList<>();
  private VisualLayoutTO visualLayout = new VisualLayoutTO();
  private List<PropertyTO> properties = new ArrayList<>();

  /**
   * Creates a new instance.
   */
  public V005PlantModelTO() {
  }

  @XmlAttribute(required = true)
  public String getName() {
    return name;
  }

  public V005PlantModelTO setName(
      @Nonnull
      String name
  ) {
    requireNonNull(name, "name");
    this.name = name;
    return this;
  }

  @XmlElement(name = "point")
  public List<PointTO> getPoints() {
    return points;
  }

  public V005PlantModelTO setPoints(
      @Nonnull
      List<PointTO> points
  ) {
    requireNonNull(points, "points");
    this.points = points;
    return this;
  }

  @XmlElement(name = "path")
  public List<PathTO> getPaths() {
    return paths;
  }

  public V005PlantModelTO setPaths(
      @Nonnull
      List<PathTO> paths
  ) {
    requireNonNull(paths, "paths");
    this.paths = paths;
    return this;
  }

  @XmlElement(name = "vehicle")
  public List<VehicleTO> getVehicles() {
    return vehicles;
  }

  public V005PlantModelTO setVehicles(
      @Nonnull
      List<VehicleTO> vehicles
  ) {
    requireNonNull(vehicles, "vehicles");
    this.vehicles = vehicles;
    return this;
  }

  @XmlElement(name = "locationType")
  public List<LocationTypeTO> getLocationTypes() {
    return locationTypes;
  }

  public V005PlantModelTO setLocationTypes(
      @Nonnull
      List<LocationTypeTO> locationTypes
  ) {
    requireNonNull(locationTypes, "locationTypes");
    this.locationTypes = locationTypes;
    return this;
  }

  @XmlElement(name = "location")
  public List<LocationTO> getLocations() {
    return locations;
  }

  public V005PlantModelTO setLocations(
      @Nonnull
      List<LocationTO> locations
  ) {
    requireNonNull(locations, "locations");
    this.locations = locations;
    return this;
  }

  @XmlElement(name = "block")
  public List<BlockTO> getBlocks() {
    return blocks;
  }

  public V005PlantModelTO setBlocks(
      @Nonnull
      List<BlockTO> blocks
  ) {
    requireNonNull(blocks, "blocks");
    this.blocks = blocks;
    return this;
  }

  @XmlElement
  public VisualLayoutTO getVisualLayout() {
    return visualLayout;
  }

  public V005PlantModelTO setVisualLayout(
      @Nonnull
      VisualLayoutTO visualLayout
  ) {
    this.visualLayout = requireNonNull(visualLayout, "visualLayout");
    return this;
  }

  @XmlElement(name = "property")
  public List<PropertyTO> getProperties() {
    return properties;
  }

  public V005PlantModelTO setProperties(
      @Nonnull
      List<PropertyTO> properties
  ) {
    requireNonNull(properties, "properties");
    this.properties = properties;
    return this;
  }

  /**
   * Marshals this instance to its XML representation and writes it to the given writer.
   *
   * @param writer The writer to write this instance's XML representation to.
   * @throws IOException If there was a problem marshalling this instance.
   */
  public void toXml(
      @Nonnull
      Writer writer
  )
      throws IOException {
    requireNonNull(writer, "writer");

    try {
      createMarshaller().marshal(this, writer);
    }
    catch (JAXBException | SAXException exc) {
      throw new IOException("Exception marshalling data", exc);
    }
  }

  /**
   * Unmarshals an instance of this class from the given XML representation.
   *
   * @param reader Provides the XML representation to parse to an instance.
   * @return The instance unmarshalled from the given reader.
   * @throws IOException If there was a problem unmarshalling the given string.
   */
  public static V005PlantModelTO fromXml(
      @Nonnull
      Reader reader
  )
      throws IOException {
    requireNonNull(reader, "reader");

    try {
      return (V005PlantModelTO) createUnmarshaller().unmarshal(reader);
    }
    catch (JAXBException | SAXException exc) {
      throw new IOException("Exception unmarshalling data", exc);
    }
  }

  private static Marshaller createMarshaller()
      throws JAXBException,
        SAXException {
    Marshaller marshaller = createContext().createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    marshaller.setSchema(createSchema());
    return marshaller;
  }

  private static Unmarshaller createUnmarshaller()
      throws JAXBException,
        SAXException {
    Unmarshaller unmarshaller = createContext().createUnmarshaller();
    unmarshaller.setSchema(createSchema());
    return unmarshaller;
  }

  private static JAXBContext createContext()
      throws JAXBException {
    return JAXBContext.newInstance(V005PlantModelTO.class);
  }

  private static Schema createSchema()
      throws SAXException {
    URL schemaUrl
        = V005PlantModelTO.class.getResource("/org/opentcs/util/persistence/model-0.0.5.xsd");
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    return schemaFactory.newSchema(schemaUrl);
  }
}
