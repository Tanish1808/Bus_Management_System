import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

class BusSeatReservationSystem1 {
    // Declare Bus , Seat Information , Schedule , Price , Passenger Details for Each Seat and Passenger Information
    static String[] buses = {"1. InterCity Express", "2. Interstate Express", "3. Sleeping Coach", "4. Double Decker"};
    static int[][] seats = new int[4][20]; // 4 buses, 20 seats each
    static String[][] schedules = {
            {"9:00 AM", "12:00 PM"},  // InterCity Express schedule
            {"10:30 AM", "2:30 PM"}, // Interstate Express schedule
            {"11:00 AM", "4:00 PM"}, // Sleeping Coach schedule
            {"8:00 AM", "11:30 AM"}  // Double Decker schedule
    };
    static int[][] prices = {
            {100, 150, 200, 250, 100, 150, 200, 250, 100, 150, 200, 250, 100, 150, 200, 250, 100, 150, 200, 250},
            {150, 200, 250, 300, 150, 200, 250, 300, 150, 200, 250, 300, 150, 200, 250, 300, 150, 200, 250, 300},
            {200, 250, 300, 350, 200, 250, 300, 350, 200, 250, 300, 350, 200, 250, 300, 350, 200, 250, 300, 350},
            {250, 300, 350, 400, 250, 300, 350, 400, 250, 300, 350, 400, 250, 300, 350, 400, 250, 300, 350, 400}
    };
    static String[][][] passengerDetails = new String[4][20][3]; // Stores [name, age, contact] for each seat
    static String[][] passengers = new String[4][20]; // Stores passenger details

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        while(true){
            System.out.println("\n=== Welcome To The Bus Seat Reservation System ===");
            System.out.println("1. View Buses");
            System.out.println("2. View Bus Schedule");
            System.out.println("3. Reserve a Single Seat");
            System.out.println("4. Group Booking");
            System.out.println("5. View Seat Availability");
            System.out.println("6. Admin Panel");
            System.out.println("7. View Passenger Information");
            System.out.println("8. Exit");
            System.out.print("Enter your Choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    displayBuses();
                    break;
                case 2:
                    displayBusSchedules();
                    break;
                case 3:
                    reserveSeat(sc);
                    break;
                case 4:
                    groupBooking(sc);
                    break;
                case 5:
                    viewSeatAvailability();
                    break;
                case 6:
                    adminPanel(sc);
                    break;
                case 7:
                    viewPassengerInformation(sc);
                    break;
                case 8:
                    System.out.println("Thank you for using the Bus Seat Reservation System. ");
                    System.out.println("Wish you a safe and pleasant journey!");
                    System.out.println("Goodbye!");
                    sc.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    // Method to display available buses
    static void displayBuses() {
        System.out.println("\n=== Available Buses ===");
        for (String bus : buses) {
            System.out.println(bus);
        }
    }

    // Method to display bus schedules
    static void displayBusSchedules() {
        System.out.println("\n=== Bus Schedules ===");
        for (int i = 0; i < buses.length; i++) {
            System.out.println(buses[i] + " - Departure: " + schedules[i][0] + ", Arrival: " + schedules[i][1]);
        }
    }

    // Method to reserve a seat
    static void reserveSeat(Scanner sc) {
        System.out.println("\n=== Reserve a Seat ===");
        displayBuses();

        // Selecting a valid bus
        System.out.print("Select a bus (1-4): ");
        int busChoice = sc.nextInt();
        if (busChoice < 1 || busChoice > 4) {
            System.out.println("Invalid bus choice! Please try again.");
            return;
        }

        // Selecting a valid seat
        System.out.print("Enter seat number (1-20): ");
        int seatChoice = sc.nextInt();
        if (seatChoice < 1 || seatChoice > 20) {
            System.out.println("Invalid seat number! Please try again.");
            return;
        }

        // Checking if seat is already reserved
        if (seats[busChoice - 1][seatChoice - 1] == 1) {
            System.out.println("Sorry, this seat is already reserved!");
            return;
        }

        sc.nextLine(); // Consume newline
        System.out.print("Enter passenger name: ");
        String name = sc.nextLine();

        // Validating age input
        System.out.print("Enter passenger age: ");
        int age = sc.nextInt();
        if (age <= 0 || age > 120) {
            System.out.println("Invalid age! Age must be between 1 and 120.");
            return;
        }

        // Early booking discount validation
        System.out.print("Enter days before travel (for early booking discount): ");
        int daysBeforeTravel = sc.nextInt();
        if (daysBeforeTravel < 0) {
            System.out.println("Invalid input! Days before travel cannot be negative.");
            return;
        }

        sc.nextLine(); // Consume newline
        System.out.print("Enter passenger contact number: ");
        String contact = sc.nextLine();

        // Validating contact number (must be exactly 10 digits)
        if (!contact.matches("\\d{10}")) {
            System.out.println("Invalid Contact Number! It should contain exactly 10 digits.");
            return;
        }

        // Fetching original price
        double originalPrice = prices[busChoice - 1][seatChoice - 1];
        double discountedPrice = originalPrice;

        // Applying Senior Citizen Discount
        if (age >= 60) {
            discountedPrice *= 0.8; // 20% discount
            System.out.println("Senior Citizen Discount Applied: 20%");
        }

        // Applying Early Booking Discount
        if (daysBeforeTravel >= 7) {
            discountedPrice *= 0.85; // 15% discount
            System.out.println("Early Booking Discount Applied: 15%");
        }

        // Display price details
        System.out.println("Seat reserved successfully!");
        System.out.println("Bus: " + buses[busChoice - 1] + ", Seat: " + seatChoice);
        System.out.printf("Original Price: %.2f INR\n", originalPrice);
        System.out.printf("Discounted Price: %.2f INR\n", discountedPrice);

        // Payment verification
        System.out.print("Have you paid the amount? (Yes/No): ");
        String payment = sc.next();
        if (payment.equalsIgnoreCase("yes")) {
            // Mark seat as reserved only after successful payment
            seats[busChoice - 1][seatChoice - 1] = 1;
            passengerDetails[busChoice - 1][seatChoice - 1][0] = name;
            passengerDetails[busChoice - 1][seatChoice - 1][1] = String.valueOf(age);
            passengerDetails[busChoice - 1][seatChoice - 1][2] = contact;
            System.out.println("Seat successfully reserved for Bus " + buses[busChoice - 1] + ", Seat: " + seatChoice);
        } else {
            System.out.println("Payment not completed. Reservation failed.");
        }
    }

    static void groupBooking(Scanner sc) {
        System.out.println("\n=== Group Booking ===");
        displayBuses();

        // Selecting a valid bus
        System.out.print("Select a bus (1-4): ");
        int busChoice = sc.nextInt();
        if (busChoice < 1 || busChoice > 4) {
            System.out.println("Invalid bus choice! Please try again.");
            return;
        }

        // Selecting a valid group size
        System.out.print("Enter the number of seats to reserve: ");
        int groupSize = sc.nextInt();
        if (groupSize < 1 || groupSize > 20) {
            System.out.println("Invalid group size! Please select between 1 and 20 seats.");
            return;
        }

        // Check for consecutive available seats
        int startSeat = -1;
        for (int i = 0; i <= 20 - groupSize; i++) {
            boolean available = true;
            for (int j = 0; j < groupSize; j++) {
                if (seats[busChoice - 1][i + j] == 1) {
                    available = false;
                    break;
                }
            }
            if (available) {
                startSeat = i + 1;
                break;
            }
        }

        if (startSeat == -1) {
            System.out.println("Sorry, no consecutive seats are available for this group size.");
            return;
        }

        System.out.println("Consecutive seats available from Seat " + startSeat + " to Seat " + (startSeat + groupSize - 1) + ".");
        System.out.print("Do you want to proceed with this booking? (Yes/No): ");
        String confirmation = sc.next();

        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Group booking canceled.");
            return;
        }

        // Collect passenger details
        sc.nextLine(); // Consume newline
        for (int i = 0; i < groupSize; i++) {
            int seatNumber = startSeat + i;
            System.out.println("\nEntering details for Seat " + seatNumber + ":");

            System.out.print("Enter passenger name: ");
            String name = sc.nextLine();

            // Validating age input
            System.out.print("Enter passenger age: ");
            int age = sc.nextInt();
            if (age <= 0 || age > 120) {
                System.out.println("Invalid Age! Age must be between 1 and 120.");
                return;
            }

            sc.nextLine(); // Consume newline
            System.out.print("Enter passenger contact number: ");
            String contact = sc.nextLine();

            // Validating contact number (must be exactly 10 digits)
            if (!contact.matches("\\d{10}")) {
                System.out.println("Invalid Contact Number! It should contain exactly 10 digits.");
                return;
            }

            // Mark seat as reserved
            seats[busChoice - 1][seatNumber - 1] = 1;
            passengerDetails[busChoice - 1][seatNumber - 1][0] = name;
            passengerDetails[busChoice - 1][seatNumber - 1][1] = String.valueOf(age);
            passengerDetails[busChoice - 1][seatNumber - 1][2] = contact;
        }

        // Calculate total price
        double totalPrice = 0;
        for (int i = 0; i < groupSize; i++) {
            int seatNumber = startSeat + i;
            totalPrice += prices[busChoice - 1][seatNumber - 1];
        }

        // Apply group discount if applicable
        if (groupSize >= 5) {
            totalPrice *= 0.9; // Apply 10% group discount
            System.out.println("Group Booking Discount Applied: 10%");
        }

        System.out.printf("Total Price for Group Booking: %.2f INR\n", totalPrice);
        System.out.print("Have you paid the amount? (Yes/No): ");
        String payment = sc.next();

        if (payment.equalsIgnoreCase("yes")) {
            System.out.println("Group booking successful!");
            System.out.println("Bus: " + buses[busChoice - 1]);
            System.out.println("Seats Reserved: " + startSeat + " to " + (startSeat + groupSize - 1));
        } else {
            // Revert reservation if payment is not made
            for (int i = 0; i < groupSize; i++) {
                int seatNumber = startSeat + i;
                seats[busChoice - 1][seatNumber - 1] = 0; // Mark seat as available
                passengerDetails[busChoice - 1][seatNumber - 1] = new String[3]; // Clear passenger details
            }
            System.out.println("Payment not completed. Group booking failed.");
        }
    }

    // Method to view seat availability
    static void viewSeatAvailability() {
        System.out.println("\n=== Seat Availability ===");
        for (int i = 0; i < buses.length; i++) {
            System.out.println(buses[i]);
            System.out.print("Seats: ");
            for (int j = 0; j < seats[i].length; j++) {
                if (seats[i][j] == 0) {
                    System.out.print((j + 1) + "(Available) ");
                } else {
                    System.out.print((j + 1) + "(Reserved) ");
                }
            }
            System.out.println("\n");
        }
    }

    // Admin Panel
    static void adminPanel(Scanner sc) {
        int pass = 1234;
        System.out.print("Enter the 4-digit password to access the Admin Panel: ");
        int password = sc.nextInt();

        if (password == pass) {
            while (true) {
                System.out.println("\n=== Admin Panel ===");
                System.out.println("1. View All Reservations");
                System.out.println("2. Cancel a Reservation");
                System.out.println("3. Exit Admin Panel");
                System.out.print("Enter your choice: ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        viewAllReservations();
                        break;
                    case 2:
                        cancelReservation(sc);
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            }
        } else {
            System.out.println("Invalid Password!");
        }
    }

    // Method to view all reservations
    static void viewAllReservations() {
        System.out.println("\n=== All Reservations ===");
        for (int i = 0; i < buses.length; i++) {
            System.out.println(buses[i]);
            System.out.print("Seats: ");
            for (int j = 0; j < seats[i].length; j++) {
                if (seats[i][j] == 1) {
                    System.out.print((j + 1) + "(Reserved) ");
                }
            }
            System.out.println("\n");
        }
    }

    // Method to cancel a reservation
    static void cancelReservation(Scanner sc) {
        System.out.println("\n=== Cancel Reservation ===");
        displayBuses();
        System.out.print("Select a bus (1-4): ");
        int busChoice = sc.nextInt();

        if (busChoice < 1 || busChoice > 4) {
            System.out.println("Invalid bus choice! Please try again.");
            return;
        }

        System.out.print("Enter seat number (1-20): ");
        int seatChoice = sc.nextInt();

        if (seatChoice < 1 || seatChoice > 20) {
            System.out.println("Invalid seat number! Please try again.");
            return;
        }

        if (seats[busChoice - 1][seatChoice - 1] == 0) {
            System.out.println("This seat is not reserved!");
        } else {
            int price = prices[busChoice - 1][seatChoice - 1];
            int refund = price - (int) (price * 0.10); // 10% cancellation charge
            seats[busChoice - 1][seatChoice - 1] = 0; // Mark seat as available
            System.out.println("Reservation canceled. Refund amount: " + refund + " INR");
        }
    }
    // Method to view passenger information
    static void viewPassengerInformation(Scanner sc) {
        System.out.println("\n=== View Passenger Information ===");
        displayBuses();
        System.out.print("Select a bus (1-4): ");
        int busChoice = sc.nextInt();

        if (busChoice < 1 || busChoice > 4) {
            System.out.println("Invalid bus choice! Please try again.");
            return;
        }

        System.out.print("Enter seat number (1-20): ");
        int seatChoice = sc.nextInt();

        if (seatChoice < 1 || seatChoice > 20) {
            System.out.println("Invalid seat number! Please try again.");
            return;
        }

        // Display passenger details if reserved
        if (seats[busChoice - 1][seatChoice - 1] == 1) {
            String name = passengerDetails[busChoice - 1][seatChoice - 1][0];
            String age = passengerDetails[busChoice - 1][seatChoice - 1][1];
            String contact = passengerDetails[busChoice - 1][seatChoice - 1][2];

            System.out.println("\nPassenger Details for Bus " + buses[busChoice - 1] + ", Seat: " + seatChoice);
            System.out.println("Name: " + name);
            System.out.println("Age: " + age);
            System.out.println("Contact: " + contact);
        } else {
            System.out.println("No reservation found for this seat.");
        }
    }
}
