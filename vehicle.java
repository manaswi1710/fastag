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

    @Override
    public String toString() {
        return numberPlate + " | " + ownerName + " | Balance: ₹" + balance;
    }
}