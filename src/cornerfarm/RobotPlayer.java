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
            	
            	if(rc.isBuildReady() && Math.random() < 0.01)
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
        boolean newLocationRequest = false;
        
        MapLocation gridLoc = rc.getLocation();
        gridLoc = gridLoc.translate(- (gridLoc.x % 5), - (gridLoc.y % 5));
        MapLocation oldLoc = new MapLocation(gridLoc.x, gridLoc.y);
    	System.out.println("Initial: " + gridLoc.toString());
    	
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
    				
    				for(int i = 0; i < 30; i += 5)
    				{
    					if(rc.canMove(moveDir))
        				{
        					rc.move(moveDir);
        					break;
        				}
    				}
    				moveDir = moveDir.rotateRightDegrees(5);
    				
            	}
            	else
            	{
            		// Plant on a 5 x 5 grid
        			MapLocation treeSpot = gridLoc.add(Direction.getSouth(), 2);
            		if(newLocationRequest ||
            				(rc.canSenseAllOfCircle(gridLoc,1) && rc.isCircleOccupiedExceptByThisRobot(gridLoc, 1)) ||
            				(rc.canSenseLocation(treeSpot) && rc.isLocationOccupiedByTree(treeSpot))) {
            			// need new location!
            			
            			System.out.println(gridLoc);
            			oldLoc = gridLoc;
            			gridLoc = nextGridloc(gridLoc, archonLoc);
            			System.out.println(gridLoc);
            			newLocationRequest = false;
            		}
            		
            		if(rc.canSenseAllOfCircle(gridLoc, 1))
            		{
            			if(rc.onTheMap(gridLoc, 1))
            			{
            				rc.setIndicatorDot(gridLoc, 0, 0, 0);
            			}
            			else
            			{
            				newLocationRequest = true;
            				gridLoc = oldLoc;
            			}
            		}
            		      		
            		if(rc.canMove(gridLoc))
            		{
            			rc.move(gridLoc);
            			if(rc.getLocation().equals(gridLoc))
                		{
            				if(rc.isBuildReady() && rc.hasTreeBuildRequirements())
            				{
            					if(rc.canPlantTree(Direction.getSouth()))
                				{
                					System.out.println("planting on" + gridLoc);
                					rc.plantTree(Direction.getSouth());
                				}
            					else
                				{
            						System.out.println("requesting new location");
                					newLocationRequest = true;
                				}
            				}
            				
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
            			//gridLoc = oldLoc;
            			//newLocationRequest = true;
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
    
    static MapLocation nextGridloc(MapLocation current, MapLocation target) throws Exception
    {
    	// Compute weights
    	
    	// Stores CDF (loop across row first, then down column)
    	double[][] weights = new double[3][3];
    	float sumWeights = 0;
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j <= 1; j++)
			{
				double dx = constrain(target.x, current.x - 5, current.x + 5) - (current.x + 5 * i);
				double dy = constrain(target.y, current.y - 5, current.y + 5) - (current.y + 5 * j);
				
				// remaining on current tile is not an option
				if(i == 0 && j == 0)
				{
					System.out.format("Reference dx=%2.0f dy=%2.0f%n", dx, dy);
					continue;
				}
			
				
				// this weight is the PDF for each of the eight directions
				double weight = 0.5 + Math.atan((dx * (current.x - target.x) + dy * (current.y - target.y)) / 100) / Math.PI;
				
				System.out.format("(%2d,%2d) dx=%2.0f dy=%2.0f -> %f%n", i,j,dx,dy,weight);
				sumWeights += weight;
				weights[i + 1][j + 1] = sumWeights;
			}
		}
		
    	// pick a direction!
		double r = Math.random() * sumWeights;
		
		for(int i = -1; i <= 1; i++)
		{
			for(int j = -1; j <= 1; j++)
			{
				if (i == 0 && j == 0) continue;
				
				if (weights[i + 1][j + 1] >= r)
				{
					// found it!
					return current.translate(5 * i, 5 * j);
				}
			}
		}
		
		throw new Exception("CDF Failed");
		
    }
    
    static double constrain(double x, double min, double max)
    {
    	if (x < min) return min;
    	else if(x > max) return max;
    	return x;
    }
}
