// your index number
// 200595E

//import libraries
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.mail.PasswordAuthentication;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class allRecipients {

    private String name;
    private String email;
    private String type;

    // static member to keep the count of the recipient objects created
    public static int recipientCount = 0;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public allRecipients(String type, String name, String email){
        this.type = type;
        this.name = name;
        this.email = email;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public static int getRecipientCount(){
        return recipientCount;
    }

}

interface birthdayWish {

    String wish();
    
}

class Official extends allRecipients{
    
    private String designation;
    
    public Official(String type, String name, String email, String designation){
        super(type, name, email);
        this.designation = designation;
        recipientCount+=1;
    }

    public String getDesignation(){
        return designation;
    }
}

class Office_friend extends allRecipients implements birthdayWish{

    private String designation;
    private String birthDay;

    public Office_friend(String type, String name, String email, String designation, String birthDay){
        super(type, name, email);
        this.designation = designation;
        this.birthDay = birthDay;
        recipientCount+=1;
    }
    public String getDesignation(){
        return designation;
    }
    public String getBirthDay(){
        return birthDay;
    }
    public String wish(){
        return "Wish you a Happy Birthday. Harsha";
    }
}

class Personal extends allRecipients implements birthdayWish{
    
    private String nickName;
    private String birthDay;
    
    public Personal(String type, String name, String nickName, String email, String birthDay){
        super(type, name, email);
        this.nickName = nickName;
        this.birthDay = birthDay;
        recipientCount += 1;
    }

    public String getNickName(){
        return nickName;
    }

    public String getBirthDay(){
        return birthDay;
    }

    public String wish(){
        return "Hugs and love on your birthday. Harsha";
    }
}

class ObjectMaker {

    private static ArrayList<allRecipients> allRecipientsList = new ArrayList<>();

    public void makeObject() throws FileNotFoundException{

        File myObj = new File("C:/Users/USER/Desktop/Email client/Project1/clientList.txt");
        Scanner myReader = new Scanner(myObj);

        // read data from clientList.txt file
        // create new objects using each new line in text file
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            makeOneObject(data);       
        }

        myReader.close();
    }

    // create one object using given data
    public void oneLastObjectMaker(String data){
        makeOneObject(data);
    }

    // create three types of objects using given string
    public void makeOneObject(String data){

        // get the type of the object
        String type = data.substring(0, data.indexOf(':'));

        if (type.equalsIgnoreCase("Official")){

            String person = data.substring(data.indexOf(":")+2, data.length());
            String[] details = person.split(",");
            String name = details[0];
            String email = details[1];
            String designation = details[2];

            allRecipientsList.add(new Official(type, name, email, designation));
        }
        else if (type.equalsIgnoreCase("Office_friend")){

            String person = data.substring(data.indexOf(":")+2, data.length());
            String[] details = person.split(",");
            String name = details[0];
            String email = details[1];
            String designation = details[2];
            String birthDay = details[3];

            allRecipientsList.add(new Office_friend(type, name, email, designation, birthDay));
        }
        else if (type.equalsIgnoreCase("Personal")){

            String person = data.substring(data.indexOf(":")+2, data.length());
            String[] details = person.split(",");
            String name = details[0];
            String nickName = details[1];
            String email = details[2];
            String birthDay = details[3];

            allRecipientsList.add(new Personal(type, name, nickName, email, birthDay));
        }
    }

    // return name of the person who have the birthday on given date
    public static String getPeopleWhoHaveBirthday(String date){

        String a = "";
        
        for (allRecipients recipient : allRecipientsList){
            if (recipient.getType().equalsIgnoreCase("Office_friend")){
                Office_friend office_friend = (Office_friend) recipient;    // upcasting
                if(office_friend.getBirthDay().equals(date)){
                    a+=office_friend.getName()+"\n";
                }
            }
            if (recipient.getType().equalsIgnoreCase("Personal")){
                Personal personal = (Personal) recipient;
                if(personal.getBirthDay().equals(date)){
                    a+=personal.getName()+"\n";
                } 
            }
        }
        return a;
    }

    // return list of allRecipient objects
    public ArrayList<allRecipients> getList(){
        return allRecipientsList;
    }
    
}

class fileWritter {

    // single object to write detail strings in to clientList.txt file
    private static fileWritter instance;

    private fileWritter(){}

    public static fileWritter getInstance() throws IOException{
        if (instance == null){
            instance = new fileWritter();
        }
        return instance;
    }
    
    // append given string in to clientList.txt text file
    public void write(String s) throws IOException{
        FileWriter file = new FileWriter("C:/Users/USER/Desktop/Email client/Project1/clientList.txt", true);
        BufferedWriter B = new BufferedWriter(file);
        B.write(s);
        B.newLine();
        B.close();
        file.close();
    }
}

class Sender{
    
    // single object to send emails
    private static Sender instance;

    private Sender(){}
    public static Sender getInstance(){
        if (instance == null){
            instance = new Sender();
        }
        return instance;
    }

    // send normal emails (option 2)
    public void sendCasualMail(String email, String subject, String content, String date) throws Exception{
        SendMail.sendmail(email, subject, content, date);
    }

    // input date format is yyyy/mm/dd
    // send birthday wishes to people who have birthdays on given date
    public void sendWishes(String date) throws Exception{

        // dateMD format is mm/dd
        String dateMD = date.substring(date.indexOf("/")+1, date.length());
        
        ObjectMaker obj = new ObjectMaker();

        // get allRecipientsList in ObjectMaker class
        ArrayList<allRecipients> list = obj.getList();

        // send birthday wishes
        for (allRecipients recipient : list){

            if (recipient.getType().equalsIgnoreCase("Office_friend")){
                Office_friend office_friend = (Office_friend) recipient;
                String monthAndDate = office_friend.getBirthDay().substring(office_friend.getBirthDay().indexOf("/")+1, office_friend.getBirthDay().length());
                if (monthAndDate.equals(dateMD)){
                    SendMail.sendmail(office_friend.getEmail(), "Birthday wish", office_friend.wish(), date);
                }
                
            }
            else if (recipient.getType().equalsIgnoreCase("Personal")){
                Personal personal = (Personal) recipient;
                String monthAndDate = personal.getBirthDay().substring(personal.getBirthDay().indexOf("/")+1, personal.getBirthDay().length());
                if (monthAndDate.equals(dateMD)){
                    SendMail.sendmail(personal.getEmail(), "Birthday wish", personal.wish(), date);

                }
            }
        }
    }
}

class SendMail {

	public static ArrayList<String> oldEmails = new ArrayList<>();
	public static ArrayList<String> newEmails = new ArrayList<>();

	public static void sendmail(String email, String subject, String content, String date) throws Exception {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		
		// email and app passward
		String myEmailAddress = "hsenarathna1999@gmail.com";
		String passward = "hgdqgytuhuknpoce";
		
		Session session = Session.getInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(myEmailAddress, passward);
			}
		});
		
		Message message = prepareMessage(session, myEmailAddress, email, subject, content);
		Transport.send(message);
		// System.out.println("Email sent.");
		newEmails.add(email+","+subject+","+content+","+date+"\n");

		
	}
	
	private static Message prepareMessage(Session session, String myEmailAddress, String email, String subject, String content) {
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(myEmailAddress));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
			message.setSubject(subject);
			message.setText(content);
			return message;
		}catch (Exception e) {}
		return null;
	}
	
}


class emailSerialization {

    static void serialization(ArrayList<String> data) throws IOException, ClassNotFoundException, FileNotFoundException {

        // get all the emails sent in the past to the oldEmails ArrayList
        deserialization();

        // add newly sent emails in the newEmails ArrayList to the oldEmails ArrayList
        for (String a: SendMail.newEmails){
            SendMail.oldEmails.add(a);
        }

        // serialize oldEmails ArrayList
        String savefile = "emailSer.ser";
        FileOutputStream file = new FileOutputStream(savefile);
        ObjectOutputStream out = new ObjectOutputStream(file);
        out.writeObject(SendMail.oldEmails);
        out.close();
        file.close();
    }

    static ArrayList<String> deserialization() throws ClassNotFoundException, IOException, FileNotFoundException {

        // deserialize emailSer.ser file's data in to oldEmails ArrayList
        String savefile = "emailSer.ser";
        FileInputStream file = new FileInputStream(savefile);
        ObjectInputStream in = new ObjectInputStream(file);
        SendMail.oldEmails = (ArrayList<String>) in.readObject();
        in.close();
        file.close();
        return SendMail.oldEmails;

    }
}

public class Email_Client {

    public static void main(String[] args) throws Exception{

        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");  
        LocalDateTime now = LocalDateTime.now(); 
        String date = dtf.format(now);  

        try {
            // create objects when code is running
            ObjectMaker ObjMaker = new ObjectMaker();
            ObjMaker.makeObject();
        } catch (Exception e) {
            File newFile = new File("clientList.txt");
            newFile.createNewFile();
        }
        
        // search through objects and send birthday wishes
        System.out.println("Birth day wishes are sending, please wait.");
        Sender sendBirtdayWishes = Sender.getInstance();
        sendBirtdayWishes.sendWishes(date);
        System.out.println("All birth day wishes completed.");

        while(running){
            System.out.println("Enter option type: \n"
                + "1 - Adding a new recipient\n"
                + "2 - Sending an email\n"
                + "3 - Printing out all the recipients who have birthdays\n"
                + "4 - Printing out details of all the emails sent\n"
                + "5 - Printing out the number of recipient objects in the application\n"
                + "6 - Exit program");

            int option = scanner.nextInt();
            scanner.nextLine();
            
            switch(option){
                case 1:
                    
                    // add new recipients to clientList.txt text file
                    System.out.println("Input:\n"
                    +"official: <name>, <email>,<designation>\n"
                    +"office_friend: <Name>,<Email>,<Designation>,<BirthDay>\n"
                    +"personal: <Name>,<NickName>,<Email>,<Birthday>");
                    String detail = scanner.nextLine();
                    fileWritter write = fileWritter.getInstance();
                    write.write(detail);
                    ObjectMaker newObjectMaker = new ObjectMaker();

                    // create a new object using new recipient detail
                    newObjectMaker.oneLastObjectMaker(detail);
                    break;

                case 2:
                
                    // input format - email, subject, content
                    // code to send an email

                    String email = scanner.nextLine();
                    String subject = scanner.nextLine();
                    String content = scanner.nextLine();
                    Sender send = Sender.getInstance();
                    System.out.println("Sending email.Please wait.");
                    send.sendCasualMail(email, subject, content, date);
                    System.out.println("Email sent.");
                    break;

                case 3:
                    
                    // input format - yyyy/MM/dd (ex: 2018/09/17)
                    // print recipients who have birthdays on the given date

                    String inputDate = scanner.nextLine();
                    System.out.println(ObjectMaker.getPeopleWhoHaveBirthday(inputDate));

                    break;
                case 4:

                    // input format - yyyy/MM/  dd (ex: 2018/09/17)
                    // print the details of all the emails sent on the input date

                    // deserialize old emails from emailSer.ser
                    emailSerialization.deserialization();
                    String inputDateToPrintEmails = scanner.nextLine();

                    // iterate oldEmails ArrayList
                    // compare input date and sent date 
                    for (String emailDetail : SendMail.oldEmails){
                        String [] details  = emailDetail.split(",");
                        if (details[3].strip().equals(inputDateToPrintEmails)){

                            // print details of the email which was sent on the given date
                            System.out.println("Email :- "+details[0]+"\nSubject :- "+details[1]+"\nContent :- "+details[2]+"\n");
                        }
                    }
                    break;

                case 5:
                
                    // code to print the number of recipient objects in the application

                    System.out.println(allRecipients.getRecipientCount());
                    break;

                case 6:

                    // serialization of the messages and end the program

                    emailSerialization.serialization(SendMail.newEmails);
                    running = false;
                    break;    
            }
        }   
        scanner.close();
    }
}