class VehicleProcessor extends Thread {
    private String numberPlate;

    public VehicleProcessor(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    
    public void run() {
        synchronized (FastagApp.vehicles) { 
            if (FastagApp.vehicles.containsKey(numberPlate)) {
                vehicle v = FastagApp.vehicles.get(numberPlate);
                double tollAmount = FastagApp.getTollRate(v.type);
                if (v.balance >= tollAmount) {
                    v.balance -= tollAmount;
                    System.out.println(" " + numberPlate + " | Toll deducted: ₹" + tollAmount +
                                       " | New balance: ₹" + v.balance);
                    FastagApp.logTransaction(numberPlate, tollAmount, v.balance);
                } else {
                    System.out.println("" + numberPlate + " | Insufficient balance!");
                }
            } else {
                System.out.println("" + numberPlate + " | Vehicle not found!");
            }
        }
    }
}