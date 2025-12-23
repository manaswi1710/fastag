import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class FastagApp {
  private static final String VEHICLE_FILE = "vehicles.txt";
  private static final String TRANSACTION_FILE = "transactions.txt";
  public static Map<String, vehicle> vehicles = new HashMap<>();

  

  public static void main(String[] args) throws Exception {
      Scanner sc=new Scanner(System.in);
        loadVehicles();
        // Vehicles arriving at toll plaza
        String[] arrivingVehicles = {
            "KA01AB1234", "KA02XY5678", "KA03MN4321",
            "KA04PQ9876", "KA05XY1111", "KA06AB2222",
            "KA07CD3333", "KA08EF4444", "KA09GH5555", "KA10IJ6666"
        };

       // ✅ Fixed toll for all vehicles

        Random rand = new Random();

        // Simulate random arrival times, but fixed toll
        for (String numberPlate : arrivingVehicles) {
            Thread t = new VehicleProcessor(numberPlate);
            t.start();

            // Random delay between arrivals (0.5–2 seconds)
            Thread.sleep(500 + rand.nextInt(1500));
        }

      


        saveVehicles();

        while (true) {
            System.out.println("\n--- FASTag Toll Booth ---");
            System.out.println("1. Check & Deduct Toll");
            System.out.println("2. View All Vehicles");
            System.out.println("3. Recharge FASTag");
            System.out.println("4. Generate Daily Report");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> processToll(sc);
                case 2 -> viewVehicles();
                case 3 -> {System.out.print("Enter vehicle number: ");
                            String vnum = sc.next();
                            vehicle v = vehicles.get(vnum);
                             if (v != null) {
                                  System.out.print("Enter recharge amount: ₹");
                                  double amt = sc.nextDouble();
                                   v.recharge(amt);
                                   logRecharge(vnum, amt);
                                   saveVehicles();
                               } else {
                                           System.out.println("Vehicle not found.");
                                      }
                                    }
                case 4 -> {generateDailyReport();
                }
                                    
                case 5 -> {
                    saveVehicles();
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

public static void logRecharge(String vnum, double amount) {
    try (FileWriter fw = new FileWriter("recharges.txt", true)) {
        String time = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        fw.write(vnum + "," + amount + "," + time + "\n");
    } catch (IOException e) {
        System.out.println("Error logging recharge.");
    }
}


  private static void processToll(Scanner sc) throws Exception {
    System.out.print("Enter vehicle number: ");
    String number = sc.nextLine();

    if (vehicles.containsKey(number)) {
      vehicle v = vehicles.get(number);
      System.out.println("✅ Vehicle found: " + v.ownerName + " | Balance: ₹" + v.balance);

      double toll = 80.0; // fixed toll amount
      if (v.balance >= toll) {
        v.balance -= toll;
        System.out.println("💸 Toll deducted: ₹" + toll);
        System.out.println("New balance: ₹" + v.balance);
        logTransaction(v.numberPlate, toll, v.balance);
      } else {
        System.out.println("❌ Insufficient balance! Please recharge FASTag.");
      }
    } else {
      System.out.println("❌ Vehicle not found in database.");
    }
  }

  private static void viewVehicles() {
    System.out.println("\n--- Vehicle Database ---");
    for (vehicle v : vehicles.values()) {
      System.out.println(v);
    }
  }

  private static void loadVehicles() {
    try (BufferedReader br = new BufferedReader(new FileReader(VEHICLE_FILE))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split("\\|");
        vehicles.put(parts[0], new vehicle(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3]));
      }
    } catch (Exception e) {
      System.out.println("No vehicle file found, starting fresh.");
    }
  }

  public static void generateDailyReport() {
    double totalRevenue = 0;
    int vehicleCount = 0;
    int rechargeCount = 0;
    List<String> lowBalanceVehicles = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader("transactions.txt"))) {
        String line;
        String today = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        while ((line = br.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length >= 4) {
                String dateTime = parts[0];
                if (dateTime.startsWith(today)) {
                    vehicleCount++;
                    totalRevenue += Double.parseDouble(parts[2]);
                    double balance = Double.parseDouble(parts[3]);
                    if (balance < 50) {
                        lowBalanceVehicles.add(parts[1]);
                    }
                }
            }
        }
    } catch (IOException e) {
        System.out.println("Error reading transactions.");
    }

    try (BufferedReader br = new BufferedReader(new FileReader("recharges.txt"))) {
        String line;
        String today = new SimpleDateFormat("dd/MM/yyyy HH::mm").format(new Date());

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                String dateTime = parts[2];
                if (dateTime.startsWith(today)) {
                    rechargeCount++;
                }
            }
        }
    } catch (IOException e) {
        System.out.println("Error reading recharges.");
    }

    System.out.println("\n--- Daily Summary Report ---");
    System.out.println("Date: " + new SimpleDateFormat("dd/MM/yyyy HH::mm").format(new Date()));
    System.out.println("Total Vehicles Passed: " + vehicleCount);
    System.out.println("Total Revenue Collected: ₹" + totalRevenue);
    System.out.println("Total Recharges: " + rechargeCount);
    System.out.println("Vehicles with Low Balance (< ₹50): " + lowBalanceVehicles);
}

  public static double getTollRate(String type) {
    return switch (type.toLowerCase()) {
      case "car" -> 80.0;
      case "bike" -> 40.0;
      case "truck" -> 150.0;
      case "bus" -> 120.0;
      default -> 100.0; // fallback
    };
  }

  private static void saveVehicles() {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(VEHICLE_FILE))) {
      for (vehicle v : vehicles.values()) {
        bw.write(v.getNumber() + "|" + v.getOwner() + "|" + v.getBalance() + "|" + v.type);
        bw.newLine();
      }
    } catch (IOException e) {
      System.out.println("Error saving vehicles: " + e.getMessage());
    }
  }

  public static void logTransaction(String numberPlate, double amount, double newBalance) {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANSACTION_FILE, true))) {
      String time = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
      bw.write(time + "|" + numberPlate + "|" + amount + "|" + newBalance);
      bw.newLine();
    } catch (IOException e) {
      System.out.println("Error logging transaction: " + e.getMessage());
    }
  }
}