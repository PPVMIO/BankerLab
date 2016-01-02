import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by PaulPelayo on 11/22/15.
 */
public class BankerManager {

    private int[] listOfResources;                                       // Array of the current resources
    private int[] initialResources;										// List containing the initial list of resources
    private ArrayList<ArrayList<Activity>> listOfTasks;  	            // ArrayList containing ArrayLists of Activities
    private int taskCount[];                                             // Holds number to represent the current activity for a Task

    public BankerManager(int[] listOfResources, int[] initialResources, ArrayList<ArrayList<Activity>> listOfTasks, int taskCount[]){
        this.listOfResources = listOfResources;
        this.initialResources = initialResources;
        this.listOfTasks = listOfTasks;
        this.taskCount = taskCount;

    }

    /**
     * Prints final results
     */
    public void printBankerReults(){

        Activity orgActivity;
        int result;

        int totalTimeTaken = 0;
        int totalWaitingTime = 0;


        System.out.printf("\t~~~ BANKER ~~~\n");

        for(int i = 0; i < listOfTasks.size();i++){
            orgActivity = (Activity) listOfTasks.get(i).get(0);
            System.out.printf("Task " + orgActivity.getTask() + "\t");
            if(!orgActivity.getActName().equals("aborted")){
                totalTimeTaken += orgActivity.getTimeTaken();
                totalWaitingTime += orgActivity.getWaitingTime();
                result = (int) Math.round((100*((double) orgActivity.getWaitingTime() / (double) orgActivity.getTimeTaken())));
                System.out.printf(orgActivity.getTimeTaken() + "\t" + orgActivity.getWaitingTime() + "\t" + result + "%%\n");
            }
            else{
                System.out.printf("aborted\n");
            }
        }
        System.out.printf("Total\t" + totalTimeTaken + "\t" + totalWaitingTime + "\t" + Math.round(100 * ((double) totalWaitingTime/(double) totalTimeTaken)) + "%%\n\n");
    }
    /**
     * Banker's Algorithm - pessimistic resource manager
     * constantly checks for safe state to see if it can proceed
     */
    public void runBanker(){
        int t = 0;
        int numFinishedTasks = 0;
        boolean isComplete = false;
        Activity activity;
        Activity orgActivity;
        int tempListOfResources[];
        int releaseListOfResouces[];
        int[] numActive = new int[listOfResources.length];
        ArrayList<ArrayList<Activity>> blockedList = new ArrayList<ArrayList<Activity>>();

        Activity tempRootActivity;
        //check for aborted tasks

        for(int i = 0; i < listOfTasks.size();i++){
            tempRootActivity = (Activity) listOfTasks.get(i).get(0);
            for(int j = 0; j < listOfResources.length;j++){
//                System.out.println(initialResources[j]);
                if(tempRootActivity.getInitClaim()[j] > initialResources[j]){
                    tempRootActivity.setActName("aborted");
                }
                checkClaims(i);
            }
        }

        for(ArrayList list : listOfTasks){
            Activity org = (Activity) list.get(0);
            if(org.getActName().equals("aborted"))
                numFinishedTasks++;
        }

        //cycle through activities
        while(!isComplete){

            tempListOfResources = listOfResources.clone();
            releaseListOfResouces = new int[listOfResources.length];
            for(int j = 0; j < blockedList.size();j++){

                orgActivity = (Activity) blockedList.get(j).get(0);

                if(orgActivity.isBlocked() && !orgActivity.getActName().equals("aborted")){
                    activity = (Activity) blockedList.get(j).get(taskCount[j]);


                    //Check for safe state
                    orgActivity.setIsSafe(isSafeState(activity, orgActivity, tempListOfResources));

                    if (orgActivity.isSafe()){
                        orgActivity = (Activity) blockedList.get(0).get(0);
                        activity = (Activity) listOfTasks.get(orgActivity.getTask() - 1).get(taskCount[orgActivity.getTask() -1]);
                        orgActivity.setIsBlocked(false);
                        orgActivity.setActName("notBlocked");
                        orgActivity.setJustReleased(true);
                        tempListOfResources[activity.getResource() - 1] -= activity.getUnits();
                        orgActivity.getCurrClaim()[activity.getResource() - 1] += activity.getUnits();
                        orgActivity.getResourcesUsed().set(activity.getResource() - 1, (Integer) orgActivity.getResourcesUsed().get(activity.getResource() - 1) + activity.getUnits());

                    }
                    else{
                        orgActivity.increaseWaitingTime();

                        if(orgActivity.isMustAbort()){
                            orgActivity.setActName("aborted");

                            blockedList.remove(blockedList.get(j));

                            numActive[activity.getResource() - 1]--;
                            numFinishedTasks++;
                            for(int k = 0; k < listOfResources.length;k++){
                                tempListOfResources[k] += (Integer) orgActivity.getResourcesUsed().get(k);
                            }

                        }
                    }

                }
            }

            for(int i = 0; i < listOfTasks.size();i++){

                orgActivity = (Activity) listOfTasks.get(i).get(0);

                if(!orgActivity.isBlocked() && !orgActivity.getActName().equals("aborted") && !orgActivity.getActName().equals("terminate")){

                    activity = (Activity) listOfTasks.get(i).get(taskCount[i]);

                    if(!activity.getActName().equals("compute") || orgActivity.isLastWasCompute()){
                        taskCount[i]++;
                        orgActivity.setLastWasCompute(false);
                    }

                    if(activity.getActName().equals("initiate")) {
                        numActive[activity.getResource() - 1]++;
                    }

                    else if(activity.getActName().equals("request")){

                        orgActivity.setIsSafe(isSafeState(activity, orgActivity, tempListOfResources));
                        if(!orgActivity.isSafe() && !orgActivity.isJustReleased()){
//                            System.out.println("Not safe");
                            blockedList.add(listOfTasks.get(i));
                            orgActivity.increaseWaitingTime();
                            orgActivity.setIsBlocked(true);
                            orgActivity.setTimeBlocked(t);
                            taskCount[i]--;

                        }
                        else{
                            //safe

                            if(activity.isExceedsClaim()){
                                orgActivity.setActName("aborted");
                                numFinishedTasks++;
                                for(int k = 0; k < listOfResources.length;k++){
                                    orgActivity.getCurrClaim()[k] = 0;
                                }


                            }
                            else{
                                if(orgActivity.isJustReleased()){
                                    blockedList.remove(blockedList.get(0));
                                    orgActivity.setJustReleased(false);
                                    orgActivity.getCurrClaim()[activity.getResource()-1] += activity.getUnits();

                                }
                                else{
                                    tempListOfResources[activity.getResource()-1] -= activity.getUnits();
                                    orgActivity.getCurrClaim()[activity.getResource()-1] += activity.getUnits();
                                }
                                orgActivity.getResourcesUsed().set(activity.getResource()-1, (Integer) orgActivity.getResourcesUsed().get(activity.getResource()-1) + activity.getUnits());


                            }
                        }
                    }
                    else if(activity.getActName().equals("compute")){
                        activity.decreaseCycle();

                        if(activity.getCycles() == 0){
                            taskCount[i]++;
                            orgActivity.setLastWasCompute(true);

                            activity = (Activity) listOfTasks.get(i).get(taskCount[i]);

                            if(activity.getActName().equals("terminate") && !activity.isTermCounted()){
                                numFinishedTasks++;
                                activity.setTermCounted(true);

                                orgActivity.setCurrClaim(new int[listOfResources.length]);
                                orgActivity.setNeed(new int[listOfResources.length]);

                                orgActivity.setActName("terminate");
                                orgActivity.setTimeTaken(t+1);
                            }

                        }
                    }
                    else if(activity.getActName().equals("release")){
                        releaseListOfResouces[activity.getResource() - 1] += activity.getUnits();
                        orgActivity.getCurrClaim()[activity.getResource()-1] = 0;
                        orgActivity.setTimeBlocked(0);
                        orgActivity.getResourcesUsed().set(activity.getResource()-1, (Integer) orgActivity.getResourcesUsed().get(activity.getResource()-1) - activity.getUnits());

                        int tempResourceNum = activity.getResource() - 1;
                        activity = (Activity) listOfTasks.get(i).get(taskCount[i]);

                        if(activity.getActName().equals("terminate") && !activity.isTermCounted()){
                            numFinishedTasks++;
                            numActive[tempResourceNum]--;
                            activity.setTermCounted(true);

                            orgActivity.setCurrClaim(new int[listOfResources.length]);
                            orgActivity.setNeed(new int[listOfResources.length]);

                            orgActivity.setActName("terminate");

                            orgActivity.setTimeTaken(t+1);
                        }
                    }
                }
            }

            if (numFinishedTasks == listOfTasks.size())
                isComplete = true;

            for(int m = 0; m < tempListOfResources.length;m++){
                tempListOfResources[m] += releaseListOfResouces[m];
            }
            listOfResources = tempListOfResources.clone();

            t++;


        }
    }

    /**
     * Checks to see if tasks are currently in a safe state used
     * only for Banker's Algorithm
     *
     * @param activity
     * @param orgActivity
     * @param tempListOfResources
     * @return
     */
    public boolean isSafeState(Activity activity, Activity orgActivity, int[] tempListOfResources){
        int[] tempResourceList = tempListOfResources.clone();
        int[] total = new int[tempResourceList.length];
        int[] available = new int[tempResourceList.length];
        Activity initialRootActivity = orgActivity;
        int returnableResourceCount = 0;
        int tempArray[];

        tempArray = orgActivity.getCurrClaim().clone();
        for(int i = 0; i < tempResourceList.length;i++){
            orgActivity.getCurrClaim()[i] = 0;
        }

        for(int i = 0; i < listOfTasks.size(); i++){
            orgActivity = (Activity) listOfTasks.get(i).get(0);
            orgActivity.setNeed(new int[tempResourceList.length]);
            for(int j = 0; j < tempResourceList.length;j++){
                orgActivity.getNeed()[j] += (orgActivity.getInitClaim()[j] - orgActivity.getCurrClaim()[j]);
                total[j] += orgActivity.getCurrClaim()[j];
                available[j] = (initialResources[j] - total[j]);
            }
        }

        initialRootActivity.setCurrClaim(tempArray);


        for(int i = 0; i < tempResourceList.length;i++){
            if(initialRootActivity.getNeed()[i] > available[i] && !initialRootActivity.getActName().equals("terminate") && !initialRootActivity.getActName().equals("aborted"))
                return false;
        }

        for(ArrayList process : listOfTasks){
            orgActivity = (Activity) process.get(0);
            if(orgActivity.getTask() != activity.getTask() && !orgActivity.getActName().equals("terminate") && !orgActivity.getActName().equals("aborted")){
                for(int i = 0; i < tempResourceList.length; i++){
                    if(orgActivity.getNeed()[i] <= available[i]){
                        returnableResourceCount++;
                    }
                    else
                        return false;
                }
                if(returnableResourceCount == tempResourceList.length){
                    for(int i = 0; i < tempResourceList.length; i++){
                        available[i] += orgActivity.getCurrClaim()[i];
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks to see if a task's request exceeds its original claim
     *
     * @param processIndex
     */
    public void checkClaims(int processIndex){

        ArrayList list = listOfTasks.get(processIndex);
        Activity orgActivity = (Activity) list.get(0);

        int[] totalUsed = new int[listOfResources.length];
        Activity currActivity;
        String activityName;

        for(int i = 0; i < list.size();i++){
            currActivity = (Activity) list.get(i);
            activityName = currActivity.getActName();
            if(activityName.equals("request")){
                totalUsed[currActivity.getResource() - 1] += currActivity.getUnits();

                for(int j = 0; j < listOfResources.length;j++){

                    if(totalUsed[j] > orgActivity.getInitClaim()[j])
                    {
                        System.out.println("Task " + currActivity.getTask() + " exceeded number of units present - will abort");
                        currActivity.setExceedsClaim(true);
                    }
                }
            }
            if(activityName.equals("release")){
                totalUsed[currActivity.getResource()-1] -= currActivity.getUnits();
            }
        }

    }

}
