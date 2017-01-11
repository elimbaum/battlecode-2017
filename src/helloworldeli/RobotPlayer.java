package helloworldeli;
import battlecode.common.*;

public class RobotPlayer {
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
				
//				if(rc.canHireGardener(dir.opposite()))
//				{
//					rc.hireGardener(dir.opposite());
//				}
				
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
				
				if(rc.canMove(dir))
				{
					rc.move(dir);
				}
				
				if(rc.canPlantTree(dir.opposite()))
				{
					rc.plantTree(dir.opposite());
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
	
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }
}
