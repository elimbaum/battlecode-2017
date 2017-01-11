// vv Less good bot below vv
package helloworldeli;
import battlecode.common.*;

public strictfp class RobotPlayer {
	static RobotController rc;
	
	public static void run(RobotController rc) throws GameActionException
	{
		RobotPlayer.rc = rc;
		
		switch(rc.getType())
		{
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
				break;
			case SCOUT:
				break;
			case TANK:
				break;
		}
	}
	
	static void runArchon() throws GameActionException
	{
		while (true)
		{
			try
			{
				Direction dir = randomDirection();
				
				if(rc.canMove(dir))
				{
					rc.move(dir);
				}
				
				if(rc.canHireGardener(dir.opposite()))
				{
					rc.hireGardener(dir.opposite());
				}
				
				Clock.yield();
			}
			catch (Exception e)
			{
				System.out.println("Archon died");
				e.printStackTrace();
			}
		}
	}
	
	static void runGardener() throws GameActionException
	{
		while (true)
		{
			try
			{
				Direction dir = randomDirection();
				
				if(rc.canPlantTree(dir.opposite()) && Math.random() < 0.1)
				{
					rc.plantTree(dir.opposite());
				}
				else if(rc.canBuildRobot(RobotType.SOLDIER, dir.opposite()))
				{
					rc.buildRobot(RobotType.SOLDIER, dir.opposite());
				}
				
				// tree maintenance
				TreeInfo[] trees = rc.senseNearbyTrees(-1, rc.getTeam());
				
				for(TreeInfo t : trees)
				{	
					if (rc.canWater(t.ID))
					{
						rc.water(t.ID);
						//System.out.println("Watered " + t.ID);
					}
					if (rc.canShake(t.ID))
					{
						rc.shake(t.ID);
						//System.out.println("Shook " + t.ID);
					}
				}
				
				if(rc.canMove(dir))
				{
					rc.move(dir);
				}
				
				Clock.yield();
			}
			catch (Exception e)
			{
				System.out.println("Gardener died");
				e.printStackTrace();
			}
		}
	}
	
	static void runSoldier() throws GameActionException
	{
		Team enemy = rc.getTeam().opponent();
		
		while(true)
		{
			try
			{
				MapLocation myLoc = rc.getLocation();
				
				RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
				
				Direction dir;
				if (robots.length > 0)
				{
					dir = myLoc.directionTo(robots[0].location);
					if (rc.canFireTriadShot())
					{
						rc.fireTriadShot(dir);
						dir.rotateLeftDegrees(15);
					}
					
				}
				else
				{
					dir = randomDirection();
				}
				
				if(rc.canMove(dir))
				{
					rc.move(dir);
				}
				Clock.yield();
			}
			catch (Exception e)
			{
				System.out.println("Solider died.");
				e.printStackTrace();
			}
		}
	}
	
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }
}
