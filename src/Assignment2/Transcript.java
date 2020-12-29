package Assignment2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;

/**
* This class generates a transcript for each student, whose information is in the text file.
*/

public class Transcript {
	private ArrayList<Object> grade = new ArrayList<Object>();
	private File inputFile;
	private String outputFile;
	
	/**
	 * This the the constructor for Transcript class that 
	 * initializes its instance variables and call readFie private
	 * method to read the file and construct this.grade.
	 * @param inFile is the name of the input file.
	 * @param outFile is the name of the output file.
	 */
	public Transcript(String inFile, String outFile) {
		inputFile = new File(inFile);	
		outputFile = outFile;	
		grade = new ArrayList<Object>();
		this.readFile();
	} // end of Transcript constructor

	/** 
	 * This method reads a text file and add each line as 
	 * an entry of grade ArrayList.
	 * @exception It throws FileNotFoundException if the file is not found.
	 */
	private void readFile() {
		Scanner sc = null; 
		try {
			sc = new Scanner(inputFile);	
			while(sc.hasNextLine()){
				grade.add(sc.nextLine());
	        }      
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			sc.close();
		}		
	} // end of readFile
	
	/**
	 * This method creates and returns an ArrayList, whose element is an object of class Student.
	 * The object at each element is created by aggregating ALL the information, that is found for one student
	 * in the grade Arraylist of class Transcript, i.e. if the text file contains information about 
	 * 9 students, then the array list will have 9 elements.
	 * Triple ArrayList used to get access to each students' grades for each course separately and store it in
	 * a large ArrayList named allStudentGrades. 
	 * Triple ArrayList used to get access to each students' exam weights for each course separately and store 
	 * it in a large ArrayList named allGradeWeights. 
	 * @return returns an arraylist 
	 * @throws InvalidTotalException  
	 */
	public ArrayList<Student> buildStudentArray() throws InvalidTotalException { 
		ArrayList<Student> students = new ArrayList<Student>(); 
        ArrayList<String> studentNames = new ArrayList<>();
        
        // Create a list unique student names
        for (Object record : grade) {
        	// Split each record w.r.t. commas (,)
            ArrayList<String> details = new ArrayList<String>(Arrays.asList(((String) record).split(",")));
            int lastIndex = details.size() - 1;  // To get last element, i.e. student-name
            String name = details.get(lastIndex).trim();
            if(!studentNames.contains(name)) { // Check if name already exists
                studentNames.add(name);
            }
        }
       
        // List of grades for each assessment for each student
        ArrayList<ArrayList<ArrayList<Double>>> allStudentGrades = new ArrayList<ArrayList<ArrayList<Double>>>();  
        // List of weights for each assessment for each student
        ArrayList<ArrayList<ArrayList<Integer>>> allGradeWeights = new ArrayList<ArrayList<ArrayList<Integer>>>(); 
        
        // for-each unique student
        for (String studentName : studentNames) {
            ArrayList<Course> courses = new ArrayList<>();
            // Individual student grades for each assessment
            ArrayList<ArrayList<Double>> studentGrades = new ArrayList<ArrayList<Double>>();  
            // Individual student weights for each assessment
            ArrayList<ArrayList<Integer>> gradeWeights = new ArrayList<ArrayList<Integer>>(); 
            String studentID = "";
            
            // for-each record of `input.txt`
            for (Object record : grade) {
                ArrayList<String> details = new ArrayList<String>(Arrays.asList(((String) record).split(",")));
                int lastIndex = details.size() - 1;
                String sName = details.get(lastIndex).trim();  
                
                if(sName.equals(studentName)) {  
                    ArrayList<Double> studentGradeForCourse = new ArrayList<>(); 
                    
                    ArrayList<Integer> gradeWeightForCourse = new ArrayList<>();
                    
                    // first element of input.txt is courseCode
                    String courseCode = details.get(0); // To get last element, i.e. student-name 
                    double courseCredit = Double.parseDouble(details.get(1)); //To get second element, i.e. credit 
                    
                    //ArrayList contains all P or E elements with weights and marks. 
                    ArrayList<String> assessments = new ArrayList<String>(details.subList(3, lastIndex)); 
                    ArrayList<Assessment> courseAssignment = new ArrayList<>(); //for Assessment objects 
                    
                    // 
                    for (String a : assessments) { 
                        char aType = a.charAt(0);       // Assessment type (i.e. P or E).
                        
                        // Assessment weight (for example - 10 in P10(50) ).
                        int aWeight = Integer.parseInt(a.substring(1, a.indexOf('(')));   
                        gradeWeightForCourse.add(aWeight);
                        
                        //Student marks for particular P or E. (for example - 50 in P10(50) ).
                        double aGrade = Double.parseDouble(a.substring(a.indexOf('(') + 1, a.indexOf(')'))); 
                        studentGradeForCourse.add(aGrade); //adding student marks for each P or E in ArrayList
                        
                        //creating an instance of Assessment (using static factory method).
                        courseAssignment.add(Assessment.getInstance(aType, aWeight));  
                    }
                    
                    studentGrades.add(studentGradeForCourse); 
                    gradeWeights.add(gradeWeightForCourse);
                    courses.add(new Course(courseCode, courseAssignment, courseCredit));
                    studentID = details.get(2); // To get last element, i.e. student-name
                }
            }
            
            students.add(new Student(studentID, studentName, courses)); 
            allStudentGrades.add(studentGrades);
            allGradeWeights.add(gradeWeights);
        }
        
        int numStudents = students.size();
        
        for (int s = 0; s < numStudents; s++) { 
            int numCourses = allStudentGrades.get(s).size();  
            for (int c = 0; c < numCourses; c++) {
            	try {
            		//adding to addGrade to calculate the final grade of the specific student.
            		students.get(s).addGrade(allStudentGrades.get(s).get(c), allGradeWeights.get(s).get(c)); 
            	}
            	catch(InvalidTotalException e) {
            		e.printStackTrace();
            	}
            }
        }
        
        return students; //ArrayList returned
	}

   /**
    * This is the method that prints the transcript to the given file (i.e. outputFile attribute).
    * @param students
    * @throws IOException - exception thrown if file is not able to be read or write to a txt file.  
    */
    public void printTranscript(ArrayList<Student> students) throws IOException {
    	FileWriter outputWriter = new FileWriter(outputFile);
    	try {
	        String dashes = "--------------------";
	        for (Student s : students) {
	        	String line1 = s.getName() + "\t" + s.getStudentID();
	            System.out.println(line1);
	            outputWriter.write(line1);
	            outputWriter.write("\n");
	            System.out.println(dashes);
	            outputWriter.write(dashes);
	            outputWriter.write("\n");
	            int size = s.getCourseTaken().size();
	            for (int i = 0; i < size; i++) { 
	            	String line2 = s.getCourseTaken().get(i).getCode() + "\t" + s.getFinalGrade().get(i);
	            	outputWriter.write(line2);
	            	outputWriter.write("\n");
	            	System.out.println(line2);
	            }
	            System.out.println(dashes);
	            outputWriter.write(dashes);
	            outputWriter.write("\n");
	            String line3 = "GPA: " + String.valueOf(s.weightedGPA()) + "\n";
	            System.out.println(line3);
	            outputWriter.write(line3);
	            outputWriter.write("\n");
	        }
    	}
    	catch (Exception e) {
    		System.out.println("Error occured while generating transcript!");
    		e.printStackTrace();
    	}
    	finally {
    		outputWriter.flush(); //clearing up buffer (if any). 
    		outputWriter.close(); //output file closed. 
    	}
    }
} // end of Transcript
       
/**
 * Exception IOException is thrown in method addGrade() of class Student.
 * @author nishaantdhingra
 */
class InvalidTotalException extends Exception{ 
	public InvalidTotalException() {
		System.out.println("Sum of weights/grades does not add up to 100."); 
	}
} //end of InvalidTotalException

/**
 * This class computes weighted grades for each course and add it to ArrayList finalGrade in method addGrade().
 * This class also computes the GPA for each student and returns it. 
 * @author nishaantdhingra
 *
 */
class Student /*extends keyword is not used because of composition*/{ 
	private String studentID;
	private String name;
	private ArrayList<Course> courseTaken;
	private ArrayList<Double> finalGrade;
	
	public String getStudentID() { 
		return studentID;
	}
	public String getName() {
		return name;
	}
	public ArrayList<Course> getCourseTaken() { 
		return courseTaken; 
	}
	public ArrayList<Double> getFinalGrade() {
		return finalGrade;
	}
	public Student() {
		this.studentID = "";
		this.name = "";
		this.courseTaken = new ArrayList<Course>();
		this.finalGrade = new ArrayList<Double>();
	}
	public Student(String stuID, String name, ArrayList<Course> takenCourse) {
		this.studentID = stuID;
		this.name = name;
		this.courseTaken = takenCourse; 
		this.finalGrade = new ArrayList<Double>();
	}
	
	 public void addCourse(Course course) {
        // add given course to courseTaken
        courseTaken.add(course);
     }
	 
	/**
	 * This method gets an array list of the grades and their weights, computes the true value of the 
	 * grade based on its weight and add it to finalGrade attribute. In case the sum of the weight was
	 * not 100, or the sum of grade was greater 100, it throws InvalidTotalException, which is an 
	 * exception that is defined in a separate class. 
	 * @param gradeFinal
	 * @param weightage
	 */
	 public void addGrade(ArrayList<Double> studentMarks, ArrayList<Integer> weights) throws InvalidTotalException  {
		int sumOfWeights = 0;
        for (int w : weights) {  
            sumOfWeights += w; 
        }
        boolean flag = false;
        if (sumOfWeights != 100) { 
            throw new InvalidTotalException(); 
        }
        
        else {
	        for (double g : studentMarks) {  
	        	if(g < 0.0 || g > 100.0 ) { 
	                flag = true;
	                break;
	            }
	        }
	        if (flag) {
	        	throw new InvalidTotalException();
	        }
	        else {
	        	int size = studentMarks.size();
		        double totalGrade = 0.0;
		        for (int i = 0; i < size; i++) {
		            totalGrade += (studentMarks.get(i) * weights.get(i)) / 100;
		        }
		        totalGrade = Math.round(totalGrade * 10) / 10.0;
		        finalGrade.add(totalGrade);
	        }
        }
    }
	 
	/**
	 * It is the method that computes the GPA. 
	 * @return returns GPA of the student rounded off to one decimal place.
	 */
	public double weightedGPA() {
		double totalCredits = 0.0;
        int totalGradePoints = 0;
        int numCourses = courseTaken.size();
        for (int c = 0; c < numCourses; c++) {
            double credit = courseTaken.get(c).getCredit(); 
            double grade = finalGrade.get(c);
            int gradePoint = 0;
            if (grade >= 90.0) {
                gradePoint = 9;
            }
            else if (grade >= 80.0) {
                gradePoint = 8;
            }
            else if (grade >= 75.0) {
                gradePoint = 7;
            }
            else if (grade >= 70.0) {
                gradePoint = 6;
            }
            else if (grade >= 65.0) {
                gradePoint = 5;
            }
            else if (grade >= 60.0) {
                gradePoint = 4;
            }
            else if (grade >= 55.0) {
                gradePoint = 3;
            }
            else if (grade >= 50.0) {
                gradePoint = 2;
            }
            else if (grade >= 47.0) {
                gradePoint = 1;
            }
            totalGradePoints += (gradePoint * credit);
            totalCredits += credit;
        }
        double finalGPA = Math.round((totalGradePoints / totalCredits) * 10) / 10.0;
        return finalGPA;
	}
} //end of Student

class Course /*extends keyword is not used because of composition*/{
	private String code;
	private ArrayList<Assessment> assignment;
	private double credit; 
	
	public Course() {
		this.code = "";	
		this.assignment = new ArrayList<Assessment>();
		this.credit = 0.0 ; 
	}
	
	public String getCode() { 
		return code;
	}
	
	public ArrayList<Assessment> getAssignment() {
		return assignment;
	}
	
	public double getCredit() {
		return credit;
	}
	
	public Course(String code, ArrayList<Assessment> assignment, double credit) {
		this.code = code;
		this.assignment = assignment;
		this.credit = credit;
	} 
	
	public Course(Course c) { 
		this.code = c.code;
	    this.assignment = c.assignment;
	    this.credit = c.credit;
	}
	
	/**
	 * Overriden method equals() of class Object. Simply returns true if variables of two
	 * different objects of same type are equal, and false otherwise. 
	 * For this specific class, this overriden equals() checks if this code, this credit and this assignment 
	 * is equal to object 'o' of the same type. 
	 */
	@Override
	public boolean equals(Object o) {
		 // check if object is null
	    if(o == null) {
	        return false;
	    }
	    // check if same object is passed
	    else if(this == o) {
	        return true;
	    }
	    // check if type of object is different
	    else if(this.getClass() != o.getClass()) {
	        return false;
	    }
	    // check for values of variables of two different objects of the same type
	    else {
	        Course c = (Course) o;
	        if((this.code == c.code) && 
	                (this.credit == c.credit) &&
	                (this.assignment.equals(c.assignment)))
	            return true;
	        else
	            return false;
	    }
	}
}//end of Course

/**
 * This class returns an instance of type Assessment using a static factory method. 
 * This class also checks the quality of different objects of same type by overriding equals() method
 * of class Object.
 * @author nishaantdhingra
 *
 */
class Assessment /*extends keyword is not used because of composition*/{
	private char type;
	private int weight;

	private Assessment() {
		this.type = '\0'; 
		this.weight = 0; 
	}
	
	private Assessment(char type, int weight) {
		this.type = type;
		this.weight = weight; 
	}
	
	public static Assessment getInstance(char type, int weight) { //static factory method 
		return new Assessment(type, weight);
	}
	
	/**
	 * Overriden method equals() of class Object. Simply returns true if variables of two
	 * different objects of same type are equal, and false otherwise. 
	 * For this specific class, this overriden equals() checks if this type and this weight 
	 * is equal to object 'o' of the same type. 
	 */
	@Override 
	public boolean equals(Object o) {
		// check if same object is passed
		if(this == o) {
			return true;
		}
		 // check if object is null
		if(o == null) {
			return false;
		}
	    // check if type of object is different
		if(this.getClass() != o.getClass()) {
			return false;
		}
		else {
			Assessment a = (Assessment) o;
			if((this.type == a.type) && (this.weight == a.weight)) {
				return true;
			}
			else
				return false;
		}
	}
}//end of Assessment