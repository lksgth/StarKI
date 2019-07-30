package de.uni_passau.fim.sommercamp.sc2.bots._2019;

        import com.github.ocraft.s2client.protocol.data.Units;
        import de.uni_passau.fim.sommercamp.sc2.bots.AbstractBot;
        import de.uni_passau.fim.sommercamp.sc2.bots.Unit;
        import de.uni_passau.fim.sommercamp.sc2.bots.util.Vec2;

        import java.util.ArrayList;
        import java.util.List;

/**
 * Empty bot for the Sommercamp SC2 interface.
 */
public class StarKIBot extends AbstractBot {

    String name;
    List<Unit> workers;
    Vec2 enemyLocation;
    Unit myScout;
    Boolean scouting;
    Boolean scoutNextToTeam;

    /**
     * This constructor is called by the framework. Extend it with all necessary setup, other constructors won't work.
     */
    public StarKIBot() { name = "Empty Bot"; }

    /**
     * Get Unit methods
    */

    private List<Unit> getEnemyMedics() {
        List<Unit> medics = new ArrayList<>();
        for (Unit medic: getEnemyUnits()) {
            if (medic.canHeal() && medic.isAliveAndVisible()) {
                medics.add(medic);
            }
        }

        return medics;
    }

    private List<Unit> getMyMedics() {
        List<Unit> medics = new ArrayList<>();
        for (Unit medic: getMyUnits()) {
            if (medic.canHeal() && medic.isAliveAndVisible()) {
                medics.add(medic);
            }
        }

        return medics;
    }

    private List<Unit> getMyTanks(){
        List<Unit> tanks = new ArrayList<>();
        for (Unit tank: getMyUnits()) {

            if(tank.getType() == Units.TERRAN_FIREBAT && tank.isAliveAndVisible()){
                tanks.add(tank);
        }
        }
        return tanks;
    }

    private List<Unit> getMyBigTanks(){
        List<Unit> bigTanks = new ArrayList<>();
        for (Unit tank: getMyUnits()) {

            if(tank.getType() == Units.TERRAN_MARAUDER &&  tank.isAliveAndVisible()){
                bigTanks.add(tank);
            }
        }
        return bigTanks;
    }

    private List<Unit> getMySoldiers(){
        List<Unit> soldiers = new ArrayList<>();
        for (Unit soldier: getMyUnits()) {

            if(soldier.getType() == Units.TERRAN_MARINE && soldier.isAliveAndVisible()){
                soldiers.add(soldier);
            }
        }
        return soldiers;
    }

    /**/

    private Boolean foundEnemy() {
        return getEnemyUnits().size() > 0 ? true : false;
    }

    /**
     * Scout methods
     */

    private void pickScout() {
        // Check if at least one bigTank is alive and pick it as a scout
        // Else if pick a normal tank
        // Else if pick a marine
        if (getMyBigTanks().size() > 0) {
            myScout = getMyBigTanks().get(0);
        } else if (getMyTanks().size() > 0) {
            myScout = getMyTanks().get(0);
        } else {
            myScout = getMySoldiers().get(0);
        }
    }

    private void scout() {
        pickScout();

        myScout.move(getRandomPointOnMap());
        scouting = true;
    }

    private void returnScoutToTeam() {
        printDebugString("Scout returns to Team.");

        // Clear order queue of scout
        myScout.stop();

        printDebugString("Cleared orders of Scout.");

        myScout.move(getMyMedics().get(0).getPosition());

        scoutNextToTeam = false;
    }

    private boolean scoutNearTeam() {
        Boolean nearTeam = false;

        List<Float> teamPosition = new ArrayList();
        if (getMyMedics().size() > 0) {
            teamPosition.add(0, getMyMedics().get(0).getPosition().getX());
            teamPosition.add(1, getMyMedics().get(0).getPosition().getY());
        } else if (getMySoldiers().size() > 0) {
            teamPosition.add(0, getMySoldiers().get(0).getPosition().getX());
            teamPosition.add(1, getMySoldiers().get(0).getPosition().getY());
        } else if (getMyTanks().size() > 0) {
            teamPosition.add(0, getMyTanks().get(0).getPosition().getX());
            teamPosition.add(1, getMyTanks().get(0).getPosition().getY());
        } else if (getMyBigTanks().size() > 0) {
            teamPosition.add(0, getMyBigTanks().get(0).getPosition().getX());
            teamPosition.add(1, getMyBigTanks().get(0).getPosition().getY());
        }

        if (teamPosition.get(0) - myScout.getPosition().getX() < 2.5 || teamPosition.get(0) - myScout.getPosition().getX() > -2.5) {
            if (teamPosition.get(1) - myScout.getPosition().getY() < 2.5 || teamPosition.get(1) - myScout.getPosition().getY() > -2.5) {
                printDebugString("Scout is near Team.");
                return true;
            }
        }

        return nearTeam;
    }

    /* */

    /**
     * This method is called every step by the framework. The game loop consists of calling this method for every bot
     * and executing the invoked actions inside the game.
     */
    @Override
    protected void onStep() {

        // Get list of units and store list in "workers"
        // Only in the first GameLoop
        if (getGameLoop() == 1) {
            workers = getMyUnits();

            scoutNextToTeam = true;
        }

        if (!foundEnemy() && scoutNextToTeam == true) {
            if (getGameLoop() % 100 == 1) {
                scout();
            }
        } else {
            if (scouting) {
                scouting = false;

                // Push enemyCoordinates
                enemyLocation = myScout.getPosition();

                returnScoutToTeam();

                if (scoutNearTeam()) {
                    printDebugString("Scout is back Home!");
                }
            }
        }

        Boolean egal = scoutNearTeam();
    }
}
