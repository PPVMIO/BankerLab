import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by PaulPelayo on 11/22/15.
 */
public class OptimisticManager {
    private int[] listOfResources;                                       // Array of the current resources
    private int[] initialResources;										// List containing the initial list of resources
    private ArrayList<ArrayList<Activity>> listOfTasks;  	            // ArrayList containing ArrayLists of Activities
    private int taskCount[];                                             // Holds number to represent the current activity for a Task

    public OptimisticManager(int[] listOfResources, int[] initialResources, ArrayList<ArrayList<Activity>> listOfTasks, int taskCount[]){
        this.listOfResources = listOfResources;
        this.initialResources = initialResources;
        this.listOfTasks = listOfTasks;
        this.taskCount = taskCount;

    }
    /**
     * Optimistc algorithm
     * Runs optimistic algorithm - manager attempts to allocate
     * resources as often as possible whenever requested.  If deadlock
     * occurs abort the task release the resource and attempt to continue
     */
    public void runOptimistic(){
        int t = 0;
        int numBlocked = 0;
        int numFinishedTasks = 0;
        boolean isComplete = false;
        Activity activity;
        Activity orgActivity;
        Activity blockTest;
        Activity blockRoot;
        int tempListOfResources[];
        int releaselistOfResources[];
        int[] numActive = new int[listOfResources.length];
        boolean mustAbort;

        while(!isComplete){

            tempListOfResources = listOfResources.clone();
            releaselistOfResources = new int[listOfResources.length];


            for(int j = 0; j < listOfTasks.size(); j++){

                mustAbort = true;
                orgActivity = (Activity) listOfTasks.get(j).get(0);

                if (orgActivity.isBlocked() && !orgActivity.getActName().equals("aborted")){
                    activity = (Activity) listOfTasks.get(j).get(taskCount[j]);

                    /*
                     * Check for deadlock
                     */
                    for(int i = 0; i < listOfTasks.size(); i++){
                        blockRoot = (Activity) listOfTasks.get(i).get(0);
                        if(blockRoot.isBlocked() && !blockRoot.getActName().equals("aborted")){
                            blockTest = (Activity) listOfTasks.get(i).get(taskCount[i]);
                            if(!blockTest.getActName().equals("terminate")){
                                if(blockTest.getUnits() <= listOfResources[blockTest.getResource() - 1]){
                                    mustAbort = false;  //do not have to abort no deadlock
                                }
                            }
                        }
                    }
                    if(activity.getUnits() <= tempListOfResources[activity.getResource() - 1]){
                        Activity tempRoot, tempActivity;
                        tempRoot = findFirstBlockedActivity(tempListOfResources);
                        tempActivity = (Activity) listOfTasks.get(tempRoot.getTask() - 1).get(taskCount[tempRoot.getTask() -1]);
                        tempRoot.setIsBlocked(false);

                        tempRoot.setActName("notBlocked");
                        tempRoot.setJustReleased(true);
                        numBlocked--;
                        tempListOfResources[tempActivity.getResource() - 1] -= tempActivity.getUnits();
                        tempRoot.getResourcesUsed().set(tempActivity.getResource() - 1, (Integer) tempRoot.getResourcesUsed().get(tempActivity.getResource() - 1) + tempActivity.getUnits());

                    }
                    else{
                        orgActivity.increaseWaitingTime();

                        if(numBlocked == numActive[activity.getResource() - 1] && mustAbort){
                            orgActivity.setActName("aborted");
                            numBlocked--;
                            numActive[activity.getResource() - 1]--;
                            numFinishedTasks++;
                            for(int k = 0; k < listOfResources.length; k++){
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

                    if(activity.getActName().equals("initiate")){
                        numActive[activity.getResource() - 1]++;

                    }

                    else if(activity.getActName().equals("request")){
                        if(activity.getUnits() > tempListOfResources[activity.getResource() - 1] && !orgActivity.isJustReleased()){
                            orgActivity.increaseWaitingTime();
                            orgActivity.setIsBlocked(true);
                            numBlocked++;
                            orgActivity.setTimeBlocked(t);
                            taskCount[i]--;

                        }
                        else{
                            if(!orgActivity.isJustReleased())
                                tempListOfResources[activity.getResource()-1] -= activity.getUnits();
                            else
                                orgActivity.setJustReleased(false);
                            orgActivity.getResourcesUsed().set(activity.getResource() - 1, (Integer) orgActivity.getResourcesUsed().get(activity.getResource() - 1) + activity.getUnits());

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
                                orgActivity.setActName("terminate");
                                orgActivity.setTimeTaken(t+1);

                            }

                        }
                    }
                    else if(activity.getActName().equals("release")){
                        releaselistOfResources[activity.getResource() - 1] += activity.getUnits();
                        orgActivity.setTimeBlocked(0);

                        orgActivity.getResourcesUsed().set(activity.getResource() - 1, (Integer) orgActivity.getResourcesUsed().get(activity.getResource() - 1) - activity.getUnits());

                        int tempResourceNum = activity.getResource() - 1;
                        activity = (Activity) listOfTasks.get(i).get(taskCount[i]);

                        if(activity.getActName().equals("terminate") && !activity.isTermCounted()){
                            numFinishedTasks++;
                            numActive[tempResourceNum]--;
                            activity.setTermCounted(true);
                            orgActivity.setActName("terminate");

                            orgActivity.setTimeTaken(t+1);

                        }
                    }
                }
            }

            if (numFinishedTasks == listOfTasks.size())
                isComplete = true;

            for(int m = 0; m < tempListOfResources.length;m++){
                tempListOfResources[m] += releaselistOfResources[m];
            }
            listOfResources = tempListOfResources.clone();
            t++;
        }
    }

    /**
     * Prints final results
     */
    public void printOptimisticResults(){

        Activity orgActivity;
        int result;

        int totalTimeTaken = 0;
        int totalWaitingTime = 0;

        System.out.printf("\t~~~ OPTIMISTIC ~~~\n");


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
     * Searches for the first blocked process
     * used for optimistic only
     *
     * @param listOfResources       -    list of resources
     * @return
     */
    public Activity findFirstBlockedActivity(int[] listOfResources) {
        Activity blockRoot;
        Activity blockTest;
        int lowestTime = 100000;
        Activity returnRoot = null;

        for(int i = 0; i < listOfTasks.size(); i++){
            blockRoot = (Activity) listOfTasks.get(i).get(0);
            if(blockRoot.isBlocked() && !blockRoot.getActName().equals("aborted")){
                blockTest = (Activity) listOfTasks.get(i).get(taskCount[i]);
                if(!blockTest.getActName().equals("terminate")){
                    if(blockTest.getUnits() <= listOfResources[blockTest.getResource() - 1]){
                        if(blockRoot.getTimeBlocked() < lowestTime){
                            returnRoot = blockRoot;
                            lowestTime = blockRoot.getTimeBlocked();
                        }
                    }
                }
            }
        }
        return returnRoot;
    }

}
