import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AdvancedTicketSystem {

    private static final String URL = "jdbc:mysql://localhost:3306/ticket_system_advanced";
    private static final String USER = "root";
    private static final String PASSWORD = "Panganiban123!";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"; // Simple email regex pattern
    private static final int MAX_TICKETS = 4;

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Welcome to Grand BINIverse Ticket Selling System!");
            System.out.println();
            System.out.println("Available concert days are:");
            System.out.println("   1. Day 1 (November 16, 2024)");
            System.out.println("   2. Day 2 (November 17, 2024)");
            System.out.println("   3. Day 3 (November 18, 2024)");
            System.out.println("You can only choose one concert day per transaction.");
            System.out.println();
            System.out.println("Ticket Types and Prices:");
            System.out.printf("   %-20s | %s%n", "VIP Ticket", "Price: 11,000 pesos");
            System.out.printf("   %-20s | %s%n", "Patron A", "Price: 8,000 pesos");
            System.out.printf("   %-20s | %s%n", "Patron B", "Price: 7,300 pesos");
            System.out.printf("   %-20s | %s%n", "Lower Box", "Price: 5,800 pesos");
            System.out.printf("   %-20s | %s%n", "Upper Box", "Price: 2,600 pesos");
            System.out.printf("   %-20s | %s%n", "General Admission", "Price: 1,500 pesos");
            System.out.println("Note: Each email can purchase a maximum of 4 e-tickets per transaction.");

            Scanner scanner = new Scanner(System.in);

            // Get customer details
            System.out.print("Enter customer name: ");
            String customerName = scanner.nextLine();

            // Get customer email and check for duplicates and valid format
            String customerEmail;
            while (true) {
                System.out.print("Enter customer email: ");
                customerEmail = scanner.nextLine();
                if (!Pattern.matches(EMAIL_REGEX, customerEmail)) {
                    System.out.println("Invalid email format. Please enter a valid email.");
                } else if (isEmailDuplicate(conn, customerEmail)) {
                    System.out.println("This email is already in use. Please enter a different email.");
                } else {
                    break; // Exit loop if email is unique and valid
                }
            }

            String paymentMethod = "";

            while (true) {
                System.out.println("Select a Payment Method:");
                System.out.println("1. Credit Card");
                System.out.println("2. Debit Card");
                System.out.println("3. PayPal");
                System.out.print("Enter the number of your payment method: ");
                
                // Handle non-integer input for paymentOption
                int paymentOption;
                if (!scanner.hasNextInt()) {
                    System.out.println("Error: Please enter a valid number (1-3).");
                    scanner.nextLine(); // Consume the invalid input
                    continue; // Prompt user again
                }
                paymentOption = scanner.nextInt();
                scanner.nextLine(); // Consume the newline
            
                switch (paymentOption) {
                    case 1:
                        paymentMethod = "Credit Card";
                        // If the user chooses Credit Card
                        String creditCardNumber;
                        while (true) {
                            System.out.print("Enter your 16-digit credit card number: ");
                            creditCardNumber = scanner.nextLine().trim();
                            // Validate the credit card number
                            if (creditCardNumber.matches("(\\d{4} ?){3}\\d{4}")) {
                                System.out.println("Credit card number accepted.\n");
                                break;  
                            } else {
                                System.out.println("Invalid credit card number. Please enter a valid 16-digit number.\n");
                            }
                        }
                        break;
            
                    case 2:
                        paymentMethod = "Debit Card";
                        // If the user chooses Debit Card
                        String debitCardNumber;
                        while (true) {
                            System.out.print("Enter your 16-digit debit card number: ");
                            debitCardNumber = scanner.nextLine().trim();
                            // Validate the debit card number
                            if (debitCardNumber.matches("(\\d{4} ?){3}\\d{4}")) {
                                System.out.println("Debit card number accepted.\n");
                                break;  
                            } else {
                                System.out.println("Invalid debit card number. Please enter a valid 16-digit number.\n");
                            }
                        }
                        break;
            
                    case 3:
                        paymentMethod = "PayPal";
                        // If the user chooses PayPal
                        String paypalAccount;
                        while (true) {
                            System.out.print("Enter your PayPal email or phone number: ");
                            paypalAccount = scanner.nextLine();
                            // Validate the paypal account (email or phone number)
                            if (paypalAccount.matches("^[\\w-\\.]+@[\\w-]+\\.[a-z]{2,3}$")) { // Valid format for PayPal email
                                System.out.println("Email accepted for PayPal.\n");
                                break;  
                            } else if (paypalAccount.matches("^(\\+63|0)?9\\d{2}[-.\\s]?\\d{3}[-.\\s]?\\d{4}$|^(\\+63|0)?9\\d{10}$")) { // Valid phone format
                                System.out.println("Phone number accepted for PayPal.\n");
                                break;  
                            } else {
                                System.out.println("Invalid input. Enter a valid email or phone number.\n");
                            }
                        }
                        break;
            
                    default:
                        System.out.println("Error: Invalid option. Please enter a valid number (1-3).");
                        continue; // Prompt user again if the option is invalid
                }
            
                // Exit loop after successful payment method selection
                break;
            }
            

            // Insert customer into the database and get customer ID
            int customerId = insertCustomer(conn, customerName, customerEmail);
            

            // Choose event day
            int eventDay = 0;
            while (true) {
                try {
                    System.out.println("Available Event Days");
                    System.out.println("1. Day 1 (November 16, 2024)");
                    System.out.println("2. Day 2 (November 17, 2024)");
                    System.out.println("3. Day 3 (November 18, 2024)");
                    System.out.print("Enter event day (1-3): ");
                    
                    // Check if input is an integer and within the valid range
                    if (scanner.hasNextInt()) {
                        eventDay = scanner.nextInt();
                        scanner.nextLine(); // Consume newline left by nextInt()
                        
                        if (eventDay >= 1 && eventDay <= 3) {
                            break; // Exit loop if valid input
                        } else {
                            System.out.println("Invalid event day. Please choose a day between 1 and 3.");
                        }
                    } else {
                        // If input is not an integer
                        System.out.println("Invalid input. Please enter a number between 1 and 3.");
                        scanner.nextLine(); // Consume invalid input to avoid an infinite loop
                    }
                } catch (Exception e) {
                    System.out.println("An unexpected error occurred. Please try again.");
                    scanner.nextLine(); // Consume invalid input to reset the scanner
                }
            }
            System.out.println("You have selected Day " + eventDay + ".");


            // Allow ordering up to 4 tickets
            boolean continueOrdering = true;
            int ticketsOrdered = 0;
            double totalAmount = 0;
            while (continueOrdering && ticketsOrdered < MAX_TICKETS) {
                System.out.println("\nAvailable Ticket Types");
                System.out.println("1. VIP | Price: 11,000 pesos");
                System.out.println("2. Patron A | Price: 8,000 pesos");
                System.out.println("3. Patron B | Price: 7,300 pesos");
                System.out.println("4. Lower Box | Price: 5,800 pesos");
                System.out.println("5. Upper Box | Price: 2,600 pesos");
                System.out.println("6. General Admission | Price: 1,500 pesos");
                System.out.print("Enter ticket type: ");
                String ticketType = scanner.nextLine().toUpperCase();

                double price;
                switch (ticketType) {
                    case "VIP":
                        price = 11000;
                        break;
                    case "PATRON A":
                        price = 8000;
                        break;
                    case "PATRON B":
                        price = 7300;
                        break;
                    case "LOWER BOX":
                        price = 5800;
                        break;
                    case "UPPER BOX":
                        price = 2600;
                        break;
                    case "GENERAL ADMISSION":
                        price = 1500;
                        break;
                    default:
                        System.out.println("Invalid Input. Please try again.");
                        continue;
                }

                
                // Define seat limits for each ticket type
                Map<String, Integer> seatLimits = new HashMap<>();
                seatLimits.put("VIP", 100);
                seatLimits.put("PATRON A", 150);
                seatLimits.put("PATRON B", 150);
                seatLimits.put("LOWER BOX", 200);
                seatLimits.put("UPPER BOX", 200);
                seatLimits.put("GENERAL ADMISSION", 200);

                // Get seat number and validate against duplicates and seat limits
                int seatNumber;
                while (true) {
                    System.out.print("Enter seat number: ");

                     // Check if the input is an integer
                    if (!scanner.hasNextInt()) {
                        System.out.println("Error: Please enter a valid seat number (integer value).");
                        scanner.nextLine(); // Consume the invalid input
                        continue; // Prompt the user again
                    }
                    
                    seatNumber = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    // Check seat number is within the valid range
                    int maxSeats = seatLimits.getOrDefault(ticketType, 0);
                    if (seatNumber < 1 || seatNumber > maxSeats) {
                        System.out.println("Error: Invalid seat number. Seat numbers for " + ticketType + " must be between 1 and " + maxSeats + ".");
                        continue;
                    }
                    
                    // Check if the seat is already reserved
                    if (isSeatDuplicate(conn, ticketType, seatNumber, eventDay)) {
                        System.out.println("Error: Seat " + seatNumber + " for " + ticketType + " on Day " + eventDay + " is already reserved.");
                    } else {
                        break;
                    }
                }

                // Insert ticket into the database with customer ID and seat number
                insertTicket(conn, ticketType, price, eventDay, customerId, seatNumber, paymentMethod);
                ticketsOrdered++;

                // Add the price of the ticket to the total amount
                totalAmount += price;

                // Check if user wants to order another ticket or reached max limit
                if (ticketsOrdered < MAX_TICKETS) {
                    while (true) { // Loop until valid input is received
                        System.out.print("Do you want to order another ticket? (yes/no): ");
                        String response = scanner.nextLine().trim().toLowerCase();

                        if (response.equals("yes")) {
                            continueOrdering = true;
                            break; // Exit the inner loop and continue the outer loop
                        } else if (response.equals("no")) {
                            continueOrdering = false;
                            break; // Exit both loops
                        } else {
                            System.out.println("Invalid input. Please enter 'yes' or 'no'.");
                        }
                    }
                } else {
                    System.out.println("You have reached the maximum limit of " + MAX_TICKETS + " tickets.");
                    continueOrdering = false; // Stop ordering after reaching the limit
                }
            }

            insertPayment(conn, customerId, paymentMethod, totalAmount);
            // Print overall order summary
            System.out.println("___________________________________________________________________________________________________");
            System.out.println("\nOverall Ticket Order Summary for " + customerName + ":");
            System.out.println("Your e-ticket will be sent to " + customerEmail);
            System.out.println("Total Amount: " + totalAmount + " pesos");
            System.out.println("On the day of the concert, simply present the e-ticket email at the venue for verification.");

            // Retrieve and display all orders for the current customer
            retrieveCustomerOrders(conn, customerId);
            System.out.println("___________________________________________________________________________________________________");

            System.out.println("Press enter to exit...");
            scanner.nextLine();

            System.out.println("\nThank you for purchasing ticket/s! Enjoy your concert experience!");

            scanner.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int insertCustomer(Connection conn, String customerName, String customerEmail) {
        String sql = "INSERT INTO customer (customer_name, customer_email) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customerName);
            stmt.setString(2, customerEmail);
            stmt.executeUpdate();
    
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1); // Return the auto-generated customer_id
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if insertion fails
    }
    

    private static void insertTicket(Connection conn, String ticketType, double price, int eventDay, int customerId, int seatNumber, String paymentMethod) {
        String sql = "INSERT INTO ticket (ticket_type, price, event_day, customer_id, seat_number, payment_method) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ticketType);
            stmt.setDouble(2, price);
            stmt.setInt(3, eventDay);
            stmt.setInt(4, customerId);
            stmt.setInt(5, seatNumber); // Set to null for VIP tickets (standing)
            stmt.setString(6, paymentMethod);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertPayment(Connection conn, int customerId, String paymentMethod, double totalAmount) {
        String sql = "INSERT INTO payment (customer_id, payment_method, total_amount) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId); // Correctly set the first parameter
            stmt.setString(2, paymentMethod); // Correctly set the second parameter
            stmt.setDouble(3, totalAmount);
            stmt.executeUpdate(); // Execute the update
        } catch (SQLException e) {
            e.printStackTrace(); // Print stack trace if an error occurs
        }
    }
    
    

    private static boolean isEmailDuplicate(Connection conn, String customerEmail) {
        String sql = "SELECT customer_email FROM customer WHERE customer_email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerEmail);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if email exists
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    

    private static boolean isSeatDuplicate(Connection conn, String ticketType, int seatNumber, int eventDay) {
        String sql = "SELECT seat_number FROM ticket WHERE ticket_type = ? AND seat_number = ? AND event_day = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ticketType);
            stmt.setInt(2, seatNumber);
            stmt.setInt(3, eventDay);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if seat is already reserved
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    
    private static void retrieveCustomerOrders(Connection conn, int customerId) {
        String sql = "SELECT ticket_type, price, event_day, seat_number, payment_method FROM ticket WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
    
            System.out.printf("%-20s %-10s %-10s %-10s %-20s%n", "Ticket Type", "Price", "Day", "Seat", "Payment Method");
            while (rs.next()) {
                String ticketType = rs.getString("ticket_type");
                double price = rs.getDouble("price");
                int eventDay = rs.getInt("event_day");
                int seatNumber = rs.getInt("seat_number");
                String paymentMethod = rs.getString("payment_method");
    
                System.out.printf("%-20s %-10.2f %-10d %-10s %-20s%n", ticketType, price, eventDay, (seatNumber == 0 ? "N/A" : seatNumber), paymentMethod);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}    
