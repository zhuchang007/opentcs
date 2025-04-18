// SPDX-FileCopyrightText: The openTCS Authors
// SPDX-License-Identifier: MIT
package org.opentcs.strategies.basic.routing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.opentcs.strategies.basic.routing.PointRouter.INFINITE_COSTS;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentcs.components.kernel.routing.GroupMapper;
import org.opentcs.components.kernel.services.TCSObjectService;
import org.opentcs.data.model.Location;
import org.opentcs.data.model.Location.Link;
import org.opentcs.data.model.LocationType;
import org.opentcs.data.model.Point;
import org.opentcs.data.model.Vehicle;
import org.opentcs.data.order.DriveOrder;
import org.opentcs.data.order.DriveOrder.Destination;
import org.opentcs.data.order.Route;
import org.opentcs.data.order.TransportOrder;
import org.opentcs.strategies.basic.routing.jgrapht.PointRouterProvider;

/**
 * Tests for {@link DefaultRouter}.
 */
class DefaultRouterTest {

  private DefaultRouter defaultRouter;
  private TCSObjectService objectService;
  private PointRouterProvider pointRouterProvider;
  private GroupMapper routingGroupMapper;
  private DefaultRouterConfiguration configuration;
  private TransportOrder order;
  private PointRouter pointRouter;

  @BeforeEach
  public void setUp() {
    objectService = mock();
    pointRouterProvider = mock();
    routingGroupMapper = mock();
    configuration = mock();
    defaultRouter = new DefaultRouter(
        objectService,
        pointRouterProvider,
        routingGroupMapper,
        configuration
    );

    pointRouter = mock();
    Point point1 = new Point("P1").withType(Point.Type.HALT_POSITION);
    Point point2 = new Point("P2").withType(Point.Type.HALT_POSITION);
    order = new TransportOrder(
        "t1",
        List.of(
            new DriveOrder(
                "o1",
                new Destination(point1.getReference())
                    .withOperation(Destination.OP_MOVE)
            ),
            new DriveOrder(
                "o2",
                new Destination(point2.getReference())
                    .withOperation(Destination.OP_MOVE)
            )
        )
    );

    when(objectService.fetchObject(Point.class, "P1")).thenReturn(point1);
    when(objectService.fetchObject(Point.class, "P2")).thenReturn(point2);
  }

  @Test
  void checkRoutabilityNotRoutable() {
    when(pointRouterProvider.getPointRoutersByVehicleGroup())
        .thenReturn(Map.of("some-group", pointRouter));
    when(pointRouter.getCosts(any(Point.class), any(Point.class))).thenReturn(INFINITE_COSTS);

    assertThat(defaultRouter.checkRoutability(order), is(empty()));
  }

  @Test
  void checkRoutabilityIsRoutable() {
    Vehicle vehicle = new Vehicle("some-vehicle");
    when(pointRouterProvider.getPointRoutersByVehicleGroup())
        .thenReturn(Map.of("some-group", pointRouter));
    when(pointRouter.getCosts(any(Point.class), any(Point.class))).thenReturn(50L);
    when(objectService.fetchObjects(Vehicle.class)).thenReturn(Set.of(vehicle));
    when(routingGroupMapper.apply(vehicle)).thenReturn("some-group");

    assertThat(defaultRouter.checkRoutability(order), contains(vehicle));
  }

  @Test
  void checkGeneralRoutabilityNotRoutable() {
    when(pointRouterProvider.getGeneralPointRouter(order)).thenReturn(pointRouter);
    when(pointRouter.getCosts(any(Point.class), any(Point.class))).thenReturn(INFINITE_COSTS);

    assertThat(defaultRouter.checkGeneralRoutability(order), is(false));
  }

  @Test
  void checkGeneralRoutabilityIsRoutable() {
    when(pointRouterProvider.getGeneralPointRouter(order)).thenReturn(pointRouter);
    when(pointRouter.getCosts(any(Point.class), any(Point.class))).thenReturn(50L);

    assertThat(defaultRouter.checkGeneralRoutability(order), is(true));
  }

  @Test
  void provideRouteSequenceForTransportOrder() {
    Vehicle vehicle = new Vehicle("V1");
    Point pointA = new Point("A").withProperty("cost", "10");
    Point pointB = new Point("B").withProperty("cost", "11");
    Point pointC = new Point("C").withProperty("cost", "12");
    Point pointD = new Point("D").withProperty("cost", "13");
    LocationType type1 = new LocationType("some-type");
    Location locationBC = new Location("L1", type1.getReference());
    locationBC = locationBC.withAttachedLinks(
        Set.of(
            new Link(locationBC.getReference(), pointB.getReference()),
            new Link(locationBC.getReference(), pointC.getReference())
        )
    );

    TransportOrder transportOrder = new TransportOrder(
        "T-1",
        List.of(
            new DriveOrder("o1", new Destination(locationBC.getReference())),
            new DriveOrder(
                "o2",
                new Destination(pointD.getReference()).withOperation(Destination.OP_MOVE)
            )
        )
    );

    when(pointRouterProvider.getPointRouterForVehicle(vehicle, transportOrder))
        .thenReturn(pointRouter);
    when(pointRouter.getRouteSteps(any(Point.class), any(Point.class)))
        .thenAnswer(
            invocation -> {
              Point dest = invocation.getArgument(1);
              return List.of(
                  new Route.Step(
                      null,
                      null,
                      dest,
                      Vehicle.Orientation.UNDEFINED,
                      0,
                      Integer.parseInt(dest.getProperty("cost"))
                  )
              );
            }
        );
    when(objectService.fetchObject(Point.class, "D")).thenReturn(pointD);
    when(objectService.fetchObject(Point.class, pointB.getReference())).thenReturn(pointB);
    when(objectService.fetchObject(Point.class, pointC.getReference())).thenReturn(pointC);
    when(objectService.fetchObject(Location.class, "L1")).thenReturn(locationBC);
    when(objectService.fetchObject(LocationType.class, type1.getReference())).thenReturn(type1);

    Set<List<Route>> orderRoutes = defaultRouter
        .getRoutes(vehicle, pointA, transportOrder, 1);

    assertThat(orderRoutes, hasSize(1));
    assertThat(orderRoutes.stream().findFirst().get().get(0).getCosts(), is(11L));
    assertThat(orderRoutes.stream().findFirst().get().get(1).getCosts(), is(13L));
  }
}
