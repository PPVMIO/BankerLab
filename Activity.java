import java.util.*;

public class Activity
{
    private String actName;				// String containing instruction command
    private int task;				// task used
    private int resource;			// resource used
    private int units;				// units needed
    private int cycles;				// cycles needed
    private boolean termCounted;		// Where this instruction was counted or not

    private int timeTaken;			// total time task took
    private int[] initClaim;			// initial claim of the process
    private int[] currClaim;			// current claim of the process
    private int[] need;
    private boolean isSafe;
    private boolean mustAbort;

    private int waitingTime;            // total time task waited
    private boolean isBlocked;      // total time task blocked
    private int timeBlocked;            // total time blocked
    private ArrayList<Integer> resourcesUsed;    // Contains which resources were used for each resource
    private boolean justReleased;   // Whether or not this process was just unblocked
    private boolean lastWasCompute;  // Whether or not the last value was "compute"

    private boolean exceedsClaim;


    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public int getCycles() {
        return cycles;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    public boolean isTermCounted() {
        return termCounted;
    }

    public void setTermCounted(boolean termCounted) {
        this.termCounted = termCounted;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public int[] getInitClaim() {
        return initClaim;
    }

    public void setInitClaim(int[] initClaim) {
        this.initClaim = initClaim;
    }

    public int[] getCurrClaim() {
        return currClaim;
    }

    public void setCurrClaim(int[] currClaim) {
        this.currClaim = currClaim;
    }

    public int[] getNeed() {
        return need;
    }

    public void setNeed(int[] need) {
        this.need = need;
    }


    public boolean isSafe() {
        return isSafe;
    }

    public void setIsSafe(boolean isSafe) {
        this.isSafe = isSafe;
    }

    public boolean isMustAbort() {
        return mustAbort;
    }

    public void setMustAbort(boolean mustAbort) {
        this.mustAbort = mustAbort;
    }

    public int getWaitingTime() {
        return waitingTime;
    }


    public boolean isBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public int getTimeBlocked() {
        return timeBlocked;
    }

    public void setTimeBlocked(int timeBlocked) {
        this.timeBlocked = timeBlocked;
    }

    public ArrayList getResourcesUsed() {
        return resourcesUsed;
    }

//    public void setResourcesUsed(ArrayList resourcesUsed) {
//        this.resourcesUsed = resourcesUsed;
//    }

    public boolean isJustReleased() {
        return justReleased;
    }

    public void setJustReleased(boolean justReleased) {
        this.justReleased = justReleased;
    }

    public boolean isLastWasCompute() {
        return lastWasCompute;
    }

    public void setLastWasCompute(boolean lastWasCompute) {
        this.lastWasCompute = lastWasCompute;
    }

    public boolean isExceedsClaim() {
        return exceedsClaim;
    }

    public void setExceedsClaim(boolean exceedsClaim) {
        this.exceedsClaim = exceedsClaim;
    }

    public void increaseWaitingTime(){
        this.waitingTime++;
    }

    public void decreaseCycle(){
        this.cycles--;
    }




    public Activity(int numResources){
        actName = "";
        task = 0;
        resource = 0;
        termCounted = false;
        timeTaken = 0;
        waitingTime = 0;
        isBlocked = false;
        resourcesUsed = new ArrayList<Integer>();
        lastWasCompute = false;
        isSafe = false;
        exceedsClaim = false;
        for(int i = 0; i < numResources; i++)
            resourcesUsed.add(new Integer(0));
    }

//    /**
//     * Use for debugging
//     */
//    public void printActivity(){
//        System.out.println("actName: " + actName);
//        System.out.println("task: " + task);
//        System.out.println("Resource Number: " + resource);
//        System.out.println("Units: " + units);
//
//
//    }
}