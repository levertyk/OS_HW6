#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <thread>

// Customer class to store customer information
class Customer
{
public:
    Customer(const std::string &name, double balance)
        : name_(name), balance_(balance) {}

    const std::string &getName() const { return name_; }
    double getBalance() const { return balance_; }

private:
    std::string name_;
    double balance_;
};

// Function to read customers' information from file and store in vector
void readCustomersFromFile(const std::string &filename, std::vector<Customer> &customers)
{
    std::ifstream file(filename);
    if (file.is_open())
    {
        std::string name;
        double balance;
        while (file >> name >> balance)
        {
            customers.push_back(Customer(name, balance));
        }
        file.close();
    }
}

// Function to count number of customers with balances less than $1000.00
int countCustomersWithLowBalance(const std::vector<Customer> &customers)
{
    int count = 0;
    for (const auto &customer : customers)
    {
        if (customer.getBalance() < 1000.00)
        {
            count++;
        }
    }
    return count;
}

int main()
{
    std::vector<Customer> customers;
    readCustomersFromFile("accounts.txt", customers);

    // Sequential version
    auto start = std::chrono::high_resolution_clock::now();
    int count1 = countCustomersWithLowBalance(customers);
    auto end = std::chrono::high_resolution_clock::now();
    auto duration1 = std::chrono::duration_cast<std::chrono::milliseconds>(end - start);
    std::cout << "Sequential run took " << duration1.count() << " milliseconds." << std::endl;
    std::cout << "Number of customers with balances less than $1000.00 is: " << count1 << std::endl;

    // Multithreaded version
    start = std::chrono::high_resolution_clock::now();
    int count2 = 0;
    int numThreads = 4; // Number of threads to use
    std::vector<std::thread> threads;
    int numCustomersPerThread = customers.size() / numThreads;
    for (int i = 0; i < numThreads; i++)
    {
        int startIdx = i * numCustomersPerThread;
        int endIdx = (i == numThreads - 1) ? customers.size() : (i + 1) * numCustomersPerThread;
        threads.push_back(std::thread([&customers, &count2, startIdx, endIdx]()
                                      {
            for (int j = startIdx; j < endIdx; j++) {
                if (customers[j].getBalance() < 1000.00) {
                    count2++;
                }
            } }));
    }

    for (auto &thread : threads)
    {
        thread.join();
    }

    end = std::chrono::high_resolution_clock::now();
    auto duration2 = std::chrono::duration_cast<std::chrono::milliseconds>(end - start);
    std::cout << "Multithreaded run took " << duration2.count() << " milliseconds." << std::endl;
    std::cout << "Number of customers with balances less than $1000.00 is: " << count2 << std::endl;

    return 0;
}