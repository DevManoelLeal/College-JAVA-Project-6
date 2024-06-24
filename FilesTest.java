//Manoel De Mattos Leal
import javax.security.auth.login.AccountException;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class FilesTest {
    //Task three
    private static final String FILE_NAME = "accounts.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yy");
    //End of this part of task three

    public static void main(String[] args){
        //Task one, ordering a file txt to a alphabetic order
        String filename = "texto.txt";
        try {
            sortFile(filename);
        } catch (IOException e) {
            System.err.println("Error");
        }
        //End of task one

        //Task two, downloading from an url based on user inputs
        Scanner scanner = new Scanner(System.in);
        //Getting the URL from user
        System.out.println("Enter the URL you want to download from:");
        String url = scanner.nextLine();
        //Getting the file name with the extension
        System.out.println("Enter the file name to save as with its extension:");
        String fileName = scanner.nextLine();
        //Getting where we will keep the file
        System.out.println("Enter the location to save the file:");
        String location = scanner.nextLine();

        //Checking if the url and the file name are the same
        while (!validateExtensions(url, fileName)) {
            System.out.println("The file extension of the URL and the file name must match. Please re-enter the file name with the extension:");
            fileName = scanner.nextLine();
        }

        //Download the file
        try {
            download(url, fileName, location);
            System.out.println("File downloaded successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred");
        }
        //End of task two

        //Task three
        //In order to get the user signup or login, let's give them such choices, so we know which one we will handle
        while (true) {
            System.out.println("Select: 1. Signup \n2. Login \n3. Finish program");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    signup(scanner);
                    break;
                case 2:
                    login(scanner);
                    break;
                case 3:
                    System.out.println("Finishing program...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
        //End of task three
    }

    //Task one
    public static void bubbleSort(ArrayList<String> valores){
//        following what I found on the following links:
//        https://www.w3schools.com/js/js_array_sort.asp ---> I know this one is in JavaScript, but it helped me quite a bit to understand how to get it done;
//        https://www.w3schools.com/dsa/dsa_algo_bubblesort.php;
//        https://stackoverflow.com/questions/11644858/bubblesort-implementation;
//        I got to this solution:
        int m = valores.size();
        boolean invertido;

        for (int i = 0; i < m - 1; i++) {
            invertido = false;
            //a for loop inside the for loop, so I can change the order of each line
            for (int j = 0; j < m - i - 1; j++) {
                //using the if statement to get which line is which
                if (valores.get(j).compareTo(valores.get(j + 1)) > 0) {
                    String temp = valores.get(j);
                    valores.set(j, valores.get(j + 1));
                    valores.set(j + 1, temp);
                    invertido = true;
                }

            }
            //ending the loop
            if (!invertido) break;
        }

        
    }

    //sortFile method as asked to read the content of a given file and store it in an ArrayList, with exception being used on the method
    public static void sortFile(String filename) throws IOException {
        ArrayList<String> valores = new ArrayList<>();
        try (BufferedReader ler = new BufferedReader(new FileReader(filename))) {
            String linha;
            while ((linha = ler.readLine()) != null) {
                valores.add(linha);
            }
        }
        //calling the bubble sort to sort the arraylist created here
        //I had put this method below the writer, therefore my code didn't work. The position must be before the writer so the new file can have the changes
        bubbleSort(valores);

        //changing the file name, rewriting its content to the order asked
        String newFilename = filename.substring(0, filename.lastIndexOf('.')) + "_sorted" + filename.substring(filename.lastIndexOf('.'));

        //using a for loop to write
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(newFilename))) {
            for (String valor : valores) {
                writer.write(valor);
                writer.newLine();
            }
        }

    }
    //End of Task One

    //Task two
    //Creating the download method, with the exception
    public static void download(String urlString, String fileName, String location) throws IOException {
        URL url = new URL(urlString);
        InputStream in = url.openStream();
        Files.copy(in, Paths.get(location, fileName), StandardCopyOption.REPLACE_EXISTING);
        in.close();
    }

    //With a boolean method, we can validate the url and file extensions to match them
    public static boolean validateExtensions(String url, String fileName) {
        String urlExtension = getFileExtension(url);
        String fileExtension = getFileExtension(fileName);
        return urlExtension.equals(fileExtension);
    }

    //I tried before making this void and add it to my main method, but I didn't work. With this method we can get the file extension from a string
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        } else {
            return "";
        }
    }
    //End of task two

    //Task three
    //Creating the class Account that will be used by other parts of my code to get the user's info
    public static class Account {
        private String username;
        private String password;
        private String lastLogin;

        public Account(String username, String password, String lastLogin) {
            this.username = username;
            this.password = password;
            this.lastLogin = lastLogin;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(String lastLogin) {
            this.lastLogin = lastLogin;
        }
    }
    //Getting the signup sorted, using the user's inputs
    private static void signup(Scanner scanner) {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        //Getting a list with the account with a method to read the details from the file
        try {
            List<Account> accounts = readAccountsFromFile();
            for (Account account : accounts) {
                if (account.getUsername().equals(username)) {
                    System.out.println("Username already exists. Choose a new one.");
                    return;
                }
            }

            String currentDate = LocalDate.now().format(DATE_FORMATTER);
            Account newAccount = new Account(username, password, currentDate);
            accounts.add(newAccount);
            writeAccountsToFile(accounts);
            System.out.println("Signup successful.");
        } catch (IOException e) {
            System.err.println("Error. Try again");
        }
    }

    //Creating the login in case the user already having an account
    private static void login(Scanner scanner) {
        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        //Similar to what we have on the signup, but now with the different login specifications
        try {
            List<Account> accounts = readAccountsFromFile();
            boolean userFound = false;
            //For loop to check each account in the list
            for (Account account : accounts) {
                //If statement to check if the username matches
                if (account.getUsername().equals(username)) {
                    //Changing the previous boolean since now we found the user in the list
                    userFound = true;
                    //A second if statement to check for the password
                    if (account.getPassword().equals(password)) {
                        LocalDate lastLoginDate = LocalDate.parse(account.getLastLogin(), DATE_FORMATTER);
                        long daysSinceLastLogin = ChronoUnit.DAYS.between(lastLoginDate, LocalDate.now());
                        System.out.println(username + ", " + password + ", " + lastLoginDate + ". It's been " + daysSinceLastLogin + " days since you last logged in.");

                        //Updating the login for the next time
                        account.setLastLogin(LocalDate.now().format(DATE_FORMATTER));
                        //Updating the file
                        writeAccountsToFile(accounts);
                    } else {
                        System.out.println("Incorrect password. Please try again.");
                    }
                    break;
                }
            }
            //Printing a message in case the user can't be found
            if (!userFound) {
                System.out.println("Username doesn't exist. Please signup.");
            }
        } catch (IOException e) {
            //Worst case scenario, error message if any exception happen
            System.err.println("Error" + e.getMessage());
        }
    }

    private static List<Account> readAccountsFromFile() throws IOException {
        //Creating an empty list to store Account objects
        List<Account> accounts = new ArrayList<>();
        File file = new File(FILE_NAME);
        //Check if the FILE_NAME exists, returning a list of empty accounts if it doesn't
        if (!file.exists()) {
            return accounts;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                //For each line, splits the line into parts with a comma for the regex
                String[] parts = line.split(",");
                //If the third part is valid as the date, creates a new account and add it to the list
                if (parts.length == 3 && isValidDate(parts[2])) {
                    accounts.add(new Account(parts[0], parts[1], parts[2]));
                }
            }
        }
        return accounts;
    }

    private static void writeAccountsToFile(List<Account> accounts) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            //Using a for loop to go through the list of accounts
            for (Account account : accounts) {
                bw.write(account.getUsername() + "," + account.getPassword() + "," + account.getLastLogin());
                bw.newLine();
            }
        }
    }

    //Checking if the date string is in the correct format
    private static boolean isValidDate(String date) {
        return Pattern.matches("\\d{2}-\\d{2}-\\d{2}", date);
    }
    //End of task three


    //I'm quite happy with the outcome of this assignment. I think I did a good job and I learn a lot with these tasks, mainly the third one. It took me weeks to get everything done, I really hope you can see my hard work through the code. I really really tried my best here.
}