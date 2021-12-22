package examplefuncsplayer;
import battlecode.common.*;

import javax.xml.stream.Location;
import java.awt.*;
import java.util.Arrays;

public strictfp class RobotPlayer {
    static RobotController rc;

    static Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST
    };

    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static int turnCount;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        turnCount = 0;

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You can add the missing ones or rewrite this into your own control structure.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                switch (rc.getType()) {
                    case HQ:                 runHQ();                break;
                    case MINER:              runMiner();             break;
                    case REFINERY:           runRefinery();          break;
                    case VAPORATOR:          runVaporator();         break;
                    case DESIGN_SCHOOL:      runDesignSchool();      break;
                    case FULFILLMENT_CENTER: runFulfillmentCenter(); break;
                    case LANDSCAPER:         runLandscaper();        break;
                    case DELIVERY_DRONE:     runDeliveryDrone();     break;
                    case NET_GUN:            runNetGun();            break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }

    static void runHQ() throws GameActionException {
        if (!check_if_nearby(RobotType.MINER)) {
            tryBuild(RobotType.MINER, Direction.NORTH);
        }

        /*
        int [] location_arr = rc.getLocation();
        int x = location_arr[0];
        int y = location_arr[0];

        String location_message = "999";

        if (x<10) {
            location_message += "0"+Integer.toString(x);
        }

        else {
            location_message += x;
        }

        if (y<10) {
            location_message += "0"+Integer.toString(y);
        }

        else {
            location_message += y;
        }

        int location_int = String.toInt(location_message);

        if (rc.canSubmitTransaction() {
            rc.submitTransaction();
        }
         */


    }

    static void runMiner() throws GameActionException {

        if (!check_if_nearby(RobotType.DESIGN_SCHOOL)) {
            tryBuild(RobotType.DESIGN_SCHOOL, Direction.EAST);
        }

        else {
            for (Direction dir : directions)
                if (tryRefine(dir))
                    System.out.println("I refined soup! " + rc.getTeamSoup());
            for (Direction dir : directions)
                if (tryMine(dir))
                    System.out.println("I mined soup! " + rc.getSoupCarrying());
        }


        // need to write exploring routine .. soup can be refined at hq


        // cool func: rc.senseNearbySoup()
        // probably want to run senseSoup(MapLocation loc) to go to most soups (or develop priority algo)



        /*

        tryBlockchain();
        tryMove(randomDirection());
        if (tryMove(randomDirection()))
            System.out.println("I moved!");
        // tryBuild(randomSpawnedByMiner(), randomDirection());
        for (Direction dir : directions)
            tryBuild(RobotType.FULFILLMENT_CENTER, dir);
        for (Direction dir : directions)
            if (tryRefine(dir))
                System.out.println("I refined soup! " + rc.getTeamSoup());
        for (Direction dir : directions)
            if (tryMine(dir))
                System.out.println("I mined soup! " + rc.getSoupCarrying());
        */
    }

    static void runDesignSchool() throws GameActionException {
        // first check if there is a landscaper nearby
        if (!check_if_nearby(RobotType.LANDSCAPER)) {
            tryBuild(RobotType.LANDSCAPER, Direction.NORTHEAST);
        }
    }

    static void runLandscaper() throws GameActionException {

        MapLocation hq_loc = null;

        // first check if hq within range

        if (check_if_nearby(RobotType.HQ)) {
            MapLocation search_result = get_robot_location(RobotType.HQ);
            if (!search_result.equals(null)) {
                hq_loc = search_result;
            }
        }





        /*
        // need to check if the ne tile can be dug at, if not check others.
        // fix below blob
        if (rc.getDirtCarrying()==0) {
            while (rc.getDirtCarrying()<12) {
                if (rc.canDigDirt(Direction.NORTHEAST)) {
                    rc.digDirt(Direction.NORTHEAST);
                }
            }
        }
        */

        /*
        rc.digDirt(Direction.NORTHEAST);
        rc.depositDirt(Direction.CENTER);
        rc.move(Direction.WEST);
        rc.digDirt(Direction.NORTHEAST);
        rc.depositDirt(Direction.CENTER);
        rc.move(Direction.WEST);
        rc.digDirt(Direction.NORTHEAST);
        rc.depositDirt(Direction.CENTER);
        rc.move(Direction.WEST);
        rc.move(Direction.CENTER.rotateLeft());
        rc.move(Direction.CENTER.rotateLeft());
        */


        // need to check cooldown
        // need to move to locations in square around HQ (so HQ needs to communicate it's location)
        // perhaps I can do this with rc.getBlock() which can communicate HQ location
        // desposit dirt with rc.depositDirt()
        // dig dirt with rc.digDirt()
        // dirt carrying limit is 25

    }

    ///// MY CUSTOM STUFF /////

    public static int[] manhattan_step(int x1, int x2, int y1, int y2) {
        int x_dif = x2 - x1;
        int y_dif = y2-y1;

        int[] action_arr = {0,0};

        if (x_dif>0) {
            action_arr[0]=1;
        }

        else if (x_dif<0) {
            action_arr[0]=-1;
        }

        else if (y_dif>0) {
            action_arr[1]=1;
        }

        else if (y_dif<0) {
            action_arr[1]=-1;
        }

        return action_arr;
    }

    // this function checks if a particular robot type is within a robot's vision range
    // add feature so it returns coordinate?
    public static boolean check_if_nearby(RobotType robot_to_check) {
        boolean type_is_nearby = false;
        RobotInfo [] nearby_info = rc.senseNearbyRobots();
        for (RobotInfo info : nearby_info) {
            RobotType test_type = info.type;
            if (test_type.equals(robot_to_check)) {
                type_is_nearby = true;
                break;
            }
        }

        return type_is_nearby;
    }

    // returns the location of the first occurance of a robot within range
    public static MapLocation get_robot_location(RobotType robot_to_check) {
        MapLocation target_location = null;
        RobotInfo [] nearby_info = rc.senseNearbyRobots();
        for (RobotInfo info : nearby_info) {
            RobotType test_type = info.type;
            if (test_type.equals(robot_to_check)) {
                target_location = info.getLocation();
                break;
            }
        }

        return target_location;
    }

    //////////////////////////////////// STUFF I HAVE NOT WORKED WITH /////////////////

    static void runRefinery() throws GameActionException {
        // System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
    }

    static void runVaporator() throws GameActionException {

    }


    static void runFulfillmentCenter() throws GameActionException {
        //for (Direction dir : directions)
        //    tryBuild(RobotType.DELIVERY_DRONE, dir);
    }


    static void runDeliveryDrone() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within capturing range
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0) {
                // Pick up a first robot within range
                rc.pickUpUnit(robots[0].getID());
                System.out.println("I picked up " + robots[0].getID() + "!");
            }
        } else {
            // No close robots, so search for robots within sight radius
            tryMove(randomDirection());
        }
    }

    static void runNetGun() throws GameActionException {

    }

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
    }

    static boolean tryMove() throws GameActionException {
        for (Direction dir : directions)
            if (tryMove(dir))
                return true;
        return false;
        // MapLocation loc = rc.getLocation();
        // if (loc.x < 10 && loc.x < loc.y)
        //     return tryMove(Direction.EAST);
        // else if (loc.x < 10)
        //     return tryMove(Direction.SOUTH);
        // else if (loc.x > loc.y)
        //     return tryMove(Direction.WEST);
        // else
        //     return tryMove(Direction.NORTH);
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMineSoup(dir)) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }


    static void tryBlockchain() throws GameActionException {
        if (turnCount < 3) {
            int[] message = new int[7];
            for (int i = 0; i < 7; i++) {
                message[i] = 123;
            }
            if (rc.canSubmitTransaction(message, 10))
                rc.submitTransaction(message, 10);
        }
        // System.out.println(rc.getRoundMessages(turnCount-1));
    }
}
