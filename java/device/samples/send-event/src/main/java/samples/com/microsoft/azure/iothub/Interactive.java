package samples.com.microsoft.azure.iothub;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Interactive {

    Scanner input = new Scanner ( System.in );
    private static String[] category = new String[] {"Produce", "Dangerous Goods", "Entertainment", "Cleaning", "Office Supplies", "Automotive"};
    private static List<Location> locations = new ArrayList<Location>() {{
        add(new Location(12.95396,77.4908534, "Bangalore"));
        add(new Location(12.3106435,76.6006702, "Mysore"));
        add(new Location(12.5224578,76.8792097, "Mandya"));
        add(new Location(12.7143589,77.2668972, "Ramanagara"));
        add(new Location(13.1297379,78.1085656, "Kolar"));
        add(new Location(13.3493819,77.0625894, "Tumkur"));
    }};
    private static List<Item> items = new ArrayList<Item>() {{
        add(new Item("Apples", category[0], 12));
        add(new Item("Fireworks/pyrotechnics", category[1], 13));
        add(new Item("Gaming Console", category[2], 14));
        add(new Item("Abrasive Cleaners",category[3], 15));
        add(new Item("Cubicle supplies", category[4], 16));
        add(new Item("Automotive supplies", category[5], 17));
    }};
    EventManager eventManager = new EventManager();
    
    public void add() {
        System.out.println("Enter total value of goods sold : ");
        int test = Integer.parseInt(input.nextLine());
        eventManager.sentEvent(getJson(test, null));
        return;
    }
    
    public void enterItems() {
        int total = 0;
        int subtotal = 0;
        int itemoption;
        int quantity;
        List<Item> selectedItems = new ArrayList<>();
        
        System.out.println("Choose item from above options: ");
        int index = 1;
        for (Item item : items) {
            System.out.println(index + ". " + item.getName());
            index++;
        }
        System.out.println(index + ". Finish");
        
        do {
            System.out.println("Choose item from above options: ");
            itemoption = Integer.parseInt(input.nextLine());
            
            if(itemoption < items.size()) {
                Item chosenItem = items.get(itemoption - 1);
                System.out.println("Enter quantity for " + chosenItem.getName()+ ": ");
                quantity = Integer.parseInt(input.nextLine());
                subtotal = quantity * chosenItem.getPricePerUnit();
                System.out.println("Selected " + chosenItem.getName() + ", for price " + subtotal);
                total += subtotal;
                
                Item selectedItem = new Item(chosenItem.getName(), chosenItem.getCategory(), chosenItem.getPricePerUnit());
                selectedItem.setSubtotal(subtotal);
                selectedItem.setQuantity(quantity);
                selectedItems.add(selectedItem);
            }
            
        } while(itemoption < items.size());
        
        System.out.println("Total purchase value " + total);
        eventManager.sentEvent(getJson(total, selectedItems));
    }
    
    public void menu(Interactive a)
    {
        int swValue;
        do
        {
            System.out.println("==========================================");
            System.out.println("|   IPOS MENU SELECTION DEMO             |");
            System.out.println("=========================================|");
            //System.out.println("| Options:                             |");
            System.out.println("|        1. Enter Total                  |");
            System.out.println("|        2. Enter items                  |");
            System.out.println("|        3. Demo Mode 1                  |");
            System.out.println("|        4. Exit                         |");
            System.out.println("==========================================");
            System.out.println("Enter your choice : ");
            swValue = Integer.parseInt(input.nextLine());

            // Switch construct
            switch (swValue) {
            case 1:
                //System.out.println("");
                a.add();
                break;
            case 2:
                a.enterItems();
                break;
            case 3:
                startDemoMode1(50);
                break;
            case 4:
                eventManager.closeConnection();
                System.exit(0);
            default:
                System.out.println("Invalid selection");
            }
        }while(swValue < 5);
    }
    
    private void startDemoMode1(int totalMessages) {
        for(int i=0; i<totalMessages; i++) {
            eventManager.sentEvent(getJson(ThreadLocalRandom.current().nextInt(0, 10000), null));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String getJson(int total, List<Item> items) {
        StringBuilder builder = new StringBuilder();
        builder.append("{'total': " + total);
        Location location = getRandomLocation();
        builder.append(",'lat': " + location.getLat());
        builder.append(",'lng': " + location.getLng());
        if(items != null && items.size() > 0) {
            builder.append(", 'items': [");
            for (Item item : items) {
                builder.append("{'name': '"+ item.getName()+ "', 'category': '" + item.getCategory() + "', 'quantity': " + item.getQuantity() + ", 'subtotal': " + item.getSubtotal() + "},");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append("]");
        }
        builder.append("}");
        return builder.toString();
    }
    
    private Location getRandomLocation() {
        Location location = locations.get(ThreadLocalRandom.current().nextInt(0, locations.size()));
        double[] randomNearbyLocation = getRandomNearbyLocation(location.getLat(), location.lng, 10000);
        location.setLat(randomNearbyLocation[0]);
        location.setLng(randomNearbyLocation[1]);
        return location;
    }
    
    static class Item {
        private String name;
        private String category;
        private int pricePerUnit;
        private int quantity;
        private int subtotal;
        
        public Item(String name, String category, int pricePerUnit) {
            super();
            this.name = name;
            this.category = category;
            this.pricePerUnit = pricePerUnit;
        }
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getQuantity() {
            return quantity;
        }
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
        public int getSubtotal() {
            return subtotal;
        }
        public void setSubtotal(int subtotal) {
            this.subtotal = subtotal;
        }
        public String getCategory() {
            return category;
        }
        public void setCategory(String category) {
            this.category = category;
        }
        public int getPricePerUnit() {
            return pricePerUnit;
        }
        public void setPricePerUnit(int pricePerUnit) {
            this.pricePerUnit = pricePerUnit;
        }
    }
    
    static class Location {
        private double lat;
        private double lng;
        private String name;

        public Location(double lat, double lng, String name) {
            super();
            this.lat = lat;
            this.lng = lng;
            this.name = name;
        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }

        public String getName() {
            return name;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
        
    }
    
    public static void main(String[] args) {
        Interactive a = new Interactive();
        a.menu(a);
        
        // Display menu graphics
//        getRandomNearbyLocation(12.95396,77.4908534, 10000);
    }
    
    public static double[] getRandomNearbyLocation(double x0, double y0, int radius) {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;
        return new double [] {foundLongitude, foundLatitude};
    }

}

