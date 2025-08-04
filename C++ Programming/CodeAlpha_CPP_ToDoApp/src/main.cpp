#include <iostream>
#include <vector>
#include <fstream>
using namespace std;

struct Task {
    string description;
    bool completed;
};

vector<Task> tasks;

void showMenu() {
    cout << "\n===== TO-DO LIST MENU =====" << endl;
    cout << "1. Add Task" << endl;
    cout << "2. View Tasks" << endl;
    cout << "3. Mark Task as Completed" << endl;
    cout << "4. Save to File" << endl;
    cout << "5. Load from File" << endl;
    cout << "6. Exit" << endl;
    cout << "Enter your choice: ";
}

void addTask() {
    string desc;
    cout << "Enter task description: ";
    cin.ignore();
    getline(cin, desc);
    tasks.push_back({desc, false});
    cout << "✅ Task added!" << endl;
}

void viewTasks() {
    cout << "\n📋 To-Do List:" << endl;
    for (size_t i = 0; i < tasks.size(); i++) {
        cout << i + 1 << ". [" << (tasks[i].completed ? "✔" : " ") << "] " << tasks[i].description << endl;
    }
}

void markTaskCompleted() {
    int index;
    viewTasks();
    cout << "Enter task number to mark as completed: ";
    cin >> index;
    if (index >= 1 && index <= tasks.size()) {
        tasks[index - 1].completed = true;
        cout << "✅ Task marked completed!" << endl;
    } else {
        cout << "⚠️ Invalid task number." << endl;
    }
}

void saveToFile() {
    ofstream file("../data/tasks.txt");
    for (auto &t : tasks) {
        file << t.description << "," << t.completed << endl;
    }
    file.close();
    cout << "💾 Tasks saved to file." << endl;
}

void loadFromFile() {
    ifstream file("../data/tasks.txt");
    string desc;
    int done;
    tasks.clear();
    while (getline(file, desc, ',')) {
        file >> done;
        file.ignore();
        tasks.push_back({desc, static_cast<bool>(done)});
    }
    file.close();
    cout << "📂 Tasks loaded from file." << endl;
}

int main() {
    int choice;
    do {
        showMenu();
        cin >> choice;
        switch (choice) {
            case 1: addTask(); break;
            case 2: viewTasks(); break;
            case 3: markTaskCompleted(); break;
            case 4: saveToFile(); break;
            case 5: loadFromFile(); break;
            case 6: cout << "👋 Exiting program." << endl; break;
            default: cout << "⚠️ Invalid choice." << endl;
        }
    } while (choice != 6);

    return 0;
}
