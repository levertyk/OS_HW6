import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class BankDriverNew {

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        /*
         * // feel free to create a different file with smaller/larger number of records
         * // create a file with random records createAccountsFile(4000000);
         * System.out.println("File Was Created!");
         */
        ArrayList<Customer> custList = new ArrayList<Customer>();
        // reading the info from the file and storing in the arrayList
        long currentRec = 0;
        System.out.println("Reading the file \"accounts.txt\" ");
        Scanner inFile = new Scanner(new File("accounts.txt"));
        while (inFile.hasNext()) { // while 2
            String currID = inFile.nextLine();
            double currBalance = inFile.nextDouble();
            if (inFile.hasNextLine())
                inFile.nextLine(); // dummy reading
            custList.add(new Customer(currID, currBalance));
            if (++currentRec % 30000 == 0)
                System.out.print(">>");
            if (currentRec % 1000000 == 0)
                System.out.println();

        } // end of while 2

        // Counting the balances that are less than 1000$
        int lowBalances = 0;
        // --Sequential-Run--------------------------------------------------------------
        long startTime = System.currentTimeMillis();
        lowBalances = sequentialCounting(custList); // sequential run
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Number of accounts with less than 1000$ is: " + lowBalances);
        System.out.println("Sequential run took in miliseconds: " + estimatedTime);

        // --Parallel-Run--------------------------------------------------------------
        startTime = System.currentTimeMillis();
        lowBalances = parallelCounting(custList); // parallel run
        estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Number of accounts with less than 1000$ is: " + lowBalances);
        System.out.println("Parallel run took in miliseconds: " + estimatedTime);

    }// end of main

    static int sequentialCounting(ArrayList<Customer> myList) {
        int count = 0;
        for (int i = 0; i < myList.size(); ++i)
            if (myList.get(i).getBalance() < 1000)
                ++count;
        return count;
    }

    static int parallelCounting(ArrayList<Customer> myList) throws InterruptedException {
        int numThreads = Runtime.getRuntime().availableProcessors(); // Get number of available processors
        int chunkSize = myList.size() / numThreads; // Calculate chunk size for each thread
        int lowBalances = 0;

        ArrayList<CountThread> threads = new ArrayList<>(); // Create an array list to hold the counting threads

        for (int i = 0; i < numThreads; i++) {
            // Create a new CountThread for each chunk of data
            int startIndex = i * chunkSize;
            int endIndex = (i == numThreads - 1) ? myList.size() : startIndex + chunkSize;
            CountThread thread = new CountThread(myList, startIndex, endIndex);
            threads.add(thread);
            thread.start(); // Start the thread
        }

        // Wait for all threads to complete
        for (CountThread thread : threads) {
            thread.join();
            lowBalances += thread.getLowBalanceCount(); // Accumulate the results from each thread
        }

        return lowBalances;
    }
}
