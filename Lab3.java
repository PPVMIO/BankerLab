


import java.util.Scanner;
import java.io.*;
import java.util.*;
import java.lang.Math;

public class Lab3{

    static int[] listOfResources;                                       // Array of the current resources
    static int[] initialResources;										// List containing the initial list of resources
    static ArrayList<ArrayList<Activity>> listOfTasks;  	            // ArrayList containing ArrayLists of Activities
    static int taskCount[];                                             // Holds number to represent the current activity for a Task


    /**
     * Reads and parses data from a .txt file.  Initilalizes
     * data structures in order to run the simulation
     *
     * @param input     -   scanner for the data to be read in
     *
     */
    public static void readInData(Scanner input){
        listOfTasks = new ArrayList<ArrayList<Activity>>();
        int numberOfTasks = Integer.parseInt(input.next());
        int numResources = Integer.parseInt(input.next());
        Activity activity;

        //all tasks start at activity 0
        taskCount = new int[numberOfTasks];
        for(int i = 0; i < numberOfTasks; i++){
            taskCount[i] = 0;
            listOfTasks.add(new ArrayList<Activity>());
        }

        //initialize list of resources and copy it to initial resources
        listOfResources = new int[numResources];
        for(int i = 0; i < numResources; i++){
            listOfResources[i] = Integer.parseInt(input.next());
        }
        initialResources = listOfResources.clone();

        //read in activities line by line
        while (input.hasNext()){
            activity = new Activity(numResources);
            activity.setActName(input.next());
            activity.setTask(Integer.parseInt(input.next()));
            activity.setResource(Integer.parseInt(input.next()));
            if (activity.getActName().equals("compute")){
                activity.setCycles(activity.getResource());
            }
            activity.setUnits(Integer.parseInt(input.next()));
            listOfTasks.get(activity.getTask() - 1).add(activity);
        }

        //claims
        for(int i = 0; i < numberOfTasks; i++){
            Activity orgActivity = (Activity) listOfTasks.get(i).get(0);
            orgActivity.setInitClaim(new int[numResources]);
            for(int j = 0; j < listOfTasks.get(i).size(); j++){
                Activity tempInst = (Activity) listOfTasks.get(i).get(j);
                if(tempInst.getActName().equals("initiate")){
                    orgActivity.getInitClaim()[tempInst.getResource()-1] = tempInst.getUnits();
                }
            }
            orgActivity.setCurrClaim(new int[numResources]);
            for(int j = 0; j < numResources; j++){
                if(orgActivity.getInitClaim()[j] > listOfResources[j])
                    orgActivity.setMustAbort(true);
            }
        }
    }



    public static void main(String[] args) {
        String fileName;
        fileName = args[0];
//        fileName = "input/input-05.txt";
        try {
            readInData(new Scanner(new File(fileName)));
//            System.out.println(initialResources);
            OptimisticManager om = new OptimisticManager(listOfResources, initialResources, listOfTasks, taskCount);
            om.runOptimistic();
            om.printOptimisticResults();

            listOfResources = null;
            initialResources = null;
            listOfTasks = null;
            taskCount = null;


//            System.out.println(initialResources);

            readInData(new Scanner(new File(fileName)));
//            System.out.println(initialResources);

            BankerManager bm = new BankerManager(listOfResources, initialResources, listOfTasks, taskCount);
            bm.runBanker();
            bm.printBankerReults();
        }catch (IOException e) {
            System.out.println("Issue reading in file: " + fileName);
            System.out.println(e);
        }


    }
}