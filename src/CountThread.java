import java.util.ArrayList;

class CountThread extends Thread {
    private ArrayList<Customer> custList;
    private int startIndex;
    private int endIndex;
    private int count;

    public CountThread(ArrayList<Customer> custList, int startIndex, int endIndex) {
        this.custList = custList;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.count = 0;
    }

    @Override
    public void run() {
        for (int i = startIndex; i < endIndex; ++i) {
            if (custList.get(i).getBalance() < 1000) {
                synchronized (this) { // synchronize access to shared count variable
                    ++count;
                }
            }
        }
    }

    public int getLowBalanceCount() {
        return count;
    }
}
