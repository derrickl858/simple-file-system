package Assignment1;

import java.security.MessageDigest;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.ArrayList;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.Console;
import java.io.FileNotFoundException;

public class FileServer {

    //variable to check if user is authenticated
    private boolean authenticated = false;
    private user currUser;

    //Array for list of users
    private ArrayList<user> userList = new ArrayList<>();

    //Array for list of files 
    private ArrayList<Filelevel> fileList = new ArrayList<>();

    public FileServer() {
        Scanner sc = new Scanner(System.in);
        checkPreReq();

        System.out.print(">> ");
        String input = sc.nextLine();

        if (input.equals("FileSystem -i")) {
            register();
        } else if (input.equals("FileSystem")) {
            readAllFile();
            login();
        } else {
            System.out.println("Error!");
            System.exit(0);
        }
    }

    //Method to generate hash
    public String generateMD5(String password, String salt) {
        String hashText = "";

        if (password == null) {
            return null;
        }
        try {
            //Create MD object
            MessageDigest digest = MessageDigest.getInstance("MD5");

            //Combining password and generated salt
            String passSalt = password + salt;

            //Convert string to byte and do hashing
            byte[] md = digest.digest(passSalt.getBytes());

            //Convert byte array into big integer
            BigInteger num = new BigInteger(1, md);

            //Convert md into hex value
            hashText = num.toString(16);

        } catch (NoSuchAlgorithmException error) {
            //Print error message
            error.printStackTrace();
        }
        return hashText;

    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();

        //Create byte array with 8 index
        byte[] saltArr = new byte[8];

        //Generate random 8 char
        random.nextBytes(saltArr);

        //Convert UTF-16 to UTF-8
        String strSalt = DatatypeConverter.printBase64Binary(saltArr);

        return strSalt;
    }

    public String generateUserId() {
        //Readding passwd File
        File passwd = new File("storage/etc/passwd.txt");
        //Keeps track of number of users in the FileServer
        int userCount = 0;

        try {
            Scanner readFile = new Scanner(passwd);

            while (readFile.hasNext()) {
                readFile.next();
                userCount++; //Increase by 1 for each row in passwd
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Return user ID with 4 chars eg: 0001
        return String.format("%04d", userCount);
    }

    public String generateGroupId() {
        //Readding passwd File
        File passwd = new File("storage/etc/passwd.txt");
        //Keeps track of number of users in the FileServer
        int userCount = 0;

        try {
            Scanner readFile = new Scanner(passwd);

            while (readFile.hasNext()) {
                readFile.next();
                userCount++; //Increase by 1 for each row in passwd
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Return user ID with 4 chars eg: 0001
        //Group ID should be same as userID
        return String.format("%04d", userCount);
    }

    public void writeToUser(user user1) {
        try {
            //Append passwd.txt
            FileWriter passwdWrite = new FileWriter("storage/etc/passwd.txt", true);
            PrintWriter writer = new PrintWriter(passwdWrite);
            writer.println(user1.getUserName() + ":x:" + user1.getUserId() + ":" + user1.getGroupId() + ":" + user1.getComment() + ":" + user1.getHomeDirectory() + ":" + user1.getShell());
            writer.close();

            //Append shadow.txt
            FileWriter shadowWrite = new FileWriter("storage/etc/shadow.txt", true);
            PrintWriter writer1 = new PrintWriter(shadowWrite);
            writer1.println(user1.getUserName() + ":" + user1.getShadowHash() + ":" + user1.getClearance());
            writer1.close();

            //Append salt.txt
            FileWriter saltWrite = new FileWriter("storage/etc/salt.txt", true);
            PrintWriter writer2 = new PrintWriter(saltWrite);
            writer2.println(user1.getUserName() + ":" + user1.getSalt());
            writer2.close();

            //Create directory for user registered
            String userDirectory = "storage/home/" + user1.getUserName();
            File homeDirectory = new File(userDirectory);
            homeDirectory.mkdir();

            System.out.println("Files written successfully");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register() {

        final String REGEX = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})";
        //Console obj to read password
        
        Console console = System.console();
         

        //Scanner to read other inputs eg:username, filename
        Scanner sc = new Scanner(System.in);

        File passwdFile = new File("storage/etc/passwd.txt");
        System.out.print("Enter UserName: ");
        String userName = sc.nextLine();

        String[] passwdTxt = null;
        String parsePasswd;

        try {
            Scanner readUser = new Scanner(passwdFile);

            //Checking for next line
            while (readUser.hasNext()) {
                parsePasswd = readUser.next();
                //Split each column
                passwdTxt = parsePasswd.split(":");

                //if found username duplicated, print error
                if (passwdTxt[0].equals(userName)) {
                    System.out.println("Username exists");
                    System.exit(0);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //prompt for password
        System.out.print("Password must contain atleast 8 character with  1 uppercase, 1 lowercase, 1 number and 1 symbol[ @ # $ % !]\nEnter password: ");

        
        String pass1 = String.valueOf(console.readPassword());
         

        //Check for password policy
        if (Pattern.matches(REGEX, pass1)) {

            //prompt for password confirmation
            System.out.print("Re-enter password: ");

        String pass2 = String.valueOf(console.readPassword());
             

            if (pass1.equals(pass2)) {
                int groupRole;
                //perform validation for role
                do {
                    //getting user department
                    System.out.println("1.Finance\n2.Accounts\n3.IT Department\n4.Sales");
                    System.out.print("Enter group/role(1/2/3/4):");
                    groupRole = sc.nextInt();
                } while (groupRole != 1 && groupRole != 2 && groupRole != 3 && groupRole != 4);

                int clearanceLevel;
                do {
                    //prompt for clearance level
                    System.out.print("Enter Clearance level(0/1/2): ");
                    clearanceLevel = sc.nextInt();
                } while (clearanceLevel != 0 && clearanceLevel != 1 && clearanceLevel != 2);

                //Generate Salt
                String salt = generateSalt();

                //Generate MD5 hashing
                String md5Hash = generateMD5(pass1, salt);

                //Generate userId
                String userId = generateUserId();

                //Generate groupId
                String groupId = generateGroupId();

                //Create user  and add to arrayList
                user user1 = new user(userName, clearanceLevel, salt, md5Hash, userId, groupId);
                user1.setGroupId(String.format("%04d", groupRole));

                userList.add(user1);

                //Write user credentials to files
                writeToUser(user1);
            } else {
                System.out.println("Password does not match.\nProgram exiting....");
            }

        } else {
            System.out.println("Password does not match with policy");

        }
        System.exit(0);

    }

    public void checkPreReq() {
        //Create File object for 3 file txt to retrieve user information
        File dir1 = new File("storage/etc/");
        File dir2 = new File("storage/home/");
        File passwdFile = new File("storage/etc/passwd.txt");
        File saltFile = new File("storage/etc/salt.txt");
        File shadowFile = new File("storage/etc/shadow.txt");
        File fileStore = new File("storage/file.store");

        //Checking pre-req before initialize FileServer
        try {

            //Checking if etc dir exists 
            if (!dir1.exists()) {
                dir1.mkdirs();
            }

            //Checking if home dir exists 
            if (!dir2.exists()) {
                dir2.mkdirs();
            }

            //Checking if passwd.txt exists 
            if (!passwdFile.exists()) {
                //Create passwd.txt
                passwdFile.createNewFile();
            }

            //Checking if shadow.txt exists 
            if (!shadowFile.exists()) {
                //Create shadow.txt
                shadowFile.createNewFile();
            }

            //Checking if salt.txt exists 
            if (!saltFile.exists()) {
                //Create salt.txt
                saltFile.createNewFile();
            }

            //Checking if file.store exists 
            if (!fileStore.exists()) //Create passwd.txt
            {
                fileStore.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void login() {
        Console console = System.console();
        Scanner sc = new Scanner(System.in);

        //Read passwd.txt to check if user exists
        File passwdFile = new File("storage/etc/passwd.txt");
        File saltFile = new File("storage/etc/salt.txt");
        File shadowFile = new File("storage/etc/shadow.txt");

        //Create known users object and store into array list
        try {
            Scanner passwdRead = new Scanner(passwdFile);
            Scanner saltRead = new Scanner(saltFile);
            Scanner shadowRead = new Scanner(shadowFile);

            //read passwd.txt
            while (passwdRead.hasNext()) {

                //Reading every first line in passwd.txt and split
                String passwdLine = passwdRead.next();
                String passwdSplit[] = passwdLine.split(":");

                //Read necessary data
                String userName = passwdSplit[0];
                String uid = passwdSplit[2];
                String gid = passwdSplit[3];

                //Reading first line in shadow.txt and split
                String shadowLine = shadowRead.next();
                String shadowSplit[] = shadowLine.split(":");

                String shadowHash = shadowSplit[1];
                int clearanceLevel = Integer.parseInt(shadowSplit[2]);

                //Reading first line in salt.txt and split
                String saltLine = saltRead.next();
                String saltSplit[] = saltLine.split(":");

                String salt = saltSplit[1];

                //Instantiate and add into arraylist
                user newUser = new user(userName, clearanceLevel, salt, shadowHash, uid, gid);
                userList.add(newUser);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("Enter UserName: ");
        String userName = sc.nextLine();

        System.out.print("Enter password: ");
        
        String password = String.valueOf(console.readPassword());
         
        boolean userExist = false;

        String retrievSalt;
        String retrievHash;

        for (user userObj : userList) {
            if (userName.equals(userObj.getUserName())) {
                //retrieve salt and shadowhash for comparison and computation
                retrievSalt = userObj.getSalt();
                retrievHash = userObj.getShadowHash();
                System.out.println("User " + userName + " found in salt.txt and passwd.txt");
                System.out.print("Salt retrieved:" + retrievSalt);
                System.out.println("\nHashing is now in process");
                userExist = true;

                //generate md5 hash using user provided password and salt retrieved from salt.txt
                String passSaltHash = password + retrievSalt;
                System.out.print("Hash value:" + passSaltHash + "\n");

                //if generated hash matches stored hash, authentication process completed.
                String computedHash = generateMD5(password, retrievSalt);
                if (computedHash.equals(retrievHash)) {
                    System.out.println("Authentication for user " + userObj.getUserName() + " is complete");
                    System.out.println("The clearance for " + userObj.getUserName() + " is " + userObj.getClearance());

                    //Set authentication to true
                    currUser = userObj;
                    authenticated = true;
                } else {
                    System.out.println("Password is incorrect!\nProgram terminating...\n");
                }
            }
        }
        if (userExist == false) {
            System.out.println("User does not exist!\nProgram terminating...\n");
            System.exit(0);
        }
    }

    public boolean createFile(String fileName, int level) {

        try {
            //Reading file.store
            File fileStore = new File("storage/file.store");
            Scanner fileScan = new Scanner(fileStore);

            //Looping through every line in the file
            while (fileScan.hasNext()) {
                String content = fileScan.next();

                //Split into array (filename[0], classification[1])
                String readFileStore[] = content.split(":");

                //getting index of file name
                int index = readFileStore[0].lastIndexOf("/");
                //getting just only file name
                String name = readFileStore[0].substring(index + 1);

                if (fileName.equals(name)) {
                    //if file with same name found, return false;
                    System.out.println("File exists!");
                    return false;
                }
            }
            //
            

            //create file in the user home directory
            File userFile = new File(currUser.getHomeDirectory()+fileName);
            Filelevel tempFile = new Filelevel(currUser.getHomeDirectory()+fileName, level);
            fileList.add(tempFile);
            userFile.createNewFile();
            System.out.println("Files created successfully");

            //keep track of file created
            FileWriter file = new FileWriter("storage/file.store", true);
            PrintWriter printer = new PrintWriter(file);
            printer.println(currUser.getHomeDirectory() + fileName + ":" + level);
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean getAuthenticated() {
        return authenticated;
    }

    //initialize during File Server creation
    public void readAllFile() {
        File read = new File("storage/file.store");

        try {
            Scanner readFile = new Scanner(read);
            while (readFile.hasNext()) {

                //Retrieve file name and security
                String fileLine = readFile.next();
                String fileArr[] = fileLine.split(":");

                //Create file Obj 
                Filelevel file = new Filelevel(fileArr[0], Integer.parseInt(fileArr[1]));
                fileList.add(file);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile(String fileName) {
        boolean found = false;
        for (Filelevel fileObj : fileList) {

            String filePath = fileObj.getFileName();
            int index = filePath.lastIndexOf("/");
            String name = filePath.substring(index + 1);

            //looking to current user directory and match with filename
            if (fileName.equals(name)) {
                found = true;
                if (currUser.getClearance() >= fileObj.getLevel()) {
                    try {
                        File file = new File(fileObj.getFileName());
                        Scanner read = new Scanner(file).useDelimiter("\\z");

                        if (read.hasNext()) {
                            System.out.println("=======================");
                            System.out.println("Start");
                            System.out.println("=======================\n");
                            String content = read.next();
                            System.out.print(content);
                            System.out.println("\n=======================");
                            System.out.println("Finish");
                            System.out.println("=======================");

                        } else {
                            System.out.println("File is empty");
                        }

                    } catch (FileNotFoundException e) {
                        System.out.println("Error!\n"
                                + "Possibly file not found or empty\n");
                    }
                } else {
                    System.out.println("You have no permission to view this file");

                }
            }
        }
        if (found == false) {
            System.out.println("File not found...");
        }
    }

    public void writeFile(String fileName) {
        boolean found = false;
        Scanner sc = new Scanner(System.in);

        for (Filelevel fileObj : fileList) {

            String filePath = fileObj.getFileName();
            //getting index of file name
            int index = filePath.lastIndexOf("/");
            //getting just only file name
            String name = filePath.substring(index + 1);

            if (fileName.equals(name)) {
                found = true;
                if (currUser.getClearance() >= fileObj.getLevel()) {
                    try {
                        
                        FileWriter writer = new FileWriter(filePath, true);
                        PrintWriter writerFile = new PrintWriter(writer);

                        System.out.println("Enter your contents");
                        String storeFile = sc.nextLine();

                        writerFile.println(storeFile);
                        writerFile.close();
                        System.out.println("Written Sucessfully");

                    } catch (FileNotFoundException e) {
                        System.out.println("Error!\nFile not found\n");
                    } catch (IOException a)
                    {
                        System.out.println("Error!\nFile not found\n");
                    }
                }else {
                    System.out.println("You have no permission to write to this file");

                }

            }
        }
        if (found == false) {
            System.out.println("File not found...");
        }
    }

    public void listFile() {

        File file = new File("storage/file.store");

        try {
            Scanner read = new Scanner(file);
            int count = 0;
            while (read.hasNext()) {
                String readLine = read.next();
                String readSplit[] = readLine.split(":");
                System.out.println(++count + ". " + readSplit[0]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
