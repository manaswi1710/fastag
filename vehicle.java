public class vehicle {
    String numberPlate;
    String ownerName;
    double balance;
    String type;

    public vehicle(String numberPlate, String ownerName, double balance,String type) {
        this.numberPlate = numberPlate;
        this.ownerName = ownerName;
        this.balance = balance;
        this.type = type;
    }

 public void recharge(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Recharge successful. New balance: ₹" + balance);
        } else {
            System.out.println("Invalid recharge amount.");
        }
    }

     public double getBalance() {
        return balance;
    }

    public String getNumber() {
        return numberPlate;
    }

    public String getOwner() {
        return ownerName;
    }


    public String toString() {
        return numberPlate + " | " + ownerName + " | Balance: ₹" + balance;
    }
}