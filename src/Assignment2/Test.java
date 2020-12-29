package Assignment2;

import java.io.IOException;
import java.util.ArrayList;

public class Test {
	public static void main(String[] args) throws IOException, InvalidTotalException {
		try {
			Transcript t = new Transcript("input.txt", "output1.txt"); 
			ArrayList<Student> allStudents = t.buildStudentArray();
			t.printTranscript(allStudents);   
		}
		catch(Exception e) {
			System.out.println("File not found theek krlo");
			e.printStackTrace();
		}
	}
}

/**
 * Transcript
	 * buildStudentArray
		 * 1. first for loop - unique arraylist of studentNames
		 * 2. second for loop - for each student, get courses and their respective grades. 
		 * 
		 * printTranscript
		 * 1. for loop - for each student s, print their transcript.
		 * 2. write to output.txt
 * Student
 	*addCourse
 		* add given course to courseTaken
 	*addGrade
 		* 1. check sum of weightage to give exception
 		* 2. return final grade 
 *  
 */