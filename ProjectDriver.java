//Christopher Otto
//Dr. Hatim Boustique
//COP3330 Section 0001
//Final Project

//imports
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ProjectDriver {
	//mapped arrays for lectures
	private static ArrayList<Integer> crns = new ArrayList<Integer>(); //list of all course numbers
	private static ArrayList<String> prefixes = new ArrayList<String>(); //list of all course prefixes
	private static ArrayList<String> classNames = new ArrayList<String>(); //list of all class names
	private static ArrayList<String> classType = new ArrayList<String>(); //0 for undergrad, 1 for grad
	private static ArrayList<String> mode = new ArrayList<String>(); //list of all class modes
	private static ArrayList<String> classLocations = new ArrayList<String>(); //list of all class locations
	private static ArrayList<String> hasLab = new ArrayList<String>(); //whether class has lab
	private static ArrayList<Integer> allCrhs = new ArrayList<Integer>(); //list of all course credit hours
	
	//mapped arrays for labs
	private static ArrayList<ArrayList<Integer>> labCrns = new ArrayList<ArrayList<Integer>>(); //list of all lab course numbers
	private static ArrayList<ArrayList<String>> labLocations = new ArrayList<ArrayList<String>>(); //list of all lab locations

	private static Scanner input = new Scanner(System.in); //declare a scanner for use everywhere

	private static ArrayList<Student> students = new ArrayList<>(); //list to hold students

	//display main menu and prompt user to make a selection
	private static int mainMenu() {
		int userSelection = -5;

		while(userSelection != 0 && userSelection != 1 && userSelection != 2) {
			//display main menu
			System.out.println("\n---------------------------------------------------\n");
			System.out.println("Main Menu\n");
			System.out.println("1 : Student Management");
			System.out.println("2 : Course Management");
			System.out.println("0 : Exit\n");

			//prompt user for selection
			System.out.printf("\tEnter your selection: ");
			userSelection = input.nextInt();
			input.nextLine();
		}

		System.out.println("-----------------");

        return userSelection; //return user’s choice menu selection
	}
	
	//display course menu and prompt user to make a selection
	private static char courseManagementMenu() {
		String userSelection = "";

		while(!userSelection.equals("A") && !userSelection.equals("B") && !userSelection.equals("C") && !userSelection.equals("X")) {
			//display courseManagementMenu
			System.out.println("\nCourse Management Menu:\n");
			System.out.println("Choose one of:");
			System.out.println("\tA - Search for a class or lab using the class/lab number");
			System.out.println("\tB - delete a class");
			System.out.println("\tC - Add a lab to a class");
			System.out.println("\tX - Back to main menu\n");

			//prompt user for selection
			System.out.printf("Enter your selection: ");
			userSelection = input.nextLine();

			//convert to uppercase to allow for any case input
			userSelection = userSelection.toUpperCase();
		}
		
		System.out.println(); //extra space after menu

        return userSelection.charAt(0); //return user’s choice menu selection
	}

	//display student menu and prompt user to make a selection
	private static char studentManagementMenu() {
		String userSelection = "p";

		while(!userSelection.equals("A") && !userSelection.equals("B") && !userSelection.equals("C") && !userSelection.equals("X") && !userSelection.equals("D")) {
			//display student management menu
			System.out.println("\nStudent Management Menu:\n");
			System.out.println("Choose one of:");
			System.out.println("\tA - Search add a student");
			System.out.println("\tB - Delete a student");
			System.out.println("\tC - Print Fee Invoice");
			System.out.println("\tD - Print List of Students");
			System.out.println("\tX - Back to main menu\n");

			//prompt user for selection
			System.out.printf("Enter your selection: ");
			userSelection = input.nextLine();

			//convert to uppercase to allow for any case input
			userSelection = userSelection.toUpperCase();
		}

		System.out.println(); //extra space after menu

        return userSelection.charAt(0); //return user’s choice menu selection
	}

	//lookup a class or lab using its number
	private static void classSearch() {
		int userSelection = -1;
		int curIndex = -1;
		boolean lab = false;
		int labIndex = -500000;

		//prompt user to enter a class/lab number and check for validity
		while(curIndex == -1) {
			System.out.print("Enter the Class/Lab Number: ");
			userSelection = input.nextInt();
			input.nextLine();
			curIndex = crns.indexOf(userSelection);

			if(curIndex == -1) {
				for(int i = 0; i < crns.size(); ++i) {
					if(labCrns.get(i).size() > 0) {
						labIndex = labCrns.get(i).indexOf(userSelection);

						if(labIndex != -1) {
							curIndex = i;
							lab = true;
							break;
						}
					}
				}
				
				if(lab == false)
					System.out.println("Invalid / not found! Please try again.");
			}	
		}

		//output search results
		if(lab == true)
			System.out.print("Lab for ");

		System.out.printf("[%d, %s, %s]\n", crns.get(curIndex), prefixes.get(curIndex), classNames.get(curIndex));

		if(lab == true)
			System.out.printf("Lab Room %s\n", labLocations.get(curIndex).get(labIndex));
	}

	//delete a class (can't delete a class with a lab)
	private static void deleteClass() {
		ArrayList<String> fileLines = new ArrayList<>();
		String curLine;
		String[] curLineParts;
		int userSelection = -1;
		int curIndex = -1;

		//no classes case
		if(crns.size() == 0) {
			System.out.println("There are no classes, so you can't delete one.");
			return;
		}

		//prompt user for a class number to delete
		while(curIndex == -1) {
			System.out.print("Enter the Class Number: ");
			userSelection = input.nextInt();
			input.nextLine();
			curIndex = crns.indexOf(userSelection);

			if(curIndex == -1)
				System.out.println("Invalid / not found! Please try again.");
		}

		
		if(hasLab.get(curIndex).equals("YES")) { //invalid for deletion if has lab
			System.out.println("Error, that class can't be deleted because it has lab(s). Quitting deletion...");
			return;
		} else {
			//deletion message
			System.out.printf("[%d, %s, %s] deleted!\n", crns.get(curIndex), prefixes.get(curIndex), classNames.get(curIndex));

			//remove class from arrays
			crns.remove(curIndex);
			prefixes.remove(curIndex);
			classNames.remove(curIndex);
			classType.remove(curIndex);
			mode.remove(curIndex);
			classLocations.remove(curIndex);
			hasLab.remove(curIndex);
			allCrhs.remove(curIndex);
			labCrns.remove(curIndex);
			labLocations.remove(curIndex);

			int idToRemove;

			//remove class from all student's schedules
			for(int i = 0; i < students.size(); ++i) {
				if(students.get(i).getType().equals("MS")) {
					if(((idToRemove = ((MsStudent) students.get(i)).getGradCrnsTaken().indexOf(userSelection))) != -1) {
						((MsStudent) students.get(i)).getGradCrnsTaken().remove(idToRemove);
					}
				} else if(students.get(i).getType().equals("Undergrad")) {
					if(((idToRemove = ((UndergraduateStudent) students.get(i)).getUndergradCrnsTaken().indexOf(userSelection))) != -1) {
						((UndergraduateStudent) students.get(i)).getUndergradCrnsTaken().remove(idToRemove);
					}
				}
			}

			//remove class from lec.txt
			try {
				BufferedReader reader = new BufferedReader(new FileReader("lec.txt"));

				//save all lines except for deleted one
				while((curLine = reader.readLine()) != null) {
					curLineParts = curLine.split(",");
					
					if(Integer.parseInt(curLineParts[0]) != userSelection)
						fileLines.add(curLine);
				}

				reader.close();

				BufferedWriter writer = new BufferedWriter(new FileWriter("lec.txt"));

				//write new file
				writer.write(fileLines.get(0));
				for(int k = 1; k < fileLines.size(); ++k) {
					writer.write("\n" + fileLines.get(k));
				}	

				writer.close();
			} catch(IOException e) {
				System.out.println("IOException!");
			}
		}
	}

	//add a lab to a class (must already be 1+ labs)
	private static void addLab() {
		ArrayList<String> fileLines = new ArrayList<>();
		String curLine;
		String[] curLineParts;
		int userSelection = -1;
		int curIndex = -1;
		String labLocation;
		boolean foundClass = false;
		int labCrn;

		//no classes case
		if(crns.size() == 0) {
			System.out.println("There are no classes, so you can't delete one.");
			return;
		}

		//prompt user for lecture number to add lab to
		while(curIndex == -1) {
			System.out.print("Enter the lecture number to add a lab to: ");
			userSelection = input.nextInt();
			input.nextLine();
			curIndex = crns.indexOf(userSelection);

			if(curIndex == -1)
				System.out.println("Invalid / not found! Please try again.");
		}
		
		if(hasLab.get(curIndex) == "NO") { //validity check
			System.out.println("Error! You can't add labs to a lecture without labs. Quitting action...");
			return;
		}

		//enter crn for lab
		System.out.print("Enter the lab's crn: ");
		labCrn = input.nextInt();
		input.nextLine();

		//enter lab location
		System.out.print("Enter the lab's location: ");
		labLocation = input.nextLine();

		//add lab to arrays
		labCrns.get(curIndex).add(labCrn);
		labLocations.get(curIndex).add(labLocation);

		//add lab to lec.txt
		try {
			BufferedReader reader = new BufferedReader(new FileReader("lec.txt"));

			//save all lines and add new lab
			while((curLine = reader.readLine()) != null) {
				curLineParts = curLine.split(",");
				
				if(Integer.parseInt(curLineParts[0]) == userSelection)
					foundClass = true;
				
				if(foundClass == true && Integer.parseInt(curLineParts[0]) != userSelection) {
					if(curLineParts.length > 2) {
						fileLines.add(labCrn + "," + labLocation);
						foundClass = false;
					}
				}

				fileLines.add(curLine);
			}

			reader.close();

			BufferedWriter writer = new BufferedWriter(new FileWriter("lec.txt"));

			//write new file
			writer.write(fileLines.get(0));
			for(int k = 1; k < fileLines.size(); ++k) {
				writer.write("\n" + fileLines.get(k));
			}

			writer.close();
		} catch(IOException e) {
			System.out.println("IOException!");
		}
	}

	private static void addStudent() {
		boolean validInput = false;
		String idInput = "";
		String typeInput = "";
		String remainingInformation;
		String[] splitRemainingInformation;
		
		//get a valid id
		while(validInput == false) {
			System.out.print("Enter student's ID: ");
			idInput = input.nextLine();

			try {
				//check that input is valid
				if(idInput.length() == 6) {
					if(Character.isAlphabetic(idInput.charAt(0)) == true && Character.isAlphabetic(idInput.charAt(1))) {
						if(Character.isDigit(idInput.charAt(2)) == true) {
							if(Character.isDigit(idInput.charAt(3)) == true) {
								if(Character.isDigit(idInput.charAt(4)) == true) {
									if(Character.isDigit(idInput.charAt(5)) == true) {
										validInput = true;
									}
								}
							}
						}
					}
				}

				//prompt user to try again if input is invalid
				if(validInput == false) {
					throw new IdException("ID not in correct format");
				} else {
					for(int i = 0; i < students.size(); ++i) {
						if(students.get(i).getId().equals(idInput)) {
							validInput = false;
							throw new IdException("Student already exists!");
						}
					}
				}
			} catch(IdException e) {
				System.err.println("Invalid ID: " + e.getMessage());
			}
		}

		validInput = false;

		//prompt user to choose student type
		while(validInput == false) {
			System.out.print("Student Type (PhD, MS, or Undergrad): ");
			typeInput = input.nextLine();

			if(typeInput.equals("PhD") || typeInput.equals("MS") || typeInput.equals("Undergrad")) {
				validInput = true;
			}

			if(validInput == false) {
				System.out.println("Invalid selection. Please try again.");
			}
		}

		System.out.println("\nEnter remaining information");
		
		//add student by prompting user for more information depending on student type
		if(typeInput.equals("PhD")) {
			System.out.println("Format: name|advisor|research subject|labCRN1,labCRN2,...\n");

			remainingInformation = input.nextLine();
			splitRemainingInformation = remainingInformation.split("\\|");

			String name = splitRemainingInformation[0];
			String advisor = splitRemainingInformation[1];
			String researchSubject = splitRemainingInformation[2];
			String[] labsAsStrings = splitRemainingInformation[3].split(","); 
			ArrayList<Integer> labsAsInts = new ArrayList<Integer>();

			for(int i = 0; i < labsAsStrings.length; ++i) {
				labsAsInts.add(Integer.parseInt(labsAsStrings[i]));
			}

			students.add(new PhdStudent(name, idInput, advisor, researchSubject, labsAsInts));
		} else if(typeInput.equals("MS")) {
			System.out.println("Format: name|classCRN1,classCRN2,...");

			remainingInformation = input.nextLine();
			splitRemainingInformation = remainingInformation.split("\\|");

			String name = splitRemainingInformation[0];
			String[] classesAsStrings = splitRemainingInformation[1].split(",");
			ArrayList<Integer> classesAsInts = new ArrayList<>();

			for(int i = 0; i < classesAsStrings.length; ++i) {
				classesAsInts.add(Integer.parseInt(classesAsStrings[i]));
			}

			students.add(new MsStudent(name, idInput, classesAsInts, null));
		} else {
			System.out.println("Format: name|classCRN1,classCRN2,...|gpa|resident? (enter true or false)");

			remainingInformation = input.nextLine();
			splitRemainingInformation = remainingInformation.split("\\|");

			String name = splitRemainingInformation[0];
			String[] classesAsStrings = splitRemainingInformation[1].split(",");
			ArrayList<Integer> classesAsInts = new ArrayList<>();
			double gpa = Double.parseDouble(splitRemainingInformation[2]);
			boolean resident;

			if(splitRemainingInformation[3].equals("true")) {
				resident = true;
			} else {
				resident = false;
			}

			for(int i = 0; i < classesAsStrings.length; ++i) {
				classesAsInts.add(Integer.parseInt(classesAsStrings[i]));
			}

			students.add(new UndergraduateStudent(name, idInput, classesAsInts, gpa, resident));
		}

		System.out.println("[" + students.get(students.size() - 1).getName() + "] added!");
	}

	private static void deleteStudent() {
		String idInput = "";
		boolean validInput = false;
		int indexToDelete = -1;
		
		//no students case
		if(students.size() == 0) {
			System.out.println("Error! There are no students, so you can't delete one. Quitting...");
			return;
		}

		//get id from user
		while(validInput == false) {
			System.out.print("Enter student's ID to delete: ");
			idInput = input.nextLine();

			try {
				//check that input is valid
				if(idInput.length() == 6) {
					if(Character.isAlphabetic(idInput.charAt(0)) == true && Character.isAlphabetic(idInput.charAt(1))) {
						if(Character.isDigit(idInput.charAt(2)) == true) {
							if(Character.isDigit(idInput.charAt(3)) == true) {
								if(Character.isDigit(idInput.charAt(4)) == true) {
									if(Character.isDigit(idInput.charAt(5)) == true) {
										validInput = true;
									}
								}
							}
						}
					}
				}

				//prompt user to try again if input is invalid
				if(validInput == false) {
					throw new IdException("Invalid id format. Please try again.");
				}
			} catch(IdException e) {
				System.err.println("Invalid ID: " + e.getMessage());
			}
		}

		//delete student
		try {
			validInput = false; //reusing validInput for future input validation

			for(int i = 0; i < students.size(); ++i) {
				if(students.get(i).getId().equals(idInput)) {
					validInput = true;
					indexToDelete = i;
					break;
				}
			}

			if(validInput == false) {
				throw new IdException("There isn't a student with that ID. Quitting...");
			}

			System.out.println("Student [" + students.get(indexToDelete).getName() + "] was removed successfully!"); //student removed successfully message

			students.remove(indexToDelete); //remove student
		} catch(IdException e) {
			System.err.println("Invalid ID: " + e.getMessage());
		}
		
	}

	private static void printFeeInvoice() {
		String idInput = "";
		boolean validInput = false;
		int idToPrint = -1;
		
		//students is empty case
		if(students.size() == 0) {
			System.out.println("Error! There are no students, so you can't print the fee invoice of one. Quitting...");
			return;
		}

		//get a ID with the correct ID format
		while(validInput == false) {
			try {
				System.out.print("Enter student's ID to print their invoice: ");
				idInput = input.nextLine();

				//check that input is valid
				if(idInput.length() == 6) {
					if(Character.isAlphabetic(idInput.charAt(0)) == true && Character.isAlphabetic(idInput.charAt(1))) {
						if(Character.isDigit(idInput.charAt(2)) == true) {
							if(Character.isDigit(idInput.charAt(3)) == true) {
								if(Character.isDigit(idInput.charAt(4)) == true) {
									if(Character.isDigit(idInput.charAt(5)) == true) {
										validInput = true;
									}
								}
							}
						}
					}
				}

				//prompt user to try again if input is invalid
				if(validInput == false) {
					throw new IdException("Invalid id format. Please try again.");
				}
			} catch(IdException e) {
				System.err.println("Invalid ID: " + e.getMessage());
			}
		}

		try {
			validInput = false; //reusing validInput for future input validation

			//check if ID entered is valid
			for(int i = 0; i < students.size(); ++i) {
				if(students.get(i).getId().equals(idInput)) {
					validInput = true;
					idToPrint = i;
					break;
				}
			}

			if(validInput == false) {
				throw new IdException("There isn't a student with that ID. Quitting...");
			}

			students.get(idToPrint).printInvoice();
		} catch(IdException e) {
			System.err.println("Invalid ID: " + e.getMessage());
		}
	}

	private static void printListOfStudents() {
		if(students.size() == 0) {
			System.out.println("There are no students, so you can't print them.");
			return;
		}

		//print PhD students
		System.out.println("PhD Students");
		System.out.println("------------");
		for(int i = 0; i < students.size(); ++i) {
			if(students.get(i).getType().equals("PhD")) {
				System.out.println("\t- " + students.get(i).getName());
			}
		}

		//print MS students
		System.out.println("MS Students");
		System.out.println("------------");
		for(int i = 0; i < students.size(); ++i) {
			if(students.get(i).getType().equals("MS")) {
				System.out.println("\t- " + students.get(i).getName());
			}
		}

		//print Undergraduate students
		System.out.println("Undergraduate Students");
		System.out.println("------------");
		for(int i = 0; i < students.size(); ++i) {
			if(students.get(i).getType().equals("Undergrad")) {
				System.out.println("\t- " + students.get(i).getName());
			}
		}
	}

	public static void main(String[] args) {
		String curLine;
		String[] curLineParts;

		//save initial lec.txt
		try {
			BufferedReader reader = new BufferedReader(new FileReader("lec.txt"));

			while((curLine = reader.readLine()) != null) {
				curLineParts = curLine.split(",");

				if(curLineParts.length == 8 || curLineParts.length == 6) { //Lecture
					crns.add(Integer.parseInt(curLineParts[0]));
					prefixes.add(curLineParts[1]);
					classNames.add(curLineParts[2]);
					classType.add(curLineParts[3]);
					mode.add(curLineParts[4]);

					if(curLineParts.length == 8) { //F2F class
						classLocations.add(curLineParts[5]);
						hasLab.add(curLineParts[6]);
						allCrhs.add(Integer.parseInt(curLineParts[7]));
					} else { //online class
						classLocations.add(null);
						hasLab.add("NO");
						allCrhs.add(Integer.parseInt(curLineParts[5]));
					}

					labCrns.add(new ArrayList<Integer>());
					labLocations.add(new ArrayList<String>());

				} else { //Lab
					labCrns.get(crns.size() - 1).add(Integer.parseInt(curLineParts[0]));
					labLocations.get(crns.size() - 1).add(curLineParts[1]);
				}
			}

			reader.close();
		} catch(IOException e) {
			System.out.println("IOException!");
		}

		//variables to store user menu selections
		int mainUserSelection;
		char studentManagementSelection = 'p';
		char courseManagementSelection = 'p';

		while((mainUserSelection = mainMenu()) != 0) { //main menu
			if(mainUserSelection == 1) { //Student Management menu
				while((studentManagementSelection = studentManagementMenu()) != 'X') {
					switch(studentManagementSelection) {
						case 'A':
							addStudent();
							break;
						case 'B':
							deleteStudent();
							break;
						case 'C':
							printFeeInvoice();
							break;
						case 'D':
							printListOfStudents();
							break;
					}
				}

				studentManagementSelection = 'p';
			} else { //Course Management menu
				while((courseManagementSelection = courseManagementMenu()) != 'X') {
					switch(courseManagementSelection) {
						case 'A':
							classSearch();
							break;
						case 'B':
							deleteClass();
							break;
						case 'C':
							addLab();
							break;
					}
				}

				courseManagementSelection = 'p';
			}
		}

		input.close();
	}

	//getters
	public static ArrayList<Integer> getCrns() {
		return crns;
	}

	public static ArrayList<String> getPrefixes() {
		return prefixes;
	}

	public static ArrayList<String> getClassNames() {
		return classNames;
	}

	public static ArrayList<String> getClassType() {
		return classType;
	}

	public static ArrayList<String> getMode() {
		return mode;
	}

	public static ArrayList<String> getClassLocations() {
		return classLocations;
	}

	public static ArrayList<String> getHasLab() {
		return hasLab;
	}

	public static ArrayList<Integer> getAllCrhs() {
		return allCrhs;
	}

	public static ArrayList<ArrayList<Integer>> getLabCrns() {
		return labCrns;
	}

	public static ArrayList<ArrayList<String>> getLabLocations() {
		return labLocations;
	}

	public static ArrayList<Student> getStudents() {
		return students;
	}
}

//---------------------------
abstract class Student {
	//instance variables
	private String name;
	private String id;
	private double totalCost;

	public Student (String name, String id) {
		this.name = name;
		this.id = id;
		totalCost = 0;
	}

	abstract public void printInvoice();

	abstract public String getType();

	//getters
	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public double getTotalCost() {
		return totalCost;
	}

	//setters
	public void setName(String name) {
		this.name = name;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
}

//---------------------------
class UndergraduateStudent extends Student {
	//instance variables
	private ArrayList<Integer> undergradCrnsTaken;
	private double gpa;
	private boolean resident;

	private static int classIndex; //to access CRNs, CRHs, and CR_PREFIXES

    public UndergraduateStudent(String name, String id, ArrayList<Integer> undergradCrnsTaken, double gpa, boolean resident) {
        super(name, id);
		this.undergradCrnsTaken = undergradCrnsTaken;
		this.gpa = gpa;
		this.resident = resident;
    }
	
	@Override
	public void printInvoice() {
		double totalCost = 0;
		setTotalCost(totalCost);

		System.out.println("------------------------------------------------------------------------------------------------------------------\n");

		//display college information
        System.out.println("\t\tVALENCE COLLEGE");
        System.out.println("\t\tORLANDO FL 10101");
        System.out.println("\t\t-------------------------\n");
        
        //display name and id
        System.out.println("\t\tFee Invoice Prepared for Student:");
        System.out.printf("\t\t%s-%s", getId().toUpperCase(), getName().toUpperCase());

		//display first name, if resident or not, and if gpa is >= 3.5
		System.out.printf("\t(%s, a %s, has a gpa %s 3.5 gpa)\n\n", getName().substring(0, getName().indexOf(" ")),
			resident == true ? "FL resident" : "non-FL resident", 
			gpa > 3.5 ? "greater than" : gpa == 3.5 ? "equal to" : "less than");

		//display cost of 1 credit hour
        System.out.printf("\t\t1 Credit Hour = $%.2f\n\n", resident == true ? 120.25 : 120.25 * 2);
        
        //display table with CRN, CREDIT HOURS, and cost for class
        System.out.println("\t\tCRN\tCR_PREFIX\tCR_HOURS");
		for(int i = 0; i < undergradCrnsTaken.size(); ++i) {
			classIndex = ProjectDriver.getCrns().indexOf(undergradCrnsTaken.get(i));
			System.out.printf("\t\t%d\t%s\t\t", undergradCrnsTaken.get(i), ProjectDriver.getPrefixes().get(classIndex));
			System.out.printf("%d\t $ %.2f\n", ProjectDriver.getAllCrhs().get(classIndex), (resident == true ? 120.25 : 120.25 * 2) * ProjectDriver.getAllCrhs().get(classIndex));
			totalCost += ((resident == true ? 120.25 : 120.25 * 2) * ProjectDriver.getAllCrhs().get(classIndex));
		}
		System.out.println(); //spacing

		setTotalCost(getTotalCost() + 35 + totalCost); //calculate the total payment
		
        System.out.println("\t\t\t\tHealth & id fees $ 35.00\n"); //display health & id fees

        //display divider and total cost for attendance
        System.out.println("\t\t----------------------------------------");

        //check student eligibility for a discount and print appropriate total
        if(getTotalCost() > 500 && gpa >= 3.5) {
            System.out.printf("\t\t\t\t\t\t$ %.2f\n", getTotalCost()); //print pre-discount cost
            System.out.printf("\t\t\t\t\t\t-$ %.2f\n", getTotalCost() / 4); //print money saved from discount
            System.out.println("\t\t\t\t\t\t----------"); //print divider
			setTotalCost(getTotalCost() * 0.75); //apply 25% discount
        }
        
        System.out.printf("\t\t\t\tTotal Payments $ %.2f\n", getTotalCost()); //print final total
	}

	//getters
	public ArrayList<Integer> getUndergradCrnsTaken() {
		return undergradCrnsTaken;
	}

	public double getGpa() {
		return gpa;
	}

	public boolean getResident() {
		return resident;
	}

	public String getType() {
		return "Undergrad";
	}

	//setters
	public void setUndergradCrnsTaken(ArrayList<Integer> undergradCrnsTaken) {
		this.undergradCrnsTaken = undergradCrnsTaken;
	}

	public void setUndergradCrnsTaken(double gpa) {
		this.gpa = gpa;
	}

	public void setResident(boolean resident) {
		this.resident = resident;
	}
}

//---------------------------
abstract class GraduateStudent extends Student {
	private ArrayList<Integer> crn;

	public GraduateStudent(String name, String id, ArrayList<Integer> crn) { 
        //crn is the crn that the grad student is a teaching assistant for
		super(name, id);
		this.crn = crn;
	}

	//getters
	public ArrayList<Integer> getCrn() {
		return crn;
	}

	//setters
	public void setCrn(ArrayList<Integer> crn) {
		this.crn = crn;
	}
}

//---------------------------
class PhdStudent extends GraduateStudent {
	//instance variables
	private String advisor;
	private String researchSubject;

	public PhdStudent(String name, String id, String advisor, String researchSubject, ArrayList<Integer> crn) {
		//crn is the course number that the Phd student is a teaching assistant for
		super(name, id, crn);
		this.advisor = advisor;
		this.researchSubject = researchSubject;
	}

	@Override
	public void printInvoice() {
		System.out.println("------------------------------------------------------------------------------------------------------------------\n");

		//display college information
        System.out.println("\t\tVALENCE COLLEGE");
        System.out.println("\t\tORLANDO FL 10101");
        System.out.println("\t\t-------------------------\n");
        
        //display name and id
        System.out.println("\t\tFee Invoice Prepared for Student:");
        System.out.printf("\t\t%s-%s\n\n", getId().toUpperCase(), getName().toUpperCase());

        //display research subject and research fee
        System.out.println("\t\tRESEARCH");
		System.out.printf("\t\t%s\t\t\t $ 700.00\n\n", researchSubject);

		setTotalCost(getTotalCost() + 35 + 700); //set total cost

        System.out.println("\t\t\t\tHealth & id fees $ 35.00\n"); //display health & id fees

        //display divider and total cost for attendance
        System.out.println("\t\t----------------------------------------");

		//check student eligibility for a discount and print appropriate total
        if(getCrn().size() >= 2) {
            System.out.printf("\t\t\t\t\t\t$ %.2f\n", getTotalCost()); //print pre-discount cost
            System.out.printf("\t\t\t\t\t\t-$ %.2f\n", (getCrn().size() == 2) ? (getTotalCost() - (getTotalCost() * 0.5)) : (700)); //print money saved from discount
            System.out.println("\t\t\t\t\t\t----------"); //print divider
			setTotalCost(getTotalCost() - ((getCrn().size() == 2) ? (getTotalCost() - (getTotalCost() * 0.5)) : (700))); //apply 25% discount
        }
        
        System.out.printf("\t\t\t\tTotal Payments $ %.2f\n", getTotalCost()); //print final total
	}

	//getters
	public String getAdvisor() {
		return advisor;
	}

	public String getResearchSubject() {
		return researchSubject;
	}

	public String getType() {
		return "PhD";
	}

	//setters
	public void setAdvisor(String advisor) {
		this.advisor = advisor;
	}

	public void setResearchSubject(String researchSubject) {
		this.researchSubject = researchSubject;
	}
}

//---------------------------
class MsStudent extends GraduateStudent {
	//instance variables
	private ArrayList<Integer> gradCrnsTaken;

	private static int classIndex; //to access CRNs, CRHs, and CR_PREFIXES

	public MsStudent(String name, String id, ArrayList<Integer> gradCrnsTaken, ArrayList<Integer> crn) {
		//gradCoursesTaken is the array of the crns that the Ms student is taking
		//crn is the course number that the Phd student is a teaching assistant for

		super(name, id, crn);
		this.gradCrnsTaken = gradCrnsTaken;
	}

	@Override
	public void printInvoice() {
		System.out.println("------------------------------------------------------------------------------------------------------------------\n");

		//display college information
        System.out.println("\t\tVALENCE COLLEGE");
        System.out.println("\t\tORLANDO FL 10101");
        System.out.println("\t\t-------------------------\n");
        
        //display name and id
        System.out.println("\t\tFee Invoice Prepared for Student:");
        System.out.printf("\t\t%s-%s\n\n", getId().toUpperCase(), getName().toUpperCase());

        System.out.println("\t\t1 Credit Hour = $300.00\n"); //display cost of 1 credit hour
        
        //display table with CRN, CREDIT HOURS, and cost for class
        System.out.println("\t\tCRN\tCR_PREFIX\tCR_HOURS");
		for(int i = 0; i < gradCrnsTaken.size(); ++i) {
			classIndex = ProjectDriver.getCrns().indexOf(gradCrnsTaken.get(i));
			System.out.printf("\t\t%d\t%s\t\t", gradCrnsTaken.get(i), ProjectDriver.getPrefixes().get(classIndex));
			System.out.printf("%d\t $ %.2f\n", ProjectDriver.getAllCrhs().get(classIndex), 300.00 * ProjectDriver.getAllCrhs().get(classIndex));
		}
		System.out.println(); //spacing

		//calculate the total payment
		for(int i = 0; i < gradCrnsTaken.size(); ++i) {
			setTotalCost(getTotalCost() + ProjectDriver.getAllCrhs().get(ProjectDriver.getCrns().indexOf(gradCrnsTaken.get(i))) * 300.00);
		}
		setTotalCost(getTotalCost() + 35);

        System.out.println("\t\t\t\tHealth & id fees $ 35.00\n"); //display health & id fees

        //display divider and total cost for attendance
        System.out.println("\t\t----------------------------------------");
        
        System.out.printf("\t\t\t\tTotal Payments $ %.2f\n", getTotalCost()); //print final total
	}

	//getters
	public ArrayList<Integer> getGradCrnsTaken() {
		return gradCrnsTaken;
	}

	public String getType() {
		return "MS";
	}

	//setters
	public void setGradCrnsTaken(ArrayList<Integer> gradCrnsTaken) {
		this.gradCrnsTaken = gradCrnsTaken;
	}
}

class IdException extends Exception {
	public IdException(String errorMessage) {
		super(errorMessage);
	}
}