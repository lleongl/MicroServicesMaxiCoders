import java.util.*;

// Define the data structures as records
record Vector(double x, double y) {
    static double calculateDistance(Vector point1, Vector point2) {
        double deltaX = point2.x() - point1.x();
        double deltaY = point2.y() - point1.y();

        // Applying the Euclidean distance formula: sqrt((x2 - x1)^2 + (y2 - y1)^2)
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
    @Override
    public String toString() {
      return "(" + this.x + ", " + this.y + ")";
     }

    public Vector add(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y);
    }

    public Vector multiply(int scalar) {
        return new Vector(this.x * scalar, this.y * scalar);
    }

    public Vector normalize() {
        double mag = Math.sqrt(x * x + y * y);
        return new Vector(x / mag, y / mag);
    }

    public Vector perpendicularClockwise()
    {
        return new Vector(this.y(), -this.x());
    }

    public Vector perpendicularCounterClockwise()
    {
        return new Vector(-this.y(), this.x());
    }
}

record Point(int x, int y) {
  static double calculateDistance(Point point1, Point point2) {
    double deltaX = point2.x() - point1.x();
    double deltaY = point2.y() - point1.y();
    // Applying the Euclidean distance formula: sqrt((x2 - x1)^2 + (y2 - y1)^2)
    return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
  }
}

record FishDetail(int color, int type) {}

record Fish(int fishId, Vector pos, Vector speed, FishDetail detail) {
  public boolean isMonster() {
    return detail.type() == -1;
  }
}

record Drone(int droneId, Vector pos, boolean dead, int battery, List<Integer> scans) {}

record RadarBlip(int fishId, String dir) {}

 class DroneState{
    public boolean goCenter = false;
    public boolean goUp = false;
    public boolean chaseFish = false;
    public boolean findFish = false;
    public int fleeMonster = 0;
    public int lastLight = 0 ;

    public DroneState(boolean first){

    }

    public void setGoCenter(boolean b){goCenter = b;}
    public void setGoUp(boolean b){goUp = b;}
    public void setChaseFish(boolean b){chaseFish = b;}
    public void setFindFish(boolean b){findFish = b;}
    public void setFleeMonster(int b){fleeMonster = b;}

    public void resetState(){
        goCenter = false;
        goUp = false;
        chaseFish = false;
        findFish = false;
        fleeMonster = 0;
    }

    public boolean noState(){
        return !goCenter && !goUp && !chaseFish && !findFish && fleeMonster != 0;
    }
}



class Player {
    public static DroneState firstDroneState = new DroneState(true);
    public static DroneState secondDroneState = new DroneState(false);

    public static Vector fleeingVectorD1 = null;
    public static Vector fleeingVectorD2 = null;

    public static int turnNumber = 0 ;

      public static String T_HEADING_DIR = "T";
      public static String TR_HEADING_DIR = "TR";
      public static String R_HEADING_DIR = "R";
      public static String BR_HEADING_DIR = "BR";
      public static String B_HEADING_DIR = "B";
      public static String BL_HEADING_DIR = "BL";
      public static String L_HEADING_DIR = "L";
      public static String TL_HEADING_DIR = "TL";

      public static int T_HEADING_VAL = 0;
      public static int TR_HEADING_VAL = 45;
      public static int R_HEADING_VAL = 90;
      public static int BR_HEADING_VAL = 135;
      public static int B_HEADING_VAL = 180;
      public static int BL_HEADING_VAL = 225;
      public static int L_HEADING_VAL = 270;
      public static int TL_HEADING_VAL = 315;
      public static int DRONE_MAX_600 = 600;

    static Map<Integer,Set<Integer>> combinationsColors = new HashMap<>();
    static Map<Integer,Set<Integer>> combinationsTypes = new HashMap<>();
    static Map<Integer,Set<Integer>> combinationsColorsFishesMissing = new HashMap<>();
    static Map<Integer,Set<Integer>> combinationsTypesFishesMissing = new HashMap<>();
    public Vector findFish(int id){
        return null;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        Map<Integer, FishDetail> fishDetails = new HashMap<>();

       int fishCount = in.nextInt();
        for (int i = 0; i < fishCount; i++) {
          int fishId = in.nextInt();
          int color = in.nextInt();
          int type = in.nextInt();
          fishDetails.put(fishId, new FishDetail(color, type));

          if (color != -1) {
            if (!combinationsColors.containsKey(color)) {
              combinationsColors.put(color, new HashSet<>());
            }

            combinationsColors.get(color).add(fishId);

            if (!combinationsTypes.containsKey(type)) {
              combinationsTypes.put(type, new HashSet<>());
            }
            combinationsTypes.get(type).add(fishId);
          }
        }
        printMissingCombinations();

        // game loop
        while (true) {
            Player.turnNumber += 1;
            List<Integer> myScans = new ArrayList<>();
            List<Integer> foeScans = new ArrayList<>();
            Map<Integer, Drone> droneById = new HashMap<>();
            List<Drone> myDrones = new ArrayList<>();
            List<Drone> foeDrones = new ArrayList<>();
            List<Fish> visibleFishes = new ArrayList<>();
            Map<Integer, List<RadarBlip>> myRadarBlips = new HashMap<>();

            int myScore = in.nextInt();
            int foeScore = in.nextInt();

            int myScanCount = in.nextInt();
            for (int i = 0; i < myScanCount; i++) {
                int fishId = in.nextInt();
                myScans.add(fishId);
            }

            int foeScanCount = in.nextInt();
            for (int i = 0; i < foeScanCount; i++) {
                int fishId = in.nextInt();
                foeScans.add(fishId);
            }

            int myDroneCount = in.nextInt();
            for (int i = 0; i < myDroneCount; i++) {
                int droneId = in.nextInt();
                int droneX = in.nextInt();
                int droneY = in.nextInt();
                boolean dead = in.nextInt() == 1;
                int battery = in.nextInt();
                Vector pos = new Vector(droneX, droneY);
                Drone drone = new Drone(droneId, pos, dead, battery, new ArrayList<>());
                droneById.put(droneId, drone);
                myDrones.add(drone);
                myRadarBlips.put(droneId, new ArrayList<>());
            }

            int foeDroneCount = in.nextInt();
            for (int i = 0; i < foeDroneCount; i++) {
                int droneId = in.nextInt();
                int droneX = in.nextInt();
                int droneY = in.nextInt();
                boolean dead = in.nextInt() == 1;
                int battery = in.nextInt();
                Vector pos = new Vector(droneX, droneY);
                Drone drone = new Drone(droneId, pos, dead, battery, new ArrayList<>());
                droneById.put(droneId, drone);
                foeDrones.add(drone);
            }

            int droneScanCount = in.nextInt();
            for (int i = 0; i < droneScanCount; i++) {
                int droneId = in.nextInt();
                int fishId = in.nextInt();
                droneById.get(droneId).scans().add(fishId);
            }

            int visibleFishCount = in.nextInt();
            for (int i = 0; i < visibleFishCount; i++) {
                int fishId = in.nextInt();
                int fishX = in.nextInt();
                int fishY = in.nextInt();
                int fishVx = in.nextInt();
                int fishVy = in.nextInt();
                Vector pos = new Vector(fishX, fishY);
                Vector speed = new Vector(fishVx, fishVy);
                FishDetail detail = fishDetails.get(fishId);
                visibleFishes.add(new Fish(fishId, pos, speed, detail));
            }

            int myRadarBlipCount = in.nextInt();
            for (int i = 0; i < myRadarBlipCount; i++) {
                int droneId = in.nextInt();
                int fishId = in.nextInt();
                String radar = in.next();
                myRadarBlips.get(droneId).add(new RadarBlip(fishId, radar));
            }
            // printAgressiveMonsterByDrone(myDrones, visibleFishes, myRadarBlips);

          for (Map.Entry<Integer, Set<Integer>> entry: combinationsColors.entrySet()) {
            combinationsColorsFishesMissing.put(entry.getKey(), new HashSet<>(entry.getValue()));
          }
          for (Map.Entry<Integer, Set<Integer>> entry: combinationsTypes.entrySet()) {
            combinationsTypesFishesMissing.put(entry.getKey(), new HashSet<>(entry.getValue()));
          }

            System.err.println(String.format("NUmber of radar blip: %d ", myRadarBlipCount));
            updateCombinationsFishesMissingBecauseSaved( myScans, fishDetails, myRadarBlips);
            
            String order = "wait 0 no order inputed";
            int lightNumber = Player.turnNumber % 3 == 0 ? 1 : 0; 
            // System.out.println(String.format("MOVE %d %d %d", targetX, targetY, light));
            Vector firstDroneTarget = null; 
            Vector firstDronePosition = null; 
            boolean firstDrone = true;
            boolean shouldResurface = shouldResurface (myDrones, fishDetails, myScans, foeScans);

            for (Drone drone : myDrones) {
                int x = (int) drone.pos().x();
                int y = (int) drone.pos().y();
                if (y < 1500) lightNumber =0;
                if(firstDrone)
                  firstDronePosition = drone.pos();

                DroneState ds = drone.droneId() ==0 || drone.droneId() == 1 ? Player.firstDroneState : Player.secondDroneState;
                // System.err.println(String.format("go up: %b ", ds.goUp));

                Vector fleeingVector = null;
                List<Fish> nearByMonsters = new LinkedList<>();
                for(Fish f: visibleFishes){
                  if(f.detail().color() == -1 && Vector.calculateDistance(drone.pos(),f.pos()) < 2000) {
                    nearByMonsters.add(f);
                  }
                }
                if(!nearByMonsters.isEmpty()){

                  Comparator<Fish> distanceComparator = Comparator.comparingDouble(fish ->
                      Vector.calculateDistance(fish.pos(), drone.pos()));
                  Collections.sort(nearByMonsters, distanceComparator);

                  // closest visible monster
                  Fish f = nearByMonsters.get(0);
                  //todo: do something with targetToAvoidMonster
                  Vector targetToAvoidMonster = getTargetToAvoidMonster(nearByMonsters, drone.pos(), ds);
                  ds.fleeMonster = 4; // number of turn fleeing
                  lightNumber = 0;
                  System.err.println(String.format("Detecting monster !!!!  %b ", f.fishId() ) );
                  Vector nextMonsterPos = getAgressiveMonsterNextPos(drone, f);

                  fleeingVector = new Vector(x -nextMonsterPos.x() , y - nextMonsterPos.y() );

                  if(firstDrone)
                     Player.fleeingVectorD1 = fleeingVector;
                  else
                     Player.fleeingVectorD2 = fleeingVector;
                }

              ds.fleeMonster -= 1 ;
              
                if(shouldResurface){                 
                    System.err.println(String.format("should resurfance  %b ", true ) );
                    ds.setGoUp(true);
                }
                if( ds.goUp || ds.fleeMonster >0){
                    if(Vector.calculateDistance(drone.pos(), new Vector(x,300)) < 200){                        
                        ds.resetState();                        
                    }else{
                        if(ds.fleeMonster <=  0)
                            order = String.format("MOVE %d 300 %d going up !!!", (int) x ,0);
                        else{
                           if((firstDrone && Player.fleeingVectorD1 != null) || (!firstDrone && Player.fleeingVectorD2 != null)){
                                if(firstDrone)
                                 order = String.format("MOVE %d %d %d Detecting monster !!!", (int) (drone.pos().x() + Player.fleeingVectorD1.x()), (int) (drone.pos().y() + Player.fleeingVectorD1.y()) ,0);
                                   else
                                order = String.format("MOVE %d %d %d Detecting monster !!!", (int)(drone.pos().x()  + Player.fleeingVectorD2.x()), (int) ( drone.pos().y() +  Player.fleeingVectorD2.y()) ,0);
                            }else{
                                order = String.format("MOVE 5000 0 0 going up !!!");
                            }
                        }
                    }
                }else{
                    //no fleeing, no going, trying to go down to find fishes 
                    Vector targetVector = null;     

                    for (Map.Entry<Integer, Set<Integer>> listOfMissingFish : Player.combinationsTypesFishesMissing.entrySet()) {
                        for( int fishId: listOfMissingFish.getValue()){
                            Vector possibleTarget = findFish( fishId,  myRadarBlips, droneById, fishDetails) ;
                            //System.err.println(String.format("Considering possible target  %s ",possibleTarget.toString() ) );

                            if(targetVector == null){
                                targetVector = possibleTarget;
                            }else{
                                if(firstDrone){
                                    if(possibleTarget.y() >  targetVector.y() || (possibleTarget.y() ==  targetVector.y()  &&  possibleTarget.x() <  targetVector.x())  ){
                                      targetVector = possibleTarget;
                                  }
                                }else{
                                    if(possibleTarget.y() >  targetVector.y() || (possibleTarget.y() ==  targetVector.y()  &&  possibleTarget.x() > targetVector.x())  ){
                                      targetVector = possibleTarget;
                                  }
                                }                                
                                
                            }
                            
                        }
                    }
                    firstDroneTarget =  targetVector; 
                    firstDrone = false;
                   
                    if(targetVector != null)
                        order = String.format("MOVE %d %d %d Moving too %s", (int) targetVector.x(), (int) targetVector.y(), lightNumber, targetVector.toString());
                            firstDrone = false;

                }
                ds.lastLight=lightNumber;
                System.out.println(order);                     
            }
        }
    }




  public static void printMissingCombinations(){
    System.err.println("------------ MISSING COMBINATIONS TYPES ------------");
    for (int type: combinationsTypesFishesMissing.keySet()) {
      System.err.print("Type " + type + ": ");
      for (int id: combinationsTypesFishesMissing.get(type)) {
        System.err.print(id + ", ");
      }
      System.err.println();
    }

    System.err.println("------------ MISSING COMBINATIONS COLORS ------------");
    for (int color: combinationsColorsFishesMissing.keySet()) {
      System.err.print("Color " + color + ": ");
      for (int id: combinationsColorsFishesMissing.get(color)) {
        System.err.print(id + ", ");
      }
      System.err.println();
    }
  }

  public static void updateCombinationsFishesMissingBecauseSaved(List<Integer> myScans, Map<Integer, FishDetail> fishDetails, Map<Integer, List<RadarBlip>> myRadarBlips) {
    for (int fishId: myScans) {
      FishDetail fish = fishDetails.get(fishId);
      combinationsColorsFishesMissing.get(fish.color()).remove(fishId);
      combinationsTypesFishesMissing.get(fish.type()).remove(fishId);
    }

    //todo: check fish ids
    Set<Integer> unscannableFishes = new HashSet<>(Set.of(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20));

    // remove fishes still on the map, the rest are unscannable    
    List<RadarBlip> blipsDrone0 = null; 
    if(myRadarBlips.get(0) != null){
      blipsDrone0 = myRadarBlips.get(0);
    }
    if(myRadarBlips.get(1) != null){
      blipsDrone0 = myRadarBlips.get(1);
    }
    for (RadarBlip blip : blipsDrone0) {
        unscannableFishes.remove(blip.fishId());
    }

    // remove unscannable fishes from the list of fishes we want to get
    for (int escapedFish: unscannableFishes) {
        combinationsColorsFishesMissing.get(0).remove(escapedFish);
        combinationsColorsFishesMissing.get(1).remove(escapedFish);
        combinationsColorsFishesMissing.get(2).remove(escapedFish);
        combinationsColorsFishesMissing.get(3).remove(escapedFish);
        combinationsTypesFishesMissing.get(0).remove(escapedFish);
        combinationsTypesFishesMissing.get(1).remove(escapedFish);
        combinationsTypesFishesMissing.get(2).remove(escapedFish);
    }

    // System.err.println("UPDATE!!");
    // printMissingCombinations();
  }

    public static boolean shouldResurface(List<Drone> myDrones, Map<Integer, FishDetail> fishDetails, List<Integer> myScans, List<Integer> foeScans) {
        Set<Integer> allScans = new HashSet<>(myScans);
        for (Drone drone : myDrones) {
            allScans.addAll(drone.scans());
        }

        boolean res = false;
        for (Drone drone: myDrones) {
            for (int scannedFishId: drone.scans()) {
                FishDetail fish = fishDetails.get(scannedFishId);
                boolean removeColorRes = combinationsColorsFishesMissing.get(fish.color()).remove(scannedFishId);
                if (removeColorRes) {
                    System.err.println("Removed Color + " + scannedFishId);
                }

                boolean removeTypeRes = combinationsTypesFishesMissing.get(fish.type()).remove(scannedFishId);
                if (removeTypeRes) {
                    System.err.println("Removed type + " + scannedFishId);
                }

                // if all fishes of 1 color just collected
                if (removeColorRes && combinationsColorsFishesMissing.get(fish.color()).isEmpty()
                && !foeHasAlreadyCompletedCombinationColor(fish.color(), foeScans)) {
                    Set<Integer> allFishesForColor = combinationsColors.get(fish.color());
                    int scannedFishesForColor = 0;
                    for (int fishId: allFishesForColor) {
                        if (allScans.contains(fishId)) {
                            scannedFishesForColor++;
                        }
                    }

                    // all 3 fishes of the same color
                    if (scannedFishesForColor == 3) {
                        res = true;
                    }
                }

                // if all fishes of 1 type just collected
                if (fish.type() != 0 && removeTypeRes && combinationsTypesFishesMissing.get(fish.type()).isEmpty()
                && !foeHasAlreadyCompletedCombinationType(fish.type(), foeScans)) {
                    Set<Integer> allFishesForType = combinationsTypes.get(fish.type());
                    int scannedFishesForType = 0;
                    for (int fishId: allFishesForType) {
                        if (allScans.contains(fishId)) {
                            scannedFishesForType++;
                        }
                    }
                    // all 4 fishes of the same type
                    if (scannedFishesForType == 4) {
                        res = true;
                    }
                }
            }
        }

        printMissingCombinations();

        return res;
    }

    public static boolean foeHasAlreadyCompletedCombinationColor(int color, List<Integer> foeScans) {
        Set<Integer> allFishesForColor = combinationsColors.get(color);
        int scannedFishesForColor = 0;
        for (int fishId: allFishesForColor) {
            if (foeScans.contains(fishId)) {
                scannedFishesForColor++;
            }
        }

        // all 3 fishes of the same color
        return scannedFishesForColor == 3;
    }

    public static boolean foeHasAlreadyCompletedCombinationType(int type, List<Integer> foeScans) {
        Set<Integer> allFishesForType = combinationsTypes.get(type);
        int scannedFishesForType = 0;
        for (int fishId: allFishesForType) {
            if (foeScans.contains(fishId)) {
                scannedFishesForType++;
            }
        }

        // all 4 fishes of the same type
        return scannedFishesForType == 4;
    }

    public int computeScore(List<Drone> myDrones) {
        int res = 0;

        return res;
    }

   public static Vector findFish(int fishId, Map<Integer, List<RadarBlip>> myRadarBlips, Map<Integer, Drone> droneById, Map<Integer, FishDetail> fishDetails) {
    Vector res;
    record RadarRes(Vector dronePos, String dir) {}
    RadarRes[] radarRes = new RadarRes[2];

    int i = 0;
    for (Map.Entry<Integer, List<RadarBlip>> entry: myRadarBlips.entrySet()) {
      Vector dronePos = droneById.get(entry.getKey()).pos();
      List<RadarBlip> blips = entry.getValue();
      String dir;
      for (RadarBlip blip: blips) {
        if (blip.fishId() == fishId) {
          dir = blip.dir();
          radarRes[i] = new RadarRes(dronePos, dir);
          break;
        }
      }
      i++;
    }

    int dirDistance = 1000;
    String dirForMostLeft;
    Vector mostLeftPos;
    String dirForMostRight;
    Vector mostRightPos;
    String dirForMostTop;
    Vector mostTopPos;
    String dirForMostBottom;
    Vector mostBottomPos;

    if (radarRes[0].dronePos().x() < radarRes[1].dronePos().x()) {
      dirForMostLeft = radarRes[0].dir();
      mostLeftPos = radarRes[0].dronePos();
      dirForMostRight = radarRes[1].dir();
      mostRightPos = radarRes[1].dronePos();
    } else {
      dirForMostLeft = radarRes[1].dir();
      mostLeftPos = radarRes[1].dronePos();
      dirForMostRight = radarRes[0].dir();
      mostRightPos = radarRes[0].dronePos();
    }

    if (radarRes[0].dronePos().y() < radarRes[1].dronePos().y()) {
      dirForMostTop = radarRes[0].dir();
      mostTopPos = radarRes[0].dronePos();
      dirForMostBottom = radarRes[1].dir();
      mostBottomPos = radarRes[1].dronePos();
    } else {
      dirForMostTop = radarRes[1].dir();
      mostTopPos = radarRes[1].dronePos();
      dirForMostBottom = radarRes[0].dir();
      mostBottomPos = radarRes[0].dronePos();
    }

    int x = 0;
    int y = 0;
    if ("TL".equals(dirForMostLeft) || "BL".equals(dirForMostLeft)) {
      x = 0;
    } else if ("TR".equals(dirForMostRight) || "BR".equals(dirForMostRight)) {
      x = 9999;
    } else {
      x = (int) (mostLeftPos.x() + mostRightPos.x()) / 2;
    }

    int minY = 0;
    int maxY = 9999;
    if (fishDetails.get(fishId).type() == 0) {
      minY = 2500;
      maxY = 5000;
    } else if (fishDetails.get(fishId).type() == 1) {
      minY = 5000;
      maxY = 7500;
    } else {
      // type == 2
      minY = 7500;
      maxY = 10000;
    }

    if ("TL".equals(dirForMostTop) || "TR".equals(dirForMostTop)) {
      if (minY <= (mostTopPos.y() - dirDistance) && (mostTopPos.y() - dirDistance) <= maxY) {
        y = (int) mostTopPos.y() - dirDistance;
      } else {
        y = (minY + maxY) / 2;
      }
    } else if ("BL".equals(dirForMostBottom) || "BR".equals(dirForMostBottom)) {
      if (minY <= (mostBottomPos.y() + dirDistance) && (mostBottomPos.y() + dirDistance) <= maxY) {
        y = (int) mostBottomPos.y() + dirDistance;
      } else {
        y = (minY + maxY) / 2;
      }
    } else {
      y = (int) (mostBottomPos.y() + mostTopPos.y()) / 2;
      if (minY > y || y > maxY) {
        y = (minY + maxY) / 2;
      }
    }


    res = new Vector(x,y);
    return res;
  }

    public static Vector getTargetToAvoidMonster(List<Fish> nearByMonsters, Vector dronePos, DroneState ds) {

        Vector monsterPos = nearByMonsters.get(0).pos();
        Vector directionVector = new Vector(dronePos.x() - monsterPos.x(), dronePos.y() - monsterPos.y()).normalize();
        int distance = 540;
        Vector monsterNextPos = monsterPos.add(directionVector.multiply(distance));

        Vector perpendicularVectorCandidate1 = directionVector.perpendicularClockwise().normalize();
        Vector perpendicularVectorCandidate2 = directionVector.perpendicularCounterClockwise().normalize();

        Vector perpendicularVector;
        if (!ds.goUp) {
            perpendicularVector = perpendicularVectorCandidate1.y() >= 0 ? perpendicularVectorCandidate1 : perpendicularVectorCandidate2;
        } else {
            perpendicularVector = perpendicularVectorCandidate1.y() <= 0 ? perpendicularVectorCandidate1 : perpendicularVectorCandidate2;
        }

        int distancePerpendicular = 600;

        Vector newDroneCoordinates = dronePos.add(perpendicularVector.multiply(distancePerpendicular));

        if (isValidTargetToAvoidMonster(newDroneCoordinates, nearByMonsters)) {
          return newDroneCoordinates;
        } else {

          // flee 1
          newDroneCoordinates = dronePos.add(directionVector.multiply(600));
          if (isValidTargetToAvoidMonster(newDroneCoordinates, nearByMonsters)) {
            return newDroneCoordinates;
          } else {
            // flee 2
            Vector fleeVector = new Vector(-perpendicularVector.x(), -perpendicularVector.y());
            newDroneCoordinates = dronePos.add(fleeVector.multiply(600));
            return newDroneCoordinates;
          }
        }
    }

    public static boolean isValidTargetToAvoidMonster(Vector target, List<Fish> nearByMonsters) {

      if (target.x() < 0 || target.x() > 9999 || target.y() < 0 || target.y() > 9999) {
        return false;
      }

      int minAuthorizedDistance = 500;
      for (Fish monster: nearByMonsters) {
        if (Vector.calculateDistance(monster.pos(), target) < minAuthorizedDistance) {
          return false;
        }
      }

      return true;
    }


    public static void printAgressiveMonsterByDrone(List<Drone> myDrones, List<Fish> visibles, Map<Integer, List<RadarBlip>> myRadarBlips) {
        System.err.println("------------ AGGRESSIVE MONSTERS BY DRONE ------------");
        for (Drone drone : myDrones) {
          int droneId = drone.droneId();
          DroneState ds = droneId == 0 || droneId == 1 ? Player.firstDroneState : Player.secondDroneState;
          for (Fish visible : visibles) {
            if (visible.isMonster()) {
              Fish monster = visible;
              double distance = Vector.calculateDistance(drone.pos(), monster.pos());
              //  0-> 800
              //  1-> 2000
              // Aggressive if distance < 800||2000
              double aggressiveDistance = ds.lastLight == 0 ? 800 : 2000;
              if (distance < aggressiveDistance) {
                List<RadarBlip> radarBlips = myRadarBlips.get(droneId);
                String dir = "";
                for (RadarBlip radarBlip : radarBlips) {
                  if (radarBlip.fishId() == monster.fishId()) {
                      dir = radarBlip.dir();
                      break;
                  }
                }
                System.err.println(String.format("droneId(%s), aggressiveDistance(%s), distance(%s), fishId(%s), dir(%s)", droneId, aggressiveDistance, distance, monster.fishId(), dir));
                getAgressiveMonsterNextPos(drone, monster);
              }
            }
          }
        }
    }

    private static Vector getAgressiveMonsterNextPos(Drone drone, Fish monster) {
        System.err.println("------------ AGGRESSIVE MONSTER NEXT POS ------------");
        Vector dronePos = drone.pos();
        Point dronePoint = new Point((int)dronePos.x(), (int)dronePos.y());
        Vector monsterPos = monster.pos();
        Point monsterPoint = new Point((int)monsterPos.x(), (int)monsterPos.y());
        double heading = getHeading(monsterPoint, dronePoint);
        Point nextMonsterPos = getHeadingEndPoint(monsterPoint, (int) heading, 540);
        //  getNextDronePosss(dronePoint, nextMonsterPos);

        return new Vector(nextMonsterPos.x(), nextMonsterPos.y());
    }

    public static double getHeading(Point p1, Point p2) {
        double a = -Math.atan2(p1.y() - p2.y(), p1.x() - p2.x());
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
        x = (int)source.x() + (int) Math.round( (Math.sin(Math.toRadians(heading)) * distance) );
        y = (int)source.y() + (int) Math.round( (Math.cos(Math.toRadians(heading)) * distance) );
        return new Point(x, y);
    }

    public static Map<String, Point> getNextDronePosss(Point drone, Point nextMonsterPos) {
        Map<String, Point> nextDronePossibilities = new HashMap<>();
        nextDronePossibilities.put(T_HEADING_DIR,  getHeadingEndPoint(drone, T_HEADING_VAL, DRONE_MAX_600));
        nextDronePossibilities.put(TR_HEADING_DIR, getHeadingEndPoint(drone, TR_HEADING_VAL, DRONE_MAX_600));
        nextDronePossibilities.put(R_HEADING_DIR,  getHeadingEndPoint(drone, R_HEADING_VAL, DRONE_MAX_600));
        nextDronePossibilities.put(BR_HEADING_DIR, getHeadingEndPoint(drone, BR_HEADING_VAL, DRONE_MAX_600));
        nextDronePossibilities.put(B_HEADING_DIR,  getHeadingEndPoint(drone, B_HEADING_VAL, DRONE_MAX_600));
        nextDronePossibilities.put(BL_HEADING_DIR, getHeadingEndPoint(drone, BL_HEADING_VAL, DRONE_MAX_600));
        nextDronePossibilities.put(L_HEADING_DIR,  getHeadingEndPoint(drone, L_HEADING_VAL, DRONE_MAX_600));
        nextDronePossibilities.put(TL_HEADING_DIR, getHeadingEndPoint(drone, TL_HEADING_VAL, DRONE_MAX_600));
        //System.out.println(nextDronePossibilities);
        excludeUnsafe(nextDronePossibilities, nextMonsterPos);
        System.out.println(nextDronePossibilities);
        return nextDronePossibilities;
    }

    public static void excludeUnsafe(Map<String, Point> nextDronePossibilities, Point nextMonsterPos) {
        Iterator<Map.Entry<String, Point>> iter = nextDronePossibilities.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry<String, Point> nextDronePossibility = iter.next();
          Point nextDronePos = nextDronePossibility.getValue();
          double distance = Point.calculateDistance(nextDronePos, nextMonsterPos);
          // if (nextDronePos.distance(nextMonsterPos) < 800) {
          if (distance < 800) {
            iter.remove();
          }
        }
    }














}
