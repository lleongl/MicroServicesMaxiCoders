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

class Player {
        public static boolean downLeft = false;
        public static    boolean downRight = false;
         public static   boolean downCenter = false;
        public static    boolean goUp = false;
        public static int turnNumber = 0 ;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        Map<Integer, FishDetail> fishDetails = new HashMap<>();

        int fishCount = in.nextInt();
        for (int i = 0; i < fishCount; i++) {
            int fishId = in.nextInt();
            int color = in.nextInt();
            int type = in.nextInt();
            fishDetails.put(fishId, new FishDetail(color, type));
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

            

            String order = "wait 0 no order inputed";
            boolean putTheLightOn = Player.turnNumber % 3 == 0;
            boolean firstDrone = true;
            // System.out.println(String.format("MOVE %d %d %d", targetX, targetY, light));
            for (Drone drone : myDrones) {
                int x = drone.pos().x();
                int y = drone.pos().y();
                System.err.println(String.format("go up: %b ", Player.goUp));
                if(y > 8900 || Player.goUp  ){
                    Player.goUp = true;  
                    if(Vector.calculateDistance(drone.pos(), new Vector(5000,300)) < 200){                        
                        Player.goUp = false;                        
                    }else{
                        if (putTheLightOn) 
                            order = new String("MOVE 5000 300 0");
                        else
                            order =  new String("MOVE 5000 300 1");
                    }
                }

                if(firstDrone == true && Player.goUp == false){
                    
                    if(Vector.calculateDistance(drone.pos(), new Vector(2700,9000)) < 1000){
                        Player.goUp = true;                        
                    }else{
                        if (putTheLightOn) 
                            order = new String("MOVE 2700 9000 0");
                        else
                            order =  new String("MOVE 2700 9000 1");
                    }
                }
               
                if(firstDrone == false && Player.goUp == false){
                    if(Vector.calculateDistance(drone.pos(), new Vector(7400,8000)) < 1000){
                        Player.downCenter = true;
                        Player.goUp = true;                        
                    }else{
                        if (putTheLightOn) 
                            order = new String("MOVE 7400 9000 0");
                        else
                            order =  new String("MOVE 7400 9000 1");
                    }
    
                }
                firstDrone = false;
                 System.out.println(order);          
                
                
            }
        }
    }
}

