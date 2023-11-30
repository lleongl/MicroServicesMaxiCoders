import java.util.*;

// Define the data structures as records
record Vector(int x, int y) {
    static double calculateDistance(Vector point1, Vector point2) {
        int deltaX = point2.x() - point1.x();
        int deltaY = point2.y() - point1.y();

        // Applying the Euclidean distance formula: sqrt((x2 - x1)^2 + (y2 - y1)^2)
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
}

record FishDetail(int color, int type) {}

record Fish(int fishId, Vector pos, Vector speed, FishDetail detail) {}

record Drone(int droneId, Vector pos, boolean dead, int battery, List<Integer> scans) {}

record RadarBlip(int fishId, String dir) {}

 class DroneState{
    public boolean goCenter = false;
    public boolean goUp = false;
    public boolean chaseFish = false;
    public boolean findFish = false;
    public boolean fleeMonster = false;
    public int lastLight = 0 ;

    public DroneState(boolean first){

    }

    public void setGoCenter(boolean b){goCenter = b;}
    public void setGoUp(boolean b){goUp = b;}
    public void setChaseFish(boolean b){chaseFish = b;}
    public void setFindFish(boolean b){findFish = b;}

    public void resetState(){
        goCenter = false;
        goUp = false;
        chaseFish = false;
        findFish = false;
        fleeMonster = false;
    }

    public boolean noState(){
        return !goCenter && !goUp && !chaseFish && !findFish && !fleeMonster;
    }
}



class Player {
    public static DroneState firstDroneState = new DroneState(true);
    public static DroneState secondDroneState = new DroneState(false);
    public static int turnNumber = 0 ;

    static Map<Integer,Set<Integer>> combinationsColors = new HashMap<>();
    static Map<Integer,Set<Integer>> combinationsTypes = new HashMap<>();
    static Map<Integer,Set<Integer>> combinationsColorsSavedFishesMissing;
    static Map<Integer,Set<Integer>> combinationsTypesSavedFishesMissing;

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
            combinationsColorsSavedFishesMissing =  new HashMap<>(combinationsColors);
            combinationsTypesSavedFishesMissing = new HashMap<>(combinationsTypes);
            printMissingCombinations();
            

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
            updateCombinationsFishesMissing( myScans, fishDetails);
            
            String order = "wait 0 no order inputed";
            int lightNumber = Player.turnNumber % 3 == 0 ? 1 : 0; 
            // System.out.println(String.format("MOVE %d %d %d", targetX, targetY, light));

            for (Drone drone : myDrones) {
                int x = drone.pos().x();
                int y = drone.pos().y();
                if (y < 1500) lightNumber =0;

                DroneState ds = drone.droneId() ==0 || drone.droneId() == 1 ? Player.firstDroneState : Player.secondDroneState;
                System.err.println(String.format("go up: %b ", ds.goUp));


                for(Fish f: visibleFishes){
                    if(f.detail().color() == -1 && Vector.calculateDistance(drone.pos(),f.pos()) < 2000){
                        ds.fleeMonster = true;
                        lightNumber = 0;
                        System.err.println(String.format("Detecting monster !!!!  %b ", f.fishId() ) );
                    }
                }

                if( shouldResurface ( myDrones, fishDetails) || ds.fleeMonster){
                    if(Vector.calculateDistance(drone.pos(), new Vector(x,300)) < 200){                        
                        ds.resetState();                        
                    }else{
                        if(!ds.fleeMonster)
                            order = String.format("MOVE %d 300 %d going up !!!", x ,lightNumber);
                        else
                            order = String.format("MOVE %d 300 %d Detecting monster !!!", x ,lightNumber);
                        
                    }
                }else{
                    //no fleeing, no scanning, trying to find fish
                    int targetedY = 9000;

                    if(!Player.combinationsTypesSavedFishesMissing.get(0).isEmpty() ) {
                        targetedY = 4000;
                    }
                    if(!Player.combinationsTypesSavedFishesMissing.get(1).isEmpty() ) {
                        targetedY =  6500;
                    }

                    int targetedX = 5000;                    

                    if(ds.goCenter){  
                        if(x > 4500 && x < 5500 ){
                            ds.resetState();                    
                        }else{
                            targetedX = 5000;
                        }
                    }

                    if(ds.noState() && x < 6000){  
                        if(x < 1200){
                            ds.resetState();  
                            ds.setGoCenter(true);                    
                        }else{
                            targetedX = 1000;
                        }
                    }
                
                    if(ds.noState() && x >= 4000){  
                        if(x > 8800){
                            ds.resetState();   
                            ds.setGoCenter(true);                     
                        }else{
                            targetedX = 9000;

                        }   
                    }

                    order = String.format("MOVE %d %d %d",targetedX, targetedY, lightNumber);
                }
                ds.lastLight=lightNumber;
                System.out.println(order);            
                
            }
        }
    }




  public static void printMissingCombinations(){
    System.err.println("------------ MISSING COMBINATIONS TYPES ------------");
    for (int type: combinationsTypesSavedFishesMissing.keySet()) {
      System.err.print("Type " + type + ": ");
      for (int id: combinationsTypesSavedFishesMissing.get(type)) {
        System.err.print(id + ", ");
      }
      System.err.println();
    }

    System.err.println("------------ MISSING COMBINATIONS COLORS ------------");
    for (int color: combinationsColorsSavedFishesMissing.keySet()) {
      System.err.print("Color " + color + ": ");
      for (int id: combinationsColorsSavedFishesMissing.get(color)) {
        System.err.print(id + ", ");
      }
      System.err.println();
    }
  }

   public static void updateCombinationsFishesMissing(List<Integer> myScans, Map<Integer, FishDetail> fishDetails) {
    for (int fishId: myScans) {
      FishDetail fish = fishDetails.get(fishId);
      combinationsColorsSavedFishesMissing.get(fish.color()).remove(fishId);
      combinationsTypesSavedFishesMissing.get(fish.type()).remove(fishId);
    }

    // System.err.println("UPDATE!!");
    // printMissingCombinations();
  }

  public static boolean shouldResurface (List<Drone> myDrones, Map<Integer, FishDetail> fishDetails) {
    Map<Integer, Set<Integer>> missingFishesColors = new HashMap<>(combinationsColorsSavedFishesMissing);
    Map<Integer, Set<Integer>> missingFishesTypes = new HashMap<>(combinationsTypesSavedFishesMissing);

    boolean res = false;
    for (Drone drone: myDrones) {
      for (int scannedFishId: drone.scans()) {
        FishDetail fish = fishDetails.get(scannedFishId);
        boolean removeColorRes = missingFishesColors.get(fish.color()).remove(scannedFishId);
        if (removeColorRes) {
          System.err.println("Removed Color + " + scannedFishId);
        }

        boolean removeTypeRes = missingFishesTypes.get(fish.type()).remove(scannedFishId);
        if (removeTypeRes) {
          System.err.println("Removed type + " + scannedFishId);
        }

        if ((removeColorRes && missingFishesColors.get(fish.color()).isEmpty())
            || (removeTypeRes && missingFishesTypes.get(fish.type()).isEmpty())) {

          //TODO check foe hasn't completed the same combination
          res =  true;
        }
      }
    }

    printMissingCombinations();

    res = false;
    return res;
  }

  public int computeScore(List<Drone> myDrones) {
    int res = 0;

    return res;
  }

  public Vector findFish() {
    Vector res = new Vector(0,0);

    return res;
  }
}
