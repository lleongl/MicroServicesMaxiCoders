package main;

import org.junit.Test;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Utils {

  private static String T_HEADING_DIR = "T";
  private static String TR_HEADING_DIR = "TR";
  private static String R_HEADING_DIR = "R";
  private static String BR_HEADING_DIR = "BR";
  private static String B_HEADING_DIR = "B";
  private static String BL_HEADING_DIR = "BL";
  private static String L_HEADING_DIR = "L";
  private static String TL_HEADING_DIR = "TL";

  private static int T_HEADING_VAL = 0;
  private static int TR_HEADING_VAL = 45;
  private static int R_HEADING_VAL = 90;
  private static int BR_HEADING_VAL = 135;
  private static int B_HEADING_VAL = 180;
  private static int BL_HEADING_VAL = 225;
  private static int L_HEADING_VAL = 270;
  private static int TL_HEADING_VAL = 315;
  private static int DRONE_MAX_600 = 600;

  public static double getHeading(Point p1, Point p2) {
//  private static double getAngleDegrees(Point p1, Point p2) {
    if (p1.equals(p2)) {
      throw new IllegalArgumentException();
    }
    double a = -Math.atan2(p1.getY() - p2.getY(), p1.getX() - p2.getX());
    a = a * 180.0 / Math.PI;
    a -= 90;
    if (a >= 360.0) {
      a -= 360.0;
    }
    if (a < 0.0) {
      a += 360.0;
    }
    return a;
  }

  public static Point getHeadingEndPoint(Point source, int heading, int distance) {
    int x, y;
//    System.out.println(Math.sin(Math.toRadians(heading)));
//    System.out.println(Math.sin(Math.toRadians(heading)) * length);
//    System.out.println(Math.cos(Math.toRadians(heading)));
//    System.out.println(Math.cos(Math.toRadians(heading)) * length);
    x = (int)source.getX() + (int) Math.round( (Math.sin(Math.toRadians(heading)) * distance) );
    y = (int)source.getY() + (int) Math.round( (Math.cos(Math.toRadians(heading)) * distance) );
    return new Point(x, y);
  }

  public static Point getVector(Point p1, Point p2) {
    return new Point((int)(p2.getX() - p1.getX()), (int)(p2.getY() - p1.getY()));
  }

  public Map<String, Point> getNextDronePosss(Point drone, Point nextMonsterPos) {
    Map<String, Point> nextDronePossibilities = new HashMap<>();
    nextDronePossibilities.put(T_HEADING_DIR,  getHeadingEndPoint(drone, T_HEADING_VAL, DRONE_MAX_600));
    nextDronePossibilities.put(TR_HEADING_DIR, getHeadingEndPoint(drone, TR_HEADING_VAL, DRONE_MAX_600));
    nextDronePossibilities.put(R_HEADING_DIR,  getHeadingEndPoint(drone, R_HEADING_VAL, DRONE_MAX_600));
    nextDronePossibilities.put(BR_HEADING_DIR, getHeadingEndPoint(drone, BR_HEADING_VAL, DRONE_MAX_600));
    nextDronePossibilities.put(B_HEADING_DIR,  getHeadingEndPoint(drone, B_HEADING_VAL, DRONE_MAX_600));
    nextDronePossibilities.put(BL_HEADING_DIR, getHeadingEndPoint(drone, BL_HEADING_VAL, DRONE_MAX_600));
    nextDronePossibilities.put(L_HEADING_DIR,  getHeadingEndPoint(drone, L_HEADING_VAL, DRONE_MAX_600));
    nextDronePossibilities.put(TL_HEADING_DIR, getHeadingEndPoint(drone, TL_HEADING_VAL, DRONE_MAX_600));
    System.out.println(nextDronePossibilities);
    excludeUnsafe(nextDronePossibilities, nextMonsterPos);
    System.out.println(nextDronePossibilities);
    return nextDronePossibilities;
  }

  public void excludeUnsafe(Map<String, Point> nextDronePossibilities, Point nextMonsterPos) {
    Iterator<Map.Entry<String, Point>> iter = nextDronePossibilities.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<String, Point> nextDronePossibility = iter.next();
      Point nextDronePos = nextDronePossibility.getValue();
      if (nextDronePos.distance(nextMonsterPos) < 800) {
        iter.remove();
      }
    }
  }

  @Test
  public void test_next_monster_pos() {
    // ------------ AGGRESSIVE MONSTERS BY DRONE ------------
    // droneId(0), aggressiveDistance(2000.0), distance(1547.2107160952576), fishId(16), dir(TL)
    //------------ AGGRESSIVE MONSTER NEXT POS ------------
    // droneXY.x(2787), droneXY.y(3218)
    // onsterXY.x(1252), monsterXY.y(3024)
    // nextPos.x(1792), nextPos.y(3024)
    Point drone1 = new Point(2787, 3218);
    Point drone2 = new Point(1000, 3000);
    Point monster = new Point(1252, 3024);                  // x=1252,y=3024
    System.out.println( getVector(drone1, monster) );
    System.out.println( getVector(drone2, monster) );
    System.out.println( drone1.distance(monster) );               // 1547.2107160952576
    // System.out.println( drone2.distance(monster) );            // 253.14027731674784
    double heading = getHeading(monster, drone1);
    System.out.println(heading);                                  // 82.79689902317645
    // System.out.println( getHeadingEndPoint(monster, 0, 540) ); // x=1252,y=3564 ~ 3024+540 \o/
    Point nextMonsterPos = getHeadingEndPoint(monster, (int) heading, 540);
    System.out.println(nextMonsterPos);                           // x=1787,y=3099
    System.out.println( drone1.distance(nextMonsterPos) );        // 1007.0556091894827 ~ 1787-540 \o/
  }

  @Test
  public void test_next_drone_pos() {
    Point drone = new Point(2787, 3218);
    Point monster = new Point(1252, 3024);
    Point nextMonsterPos = getHeadingEndPoint(monster, (int) getHeading(monster, drone), 540);
    getNextDronePosss(drone, nextMonsterPos);
  }
  
  @Test
  public void test_get_heading() {
    Point O = new Point(0, 0);
    Point A = new Point(0, 2);
    Point B = new Point(2, 2);
    Point C = new Point(2, 0);
    Point D = new Point(-2, 2);
    System.out.println( getVector(O, A) );
    System.out.println( getVector(O, B) );
    System.out.println( getVector(O, C) );
    System.out.println( getVector(O, D) );
    System.out.println( getHeading(O, A) ); // 0.0
    System.out.println( getHeading(O, B) ); // 45.0
    System.out.println( getHeading(O, C) ); // 90.0
    System.out.println( getHeading(O, D) ); // 315.0
  }

  @Test
  public void test_heading_endpoint() {
    Point O = new Point(0, 0);
    System.out.println( getHeadingEndPoint(O, 0, 5) );   // x=0,y=5
    System.out.println( getHeadingEndPoint(O, 37, 5) );  // x=3,y=4
    System.out.println( getHeadingEndPoint(O, 45, 5) );  // x=4,y=4
    System.out.println( getHeadingEndPoint(O, 53, 5) );  // x=4,y=3
    System.out.println( getHeadingEndPoint(O, 90, 5) );  // x=5,y=0
    System.out.println( getHeadingEndPoint(O, 135, 5) );  // x=5,y=0
    System.out.println( getHeadingEndPoint(O, 180, 5) );  // x=5,y=0
    System.out.println( getHeadingEndPoint(O, 225, 5) );  // x=5,y=0
    System.out.println( getHeadingEndPoint(O, 270, 5) );  // x=5,y=0
    System.out.println( getHeadingEndPoint(O, 315, 5) ); // x=-4,y=4
    System.out.println( getHeadingEndPoint(O, 360, 5) ); // x=-4,y=4
  }

  @Test
  public void test_distance() {
    Point O = new Point(0, 0);
    Point A = new Point(3, 4);
    Point B = new Point(6, 8);
    System.out.println( getVector(O, A) );
    System.out.println( getVector(O, B) );
    System.out.println( getVector(A, B) );
    System.out.println( O.distance(A) ); // 5.0
    System.out.println( O.distance(B) ); // 10.0
  }

// private static Map<String, Integer> minGainUptrendOver30minByCoin;
//  static {
//    hashmap = new HashMap<>();
//    hashmap.put("T", 15);
//  }

}