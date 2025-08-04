import java.util.ArrayList;
import java.util.Scanner;

public class GradeTracker {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Student> students = new ArrayList<>();

        System.out.println("📚 Student Grade Tracker");
        System.out.print("Enter number of students: ");
        int n = sc.nextInt();

        for (int i = 0; i < n; i++) {
            System.out.print("\nEnter name of student " + (i + 1) + ": ");
            String name = sc.next();

            System.out.print("Enter number of subjects: ");
            int subjectCount = sc.nextInt();

            int[] grades = new int[subjectCount];
            for (int j = 0; j < subjectCount; j++) {
                System.out.print("Enter grade for subject " + (j + 1) + ": ");
                grades[j] = sc.nextInt();
            }

            students.add(new Student(name, grades));
        }

        System.out.println("\n📊 Grade Summary:");
        for (Student s : students) {
            System.out.println("Name: " + s.getName());
            System.out.println("Average: " + s.getAverage());
            System.out.println("Highest: " + s.getHighest());
            System.out.println("Lowest: " + s.getLowest());
            System.out.println("---------------------");
        }

        sc.close();
    }
}
