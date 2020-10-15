import java.util.*;
import java.util.Scanner;
import java.sql.*;

/**
 *  Poised project manager is a simple program which could be used in any project management scenario that
 *  deals in building/construction. 
 * 
 *  Various options are available to the user from adding new projects to searching for any pending projects. 
 *  The menu system makes it easy and clear to navigate and perform all the relevant tasks.
 * 
 *  New additions to the program is the ability to read from a SQL database. All the data is read from the tables
 *  and written to the tables too.
 * 
 * @author George
 * @version 2.00
 */
public class ProjectManager { //ProjectManager class contains the Main class which has all of the driver code to make the program run. 

	public static void main(String[] args) {
		String projname, type, address, projnum, erf, date, projdate, pstatus, status, px, clx, comdate;
		double totalfee, atd, cost;
		int currentdate;

		String clientName, clientEmail, clientAddress, clientTelNum, projNum;
		String contName, contEmail, contAddress, contTelNum;
		String archName, archEmail, archAddress, archTel;

		int answer, choice, choice1, option1, select;

		//menu options

		Scanner sc = new Scanner(System.in);
		System.out.println("==================================================="
				+ "\n========== Please Choose an option below =========="
				+ "\n1. Projects "
				+ "\n2. New Contractor "
				+ "\n3. New Architect "
				+ "\n4. Edit "
				+ "\n5. Exit"
				+ "\n===================================================");
		answer = sc.nextInt();

		try {
			Connection connection = DriverManager.getConnection ("jdbc:mysql://localhost:3306/pms_db?useSSL=false", "root", "warner2015");
			Statement statement = connection.createStatement ();
			ResultSet arc;
			ResultSet cli;
			ResultSet con;
			ResultSet pro;
			int rowsAffected;
			arc = statement.executeQuery("SELECT Name FROM architect");
			cli = statement.executeQuery("SELECT Name FROM client");
			con = statement.executeQuery("SELECT Name FROM contractor");
            pro = statement.executeQuery("SELECT Project_Number, Project_Name FROM project");
            
			// Sub-menu for option 1
			if (answer == 1) {
				System.out.println("==================================================="
						+ "\nPlease choose an option below from the submenu:"
						+ "\n1. New Project "
						+ "\n2. Finalize Project "
						+ "\n3. View All Projects "
						+ "\n4. View all Incompleted Projects "
						+ "\n5. View all Overdue Projects "
						+ "\n6. View Any Project");
				select = sc.nextInt();

				// New project module for capturing a new project
				if (select == 1) {
					sc.nextLine();
					System.out.println("Enter in the project number:");
					projnum = sc.nextLine();
					System.out.println("Enter in the project name:");
					projname = sc.nextLine();
					System.out.println("Enter in the building type eg(Apartment, House):");
					type = sc.nextLine();
					System.out.println("Enter in the physical address of the building:");
					address = sc.nextLine();
					System.out.println("Enter in the ERF number:");
					erf = sc.nextLine();
					System.out.println("Enter in the initial date(YYYMMDD):");
					projdate = sc.nextLine();
					System.out.println("Enter in the deadline date(YYYYMMDD):");
					date = sc.nextLine();
					System.out.println("Enter in the total amount of the project:");
					totalfee = sc.nextDouble();
					System.out.println("Enter in the amount paid in to date:");
					atd = sc.nextDouble();
					System.out.println("Please enter the current date(YYYYMMDD):");
					currentdate = sc.nextInt();

					// Calculation for the total cost owing by the client 
					cost = totalfee - atd;
					if (cost == 0) { 
						pstatus = "Complete";
					}else {
						pstatus = "Incomplete";
					}
					
					// Checks if project is overdue or not
					int d = Integer.parseInt(date);
					if (currentdate > d) {
						status = "Overdue";
					}else {
						status = "Within due date";
					}

					rowsAffected = statement.executeUpdate("INSERT INTO project VALUES('"+projnum+"','"+projname+"','"+type+"','"+ address+ "','"+erf+"','"+projdate+"','"+date+"','"+totalfee+"','"+atd+"','"+cost+"','"+pstatus+"','"+status+"')");
					System.out.println("Query complete," + rowsAffected  + " row added.");
					printAllFromTablepro (statement);

					// Captures all the details of the client
					sc.nextLine();
					System.out.println("Enter in the Clients Name:");
					clientName = sc.nextLine().toUpperCase();
					System.out.println("Enter in the Email Address of the Client:");
					clientEmail = sc.nextLine();
					System.out.println("Enter in the Physical Address of the Client:");
					clientAddress = sc.nextLine();
					System.out.println("Enter in the Clients telephone number:");
					clientTelNum = sc.nextLine();
					System.out.println("Enter in the Project number for this Client: ");
					projNum = sc.nextLine();
					rowsAffected = statement.executeUpdate("INSERT INTO client VALUES('"+clientName+"','"+clientEmail+"','"+clientAddress+"','"+ clientTelNum+ "','"+projNum+"')");
					System.out.println("Query complete," + rowsAffected  + " row added.");
					printAllFromTablecli (statement);
				}
				
				// Block for finalizing the project and creates an invoice
				// also if the balance is zero then it displays no invoice needed
				if (select == 2) {
					sc.nextLine();
					System.out.println("==================================================="
							+ "\nDo you wish to finalize the project? "
							+ "\n1. Yes "
							+ "\n2. No "
                            + "===================================================");
					option1 = sc.nextInt();
					if (option1 == 1) {
						printAllFromTablepro(statement);
						sc.nextLine();
						System.out.println("Please enter the project number of the project you wish to finalize: ");
						px = sc.nextLine().toUpperCase();
						pro = statement.executeQuery("SELECT Project_Number, Project_Name, Building_Type, Physical_Address, ERF_Number, Project_Initial_Date, Deadline_Date, Total_Fee, Total_Amount_Paid, Amount_Outstanding, Project_Status, Deadline_Status FROM project WHERE Project_Number = '"+px+"'" );
						printAllFromTablecli(statement);
						System.out.println("Please enter the name of the client of the project selected above: ");
						clx = sc.nextLine().toUpperCase();
						while (pro.next()){
							System.out.println("Please enter the completion date(YYYYMMDD): ");
							comdate = sc.nextLine();
							String PROJECTNUMBER = pro.getString("Project_Number");
							String PROJECTNAME = pro.getString("Project_Name");
							Double COST = pro.getDouble("Amount_Outstanding");
							if (COST > 0) {
								System.out.println("Please see Invoice below:\nINVOICE\nProject Number: " +PROJECTNUMBER+"\nProject Name: "+PROJECTNAME+"\nAmount Outstanding: "+COST+"\nProject Finalized: Yes\nCompletion Date: "+ comdate);				
								rowsAffected = statement.executeUpdate("UPDATE project SET Project_Status = 'Complete' WHERE Project_Number='"+px+"'");
								cli = statement.executeQuery("SELECT Name, Email_Address, Physical_Address,Telephone_Number FROM client WHERE Name = '"+clx+"'");
								while (cli.next()) {
									String NAME = cli.getString("Name");
									String EMAIL = cli.getString("Email_Address");
									String ADDRESS = cli.getString("Physical_Address");
									String TELEPHONE = cli.getString("Telephone_Number");
									System.out.println("Name: "+NAME+"\nEmail Address: "+EMAIL+"\nPhysical Address: "+ADDRESS+"\nTelephone Number: "+TELEPHONE);
									break;
								}
								break;
							}
							if (COST == 0) {
								rowsAffected = statement.executeUpdate("UPDATE project SET Project_Status = 'Complete' WHERE Project_Number='"+px+"'");
								System.out.println("Balance is 0 - No Invoice required!!");
							}
						}
					}

					if (option1 == 2) {
						System.out.println("Thank you the project is marked: completed.");
					}

				}

				// This block lets the user view all projects 
				if (select == 3) {

					sc.nextLine();
					System.out.println("==================================================="
							+ "\nDo you wish to view all Projects "
							+ "\n1. Yes "
							+ "\n2. No "
							+ "\n===================================================");
					choice = sc.nextInt();
					if (choice == 1) {
						pro = statement.executeQuery("SELECT Project_Number, Project_Name, Building_Type, Physical_Address, ERF_Number, Project_Initial_Date, Deadline_Date, Total_Fee, Total_Amount_Paid, Amount_Outstanding, Project_Status, Deadline_Status FROM project");
						printAllFromTablepro (statement);

					}
					else if (choice == 2) {
						System.out.println("==========You Have Exited==========");
					}
				}

				// This block lets the user view all incomplete projects
				if (select == 4) {
					sc.nextLine();
					System.out.println("==================================================="
							+ "\nDo you wish to view all Incomplete Projects "
							+ "\n1. Yes "
							+ "\n2. No ");
					choice = sc.nextInt();
					if (choice == 1) {
						pro = statement.executeQuery("SELECT Project_Number, Project_Name, Building_Type, Physical_Address, ERF_Number, Project_Initial_Date, Deadline_Date, Total_Fee, Total_Amount_Paid, Amount_Outstanding, Project_Status, Deadline_Status FROM project WHERE Project_Status = 'Incomplete'");
						while (pro.next()) {
							String PROJECTNUMBER = pro.getString("Project_Number");
							String PROJECTNAME = pro.getString("Project_Name");
							String TYPE = pro.getString("Building_Type");
							String ADDRESS = pro.getString("Physical_Address");
							String ERF = pro.getString("ERF_Number");
							String IDATE = pro.getString("Project_Initial_Date");
							String DATE = pro.getString("Deadline_Date");
							Double TOTALFEE = pro.getDouble("Total_Fee");
							Double ATD = pro.getDouble("Total_Amount_Paid");
							Double AOUT = pro.getDouble("Amount_Outstanding");
							String PSTATUS = pro.getString("Project_Status");
							String DSTATUS = pro.getString("Deadline_Status");
							System.out.println("Please see all Incomplete Projects Below:\nProject Number: " +PROJECTNUMBER+" Project Name: " +PROJECTNAME+" Building Type: " +TYPE+" Physical Address: " +ADDRESS+" ERF Number: " +ERF+" Project Initial Date: " +IDATE+" Deadline Date: " +DATE+" Total Fee: " +TOTALFEE+" Amount Paid to Date: " +ATD+" Amount Outstanding: " +AOUT+" Project Status: " +PSTATUS+" Deadline Status: " +DSTATUS);
						}
					}
                }
                
				// Block lets the user view all overdue projects
				if (select == 5) {
					sc.nextLine();
					System.out.println("Do you wish to view all Overdue Projects "
							+ "\n1. Yes "
							+ "\n2. No ");
					choice = sc.nextInt();
					if (choice == 1) {
						pro = statement.executeQuery("SELECT Project_Number, Project_Name, Building_Type, Physical_Address, ERF_Number, Project_Initial_Date, Deadline_Date, Total_Fee, Total_Amount_Paid, Amount_Outstanding, Project_Status, Deadline_Status FROM project WHERE Deadline_Status = 'Overdue'");
						while (pro.next()) {
							String PROJECTNUMBER = pro.getString("Project_Number");
							String PROJECTNAME = pro.getString("Project_Name");
							String TYPE = pro.getString("Building_Type");
							String ADDRESS = pro.getString("Physical_Address");
							String ERF = pro.getString("ERF_Number");
							String IDATE = pro.getString("Project_Initial_Date");
							String DATE = pro.getString("Deadline_Date");
							Double TOTALFEE = pro.getDouble("Total_Fee");
							Double ATD = pro.getDouble("Total_Amount_Paid");
							Double AOUT = pro.getDouble("Amount_Outstanding");
							String PSTATUS = pro.getString("Project_Status");
							String DSTATUS = pro.getString("Deadline_Status");
							System.out.println("Please see all Overdue Projects Below:\nProject Number: " +PROJECTNUMBER+" Project Name: " +PROJECTNAME+" Building Type: " +TYPE+" Physical Address: " +ADDRESS+" ERF Number: " +ERF+" Project Initial Date: " +IDATE+" Deadline Date: " +DATE+" Total Fee: " +TOTALFEE+" Amount Paid to Date: " +ATD+" Amount Outstanding: " +AOUT+" Project Status: " +PSTATUS+" Deadline Status: " +DSTATUS);
						}
					}
				}

				// Block lets user view any project
				if (select == 6) {
					sc.nextLine();
					System.out.println("==================================================="
							+ "\nDo you wish to view any Project "
							+ "\n1. Yes "
							+ "\n2. No ");
					choice = sc.nextInt();
					if (choice == 1) {
						sc.nextLine();
						System.out.println("Please enter in the Project Number you wish to view: ");
						px = sc.nextLine();
						pro = statement.executeQuery("SELECT Project_Number, Project_Name, Building_Type, Physical_Address, ERF_Number, Project_Initial_Date, Deadline_Date, Total_Fee, Total_Amount_Paid, Amount_Outstanding, Project_Status, Deadline_Status FROM project WHERE Project_Number = '"+px+"'");
						while (pro.next()) {
							String PROJECTNUMBER = pro.getString("Project_Number");
							String PROJECTNAME = pro.getString("Project_Name");
							String TYPE = pro.getString("Building_Type");
							String ADDRESS = pro.getString("Physical_Address");
							String ERF = pro.getString("ERF_Number");
							String IDATE = pro.getString("Project_Initial_Date");
							String DATE = pro.getString("Deadline_Date");
							Double TOTALFEE = pro.getDouble("Total_Fee");
							Double ATD = pro.getDouble("Total_Amount_Paid");
							Double AOUT = pro.getDouble("Amount_Outstanding");
							String PSTATUS = pro.getString("Project_Status");
							String DSTATUS = pro.getString("Deadline_Status");
							System.out.println("Please see Project selected Below:\nProject Number: " +PROJECTNUMBER+" Project Name: " +PROJECTNAME+" Building Type: " +TYPE+" Physical Address: " +ADDRESS+" ERF Number: " +ERF+" Project Initial Date: " +IDATE+" Deadline Date: " +DATE+" Total Fee: " +TOTALFEE+" Amount Paid to Date: " +ATD+" Amount Outstanding: " +AOUT+" Project Status: " +PSTATUS+" Deadline Status: " +DSTATUS);
						}
					}	
				}
			}
				
			// Block captures the contractors details
			if (answer == 2) {

				sc.nextLine();
				System.out.println("Enter in the Contractors Name:");
				contName = sc.nextLine();
				System.out.println("Enter in the Email Address of the Contractor:");
				contEmail = sc.nextLine();
				System.out.println("Enter in the Physical Address of the Contractor:");
				contAddress = sc.nextLine();
				System.out.println("Enter in the Contractors telephone number:");
				contTelNum = sc.nextLine();

				rowsAffected = statement.executeUpdate("INSERT INTO contractor VALUES('"+contName+"','"+contEmail+"','"+contAddress+"','"+ contTelNum+ "')");
				System.out.println("Query complete," + rowsAffected  + " row added.");
				printAllFromTablecon (statement);


			}

			// Block captures the architects details
			if (answer == 3) {
				sc.nextLine();	
				System.out.println("Enter in the Architects Name: ");
				archName = sc.nextLine();
				System.out.println("Enter in the Email Address of the Architect: ");
				archEmail = sc.nextLine();
				System.out.println("Enter in the Physical Address of the Architect: ");
				archAddress = sc.nextLine();
				System.out.println("Enter in the Architects telephone number: ");
				archTel = sc.nextLine();

				rowsAffected = statement.executeUpdate("INSERT INTO architect VALUES('"+archName+"','"+archEmail+"','"+archAddress+"','"+ archTel+ "')");
				System.out.println("Query complete," + rowsAffected  + " row added.");
				printAllFromTablearc (statement);

			}	

			// Block gives user the edit option for updating the project,client,architect and contractors details
			if (answer == 4) {
				sc.nextLine();
				System.out.println("==================================================="
						+ "\n Please select what you would like to edit from the submenu below:"
						+ "\n1. Project Details"
						+ "\n2. Client Details"
						+ "\n3. Architect Details"
						+ "\n4. Contractors Details ");
				choice = sc.nextInt();

				if (choice == 1) {

					sc.nextLine();
					System.out.println("==================================================="
							+ "\n What would you like to update?"
							+ "\n1. Project number"
							+ "\n2. Project Name"
							+ "\n3. Building Type"
							+ "\n4. Project Physical Address"
							+ "\n5. ERF Number"
							+ "\n6. Project Initial Date"
							+ "\n7. Project Deadline Date"
							+ "\n8. Total Amount Paid");
					choice1 = sc.nextInt();
					if(choice1 == 1) {
						sc.nextLine();
						System.out.println("\nPlease enter the number of the Project you wish to update: ");
						String oldNum = sc.nextLine();
						System.out.println("Please enter in the new number of the Project: ");
						String newNum = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE project SET Project_Number= '"+newNum+"' WHERE Project_Number='"+oldNum+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablepro (statement);
					}
					if(choice1 == 2) {
						printAllFromTablepro (statement);
						sc.nextLine();
						System.out.println("\nPlease enter the Project Number you wish to update: ");
						px = sc.nextLine().toUpperCase();
						System.out.println("Please enter in the new name of the Project: ");
						String newName = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE project SET Project_Name= '"+newName+"'WHERE Project_Number='"+px+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablepro (statement);
					}
					if(choice1 == 3) {
						printAllFromTablepro (statement);
						sc.nextLine();
						System.out.println("\nPlease enter the Project Number you wish to update: ");
						px = sc.nextLine().toUpperCase();
						System.out.println("Please enter in the new Buidling Type of the Project: ");
						String newType = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE project SET Building_Type= '"+newType+"' WHERE Project_Number='"+px+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablepro (statement);
					}
					if(choice1 == 4) {
						printAllFromTablepro (statement);
						sc.nextLine();
						System.out.println("\nPlease enter the Project Number you wish to update: ");
						px = sc.nextLine().toUpperCase();
						System.out.println("Please enter the Physical Address of the Project you wish to update: ");
						String oldAdd = sc.nextLine();
						System.out.println("Please enter in the new Physical Address of the Project: ");
						String newAdd = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE project SET Physical_Address= '"+newAdd+"' WHERE Physical_Address='"+oldAdd+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablepro (statement);
					}
					if(choice1 == 5) {
						printAllFromTablepro (statement);
						sc.nextLine();
						System.out.println("\nPlease enter the Project Number you wish to update: ");
						px = sc.nextLine().toUpperCase();
						System.out.println("Please enter in the new ERF NUmber of the Project: ");
						String newERF = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE project SET ERF_Number= '"+newERF+"' WHERE Project_Number='"+px+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablepro (statement);
					}
					if(choice1 == 6) {
						printAllFromTablepro (statement);
						sc.nextLine();
						System.out.println("\nPlease enter the Project Number you wish to update: ");
						px = sc.nextLine().toUpperCase();
						System.out.println("Please enter in the new Initial date of the Project: ");
						String newIDATE = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE project SET Project_Initial_Date= '"+newIDATE+"' WHERE Project_Number='"+px+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablepro (statement);
					}
					if(choice1 == 7) {
						printAllFromTablepro (statement);
						sc.nextLine();
						System.out.println("\nPlease enter the Project Number you wish to update: ");
						px = sc.nextLine().toUpperCase();
						System.out.println("Please enter in the new Deadline Date of the Project: ");
						String newDDATE = sc.nextLine();
						System.out.println("Please enter the current date(YYYYMMDD):");
						int currdate = sc.nextInt();
						int dt = Integer.parseInt(newDDATE);
						if (currdate > dt) {
							status = "Overdue";
						}else {
							status = "Within due date";
						}
						rowsAffected = statement.executeUpdate("UPDATE project SET Deadline_Date= '"+newDDATE+"',Deadline_Status='"+status+"' WHERE Project_Number='"+px+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablepro (statement);
					}
					if(choice1 == 8) {
						printAllFromTablepro (statement);
						sc.nextLine();
						System.out.println("\nPlease enter the Project Number you wish to update: ");
						px = sc.nextLine().toUpperCase();
						System.out.println("Please enter in the new Amount to be Paid of the Project: ");
						Double newATD = sc.nextDouble();
						System.out.println("Please enter in the Total Fee of the selected project: ");
						Double totfee = sc.nextDouble();
						Double Cost = totfee - newATD;
						if (Cost == 0) { 
							pstatus = "Complete";
						}else {
							pstatus = "Incomplete";
						}
						rowsAffected = statement.executeUpdate("UPDATE project SET Amount_Outstanding= '"+Cost+"',Total_Amount_Paid= '"+newATD+"',Project_Status = '"+pstatus+"'WHERE Project_Number='"+px+"'");

						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablepro (statement);
					}
				}
				if (choice == 2) {

					sc.nextLine();
					System.out.println("==================================================="
							+ "\n What would you like to update?"
							+ "\n1. Client name"
							+ "\n2. Client Email Address"
							+ "\n3. Client Physical Address"
							+ "\n4. Client Telephone Number"
							+ "\n5. Client Project Number");
					choice1 = sc.nextInt();
					if(choice1 == 1) {
						printAllFromTablecli (statement);
						sc.nextLine();
						System.out.println("Please enter the name of the Client you wish to update: ");
						String oldName = sc.nextLine();
						System.out.println("Please enter in the new name of the Client: ");
						String newName = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE client SET Name= '"+newName+"' WHERE Name='"+oldName+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablecli (statement);
					}
					if(choice1 == 2) {
						printAllFromTablecli (statement);
						sc.nextLine();
						System.out.println("Please enter the Email Address of the Client you wish to update: ");
						String oldEmail = sc.nextLine();
						System.out.println("Please enter in the new Email Address of the Client: ");
						String newEmail = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE client SET Email_Address= '"+newEmail+"' WHERE Email_Address='"+oldEmail+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablecli (statement);
					}
					if(choice1 == 3) {
						printAllFromTablecli (statement);
						sc.nextLine();
						System.out.println("Please enter the Physical Address of the Client you wish to update: ");
						String oldAdd = sc.nextLine();
						System.out.println("Please enter in the new Physical Address of the Client: ");
						String newAdd = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE client SET Physical_Address= '"+newAdd+"' WHERE Physical_Address='"+oldAdd+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablecli (statement);
					}
					if(choice1 == 4) {
						printAllFromTablecli (statement);
						sc.nextLine();
						System.out.println("Please enter the Telephone Number of the Client you wish to update: ");
						String oldTel = sc.nextLine();
						System.out.println("Please enter in the new Telephone Number of the Client: ");
						String newTel = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE client SET Telephone_Number= '"+newTel+"' WHERE Telephone_Number='"+oldTel+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablecli (statement);
					}
					if(choice1 == 5) {
						printAllFromTablecli (statement);
						sc.nextLine();
						System.out.println("Please enter the Project Number of the Client you wish to update: ");
						String oldPro = sc.nextLine();
						System.out.println("Please enter in the new Project Number of the Client: ");
						String newPro = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE client SET Project_Number= '"+newPro+"' WHERE Project_Number='"+oldPro+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablecli (statement);
					}
				}
				if (choice == 3) {

					sc.nextLine();
					System.out.println("==================================================="
							+ "\n What would you like to update?"
							+ "\n1. Architect name"
							+ "\n2. Architect Email Address"
							+ "\n3. Architect Physical Address"
							+ "\n4. Architect Telephone Number");
					choice1 = sc.nextInt();
					if(choice1 == 1) {
						sc.nextLine();
						System.out.println("Please enter the name of the Architect you wish to update: ");
						String oldName = sc.nextLine();
						System.out.println("Please enter in the new name of the Architect: ");
						String newName = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE architect SET Name= '"+newName+"' WHERE Name='"+oldName+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablearc (statement);
					}
					if(choice1 == 2) {
						sc.nextLine();
						System.out.println("Please enter the Email Address of the Architect you wish to update: ");
						String oldEmail = sc.nextLine();
						System.out.println("Please enter in the new Email Address of the Architect: ");
						String newEmail = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE architect SET Email_Address= '"+newEmail+"' WHERE Email_Address='"+oldEmail+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablearc (statement);
					}
					if(choice1 == 3) {
						sc.nextLine();
						System.out.println("Please enter the Physical Address of the Architect you wish to update: ");
						String oldAdd = sc.nextLine();
						System.out.println("Please enter in the new Physical Address of the Architect: ");
						String newAdd = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE architect SET Physical_Address= '"+newAdd+"' WHERE Physical_Address='"+oldAdd+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablearc (statement);
					}
					if(choice1 == 4) {
						sc.nextLine();
						System.out.println("Please enter the Telephone Number of the Architect you wish to update: ");
						String oldTel = sc.nextLine();
						System.out.println("Please enter in the new Telephone Number of the Architect: ");
						String newTel = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE architect SET Telephone_Number= '"+newTel+"' WHERE Telephone_Number='"+oldTel+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablearc (statement);
					}
				}

				if (choice == 4) {

					sc.nextLine();
					System.out.println("==================================================="
							+ "\n What would you like to update?"
							+ "\n1. Contractors name"
							+ "\n2. Contractors Email Address"
							+ "\n3. Contractors Physical Address"
							+ "\n4. Contractors Telephone Number");
					choice1 = sc.nextInt();
					if(choice1 == 1) {
						sc.nextLine();
						System.out.println("Please enter the name of the contractors you wish to update: ");
						String coName = sc.nextLine();
						System.out.println("Please enter in the new name of the contractor: ");
						String newName = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE contractor SET Name= '"+newName+"' WHERE Name='"+coName+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablecon (statement);
					}
					if(choice1 == 2) {
						sc.nextLine();
						System.out.println("Please enter the Email Address of the contractors you wish to update: ");
						String coEmail = sc.nextLine();
						System.out.println("Please enter in the new Email Address of the contractor: ");
						String newEmail = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE contractor SET Email_Address= '"+newEmail+"' WHERE Email_Address='"+coEmail+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablecon (statement);
					}
					if(choice1 == 3) {
						sc.nextLine();
						System.out.println("Please enter the Physical Address of the contractors you wish to update: ");
						String coAdd = sc.nextLine();
						System.out.println("Please enter in the new Physical Address of the contractor: ");
						String newAdd = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE contractor SET Physical_Address= '"+newAdd+"' WHERE Physical_Address='"+coAdd+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablecon (statement);
					}
					if(choice1 == 4) {
						sc.nextLine();
						System.out.println("Please enter the Telephone Number of the contractors you wish to update: ");
						String coTel = sc.nextLine();
						System.out.println("Please enter in the new Telephone Number of the contractor: ");
						String newTel = sc.nextLine();
						rowsAffected = statement.executeUpdate("UPDATE contractor SET Telephone_Number= '"+newTel+"' WHERE Telephone_Number='"+coTel+"'");
						System.out.println("Query complete," + rowsAffected  + " row updated.");
						printAllFromTablecon (statement);
					}
				}
			}	

			// Exit option from program
			if (answer == 5) {
				System.out.println("\n==================================================="
						+ "You have exited.\nGoodbye!!"
						+ "\n===================================================");
			}
			arc.close ();
			cli.close();
			con.close();
			pro.close();
			statement.close ();
			connection.close ();
			sc.close();
			
			
		} catch ( SQLException e ) {

			e.printStackTrace ();
		}
	}

		
	
/**
 * Method printing all values in all rows for each table.
 *
 * @param statement object for executing an SQL statement
 * @return 
 * @throws SQLException
 */
	public static void printAllFromTablearc ( Statement statement ) throws SQLException {
		ResultSet arc = statement.executeQuery ( "SELECT Name, Email_Address, Physical_Address, Telephone_Number FROM architect" );

		while (arc.next()) {
			System.out.println (
					arc.getString("Name") + ", "
							+ arc.getString("Email_Address") + ", "
							+ arc.getString("Physical_Address") + ", "
							+ arc.getString("Telephone_Number")
					);
		}
	}
/**
 * Block for client
 * 
 * @param statement object for executing an SQL statement 
 *
 * @return 
 * @throws SQLException
 */
	public static void printAllFromTablecli ( Statement statement ) throws SQLException {
		ResultSet cli = statement.executeQuery("SELECT Name, Email_Address, Physical_Address, Telephone_Number, Project_Number FROM client" );

		while (cli.next()) {
			System.out.println (
					cli.getString("Name") + ", "
							+ cli.getString("Email_Address") + ", "
							+ cli.getString("Physical_Address") + ", "
							+ cli.getString("Telephone_Number") + ", "
							+ cli.getString("Project_Number")
					);
		}
	}
/**
 * Block for contractor
 * 
 * @param statement object for executing an SQL statement 
 * @return 
 * @throws SQLException
 */
	public static void printAllFromTablecon ( Statement statement ) throws SQLException {
		ResultSet con = statement.executeQuery("SELECT Name, Email_Address, Physical_Address, Telephone_Number FROM contractor" );

		while (con.next()) {
			System.out.println (
					con.getString("Name") + ", "
							+ con.getString("Email_Address") + ", "
							+ con.getString("Physical_Address") + ", "
							+ con.getString("Telephone_Number")
					);
		}
	}
/**
 * Block for Project
 * 
 * @param statement object for executing an SQL statement 
 * @return 
 * @throws SQLException
 */
	public static void printAllFromTablepro ( Statement statement ) throws SQLException {

		ResultSet pro = statement.executeQuery("SELECT Project_Number, Project_Name, Building_Type, Physical_Address, ERF_Number, Project_Initial_Date, Deadline_Date, Total_Fee, Total_Amount_Paid, Amount_Outstanding, Project_Status, Deadline_Status FROM project" );

		while (pro.next()) {
			System.out.println (
					pro.getString("Project_Number") + ", "
							+ pro.getString("Project_Name") + ", "
							+ pro.getString("Building_Type") + ", "
							+ pro.getString("Physical_Address") + ", "
							+ pro.getString("ERF_Number") + ", "
							+ pro.getString("Project_Initial_Date") + ", "
							+ pro.getString("Deadline_Date") + ", "
							+ pro.getDouble("Total_Fee") + ", "
							+ pro.getDouble("Total_Amount_Paid") + ", "
							+ pro.getDouble("Amount_Outstanding") + ", "
							+ pro.getString("Project_Status") + ", "
							+ pro.getString("Deadline_Status")
					);
		}
	}
}
