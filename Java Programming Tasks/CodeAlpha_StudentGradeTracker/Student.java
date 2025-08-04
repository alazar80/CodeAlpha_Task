public class Student {
    private String name;
    private int[] grades;

    public Student(String name, int[] grades) {
        this.name = name;
        this.grades = grades;
    }

    public String getName() {
        return name;
    }

    public int[] getGrades() {
        return grades;
    }

    public double getAverage() {
        int sum = 0;
        for (int g : grades) {
            sum += g;
        }
        return (double) sum / grades.length;
    }

    public int getHighest() {
        int max = grades[0];
        for (int g : grades) {
            if (g > max) max = g;
        }
        return max;
    }

    public int getLowest() {
        int min = grades[0];
        for (int g : grades) {
            if (g < min) min = g;
        }
        return min;
    }
}
