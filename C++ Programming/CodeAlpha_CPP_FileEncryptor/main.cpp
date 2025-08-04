#include <iostream>
#include <fstream>
#include <string>

using namespace std;

void encryptDecryptFile(const string& inputFile, const string& outputFile, char key) {
    ifstream inFile(inputFile, ios::binary);
    ofstream outFile(outputFile, ios::binary);

    if (!inFile || !outFile) {
        cout << "❌ Failed to open input/output file." << endl;
        return;
    }

    char ch;
    while (inFile.get(ch)) {
        outFile.put(ch ^ key);  // XOR encryption/decryption
    }

    inFile.close();
    outFile.close();
    cout << "✅ Operation completed: " << outputFile << endl;
}

int main() {
    int choice;
    string inputFile, outputFile;
    char key;

    cout << "===== File Encryptor/Decryptor =====\n";
    cout << "1. Encrypt a file\n";
    cout << "2. Decrypt a file\n";
    cout << "Choose option: ";
    cin >> choice;

    cout << "Enter key (single char): ";
    cin >> key;

    cout << "Enter input file path (e.g., ../samples/input.txt): ";
    cin >> inputFile;

    cout << "Enter output file path (e.g., ../samples/encrypted.txt): ";
    cin >> outputFile;

    encryptDecryptFile(inputFile, outputFile, key);

    return 0;
}
