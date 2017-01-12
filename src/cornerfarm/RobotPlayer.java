package cornerfarm;
import battlecode.common.*;

/* General strategy:
 * 
 * Move Archon(s) into corner. Build a protective farm around them.
 */
public class RobotPlayer {
	static RobotController rc;

	
	// Radio Channels
	static final int CH_ARCHON_X = 0;
	static final int CH_ARCHON_Y = CH_ARCHON_X + 1;
	static final int CH_ARCHON_DIR = CH_ARCHON_Y + 1;
	
	static final int ARCHON_MODE_CORNER = 0;
	static final int ARCHON_MODE_FARM = 1;
	static final int CH_ARCHON_MODE = 3;
	
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
            	runScout();
            	break;
            case TANK:
            	runTank();
            	break;
            default:
            	break;
        }
	}
        
    static void runArchon() throws GameActionException {
        while (true) {
            try {
            	shakeTrees();
            	
            	// first, find the corner.
            	Direction dir = Direction.getWest();
            	
            	rc.broadcast(CH_ARCHON_MODE, ARCHON_MODE_FARM);
            	
//            	for(int i = 0; i < 360; i += Math.random() * 10)
//            	{
//            		if(rc.canMove(dir))
//            		{
//            			rc.move(dir);
//            			break;
//            		}
//            		dir = dir.rotateLeftDegrees(i);
//            	}
//            	if(! rc.hasMoved())
//            	{
//            		System.out.println("done moving");
//            		rc.broadcast(CH_ARCHON_MODE, ARCHON_MODE_FARM);
//            	}
            	
            	MapLocation myLoc = rc.getLocation();
            	rc.broadcast(CH_ARCHON_X, (int)myLoc.x);
            	rc.broadcast(CH_ARCHON_Y, (int)myLoc.y);
            	
            	if(rc.isBuildReady())
            	{
                	dir = randomDirection();
                	if (rc.canHireGardener(dir))
                	{
                		rc.hireGardener(dir);
                	}
            	}

            	Clock.yield();
            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }
    
    static void runGardener() throws GameActionException {
        boolean plsmove = true;
        
        MapLocation gridLoc = rc.getLocation();
        gridLoc = gridLoc.translate(- (gridLoc.x % 5), - (gridLoc.y % 5));
        gridLoc = gridLoc.translate(15 - (3 * (int) (10 * Math.random())),
        							15 - (3 * (int) (10 * Math.random())));
    	System.out.println(gridLoc);
    	while (true) {
            try {
            	shakeTrees();
            	
            	// while archon is finding corner, track with it and protect (soldier)
            	MapLocation archonLoc = new MapLocation(rc.readBroadcast(CH_ARCHON_X),
            											rc.readBroadcast(CH_ARCHON_Y));
            	int archonMode = rc.readBroadcast(CH_ARCHON_MODE);
            	
            	if (archonMode == ARCHON_MODE_CORNER)
            	{
            		System.out.println("corner mode!");
            		if(rc.isBuildReady())
            		{
            			// build new bodyguards
            			Direction dir = randomDirection();
            			
            			if(rc.canBuildRobot(RobotType.SOLDIER, dir))
            			{
            				//rc.buildRobot(RobotType.SOLDIER, dir);
            			}
            			else
            			{
            				System.out.println("bad direction");
            			}
            		}
            		
            		// Move towards archon
    				Direction moveDir = rc.getLocation().directionTo(archonLoc);
    				
    				if(rc.canMove(moveDir))
    				{
    					rc.move(moveDir);
    				}
            	}
            	else
            	{
            		if(! plsmove)
            		{
            			Clock.yield();
            			continue;
            		}
            		
            		// Plant on a 5 x 5 grid
            		if(rc.canMove(gridLoc))
            		{
            			rc.move(gridLoc);
            			if(rc.getLocation().equals(gridLoc))
                		{
            				plsmove = false;
//            				if(rc.canPlantTree(Direction.getWest()))
//            				{
//            					System.out.println("planting on" + gridLoc);
//            					rc.plantTree(Direction.getWest());
//            				}
                		}
            		}
            		else
            		{
            			//System.out.println("random movement");
            			Direction dir = randomDirection();
            			if(rc.canMove(dir))
                		{
                			rc.move(dir);
                		}
            		}
            		
            		
            	}
            	
            	// tree maintenance
            	TreeInfo[] trees = rc.senseNearbyTrees(-1, rc.getTeam());
				for(TreeInfo t : trees)
				{	
					if (rc.canWater(t.ID))
					{
						rc.water(t.ID);
					}
				}
				
				

            	Clock.yield();
            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }
    
    static void runSoldier() throws GameActionException {
        while (true) {
            try {
            	shakeTrees();


            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }
    
    static void runLumberjack() throws GameActionException {
        while (true) {
            try {
            	shakeTrees();


            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }
    
    static void runScout() throws GameActionException {
        while (true) {
            try {
            	shakeTrees();

            } catch (Exception e) {
                System.out.println("Scout Exception");
                e.printStackTrace();
            }
        }
    }
    
    static void runTank() throws GameActionException {
        while (true) {
            try {
            	shakeTrees();


            } catch (Exception e) {
                System.out.println("Tank Exception");
                e.printStackTrace();
            }
        }
    }
    
    static void shakeTrees() throws GameActionException {
    	try {
            TreeInfo[] trees = rc.senseNearbyTrees(-1, Team.NEUTRAL);
    		for(TreeInfo t : trees)
    		{
    			if (rc.canShake(t.ID) && t.containedBullets > 0)
    			{
    				rc.shake(t.ID);
    				System.out.println("shook " + t.containedBullets + " bullets");
    			}
    		}
    	} catch (Exception e) {
    		System.out.println("Shook Exception");
    		e.printStackTrace();
    	}
    }

    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }
}
