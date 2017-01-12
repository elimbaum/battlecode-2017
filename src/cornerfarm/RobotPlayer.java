package cornerfarm;
import battlecode.common.*;


public class RobotPlayer {
	static RobotController rc;
	
	public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;

        switch (rc.getType()) {
            case ARCHON:
                runArchon();
                break;
            case GARDENER:
                runGardener();
                break;
            case SOLDIER:
                runSoldier();
                break;
            case LUMBERJACK:
                runLumberjack();
                break;
            case SCOUT:
            	break;
            case TANK:
            	break;
            default:
            	break;
        }
	}
        
    static void runArchon() throws GameActionException {
        while (true) {
            try {


            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }
    
    static void runGardener() throws GameActionException {
        while (true) {
            try {


            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }
    
    static void runSoldier() throws GameActionException {
        while (true) {
            try {


            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }
    
    static void runLumberjack() throws GameActionException {
        while (true) {
            try {


            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }
}
