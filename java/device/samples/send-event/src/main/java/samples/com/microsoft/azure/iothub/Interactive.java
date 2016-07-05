package samples.com.microsoft.azure.iothub;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import samples.com.microsoft.azure.iothub.Interactive.Location;

public class Interactive {

    Scanner input = new Scanner ( System.in );
    String[] category = new String[] {"Produce", "Dangerous Goods", "Entertainment", "Cleaning", "Office Supplies", "Automotive"};
    private static List<Location> locations = new ArrayList<Location>() {{
        add(new Location(13.0273660,77.6557600, "Bangalore"));
        add(new Location(12.5715990,77.5945630, ""));
        add(new Location(12.6715990,77.4945630, ""));
        add(new Location(12.7715990,77.3945630, ""));
        add(new Location(12.8715990,77.2945630, ""));
        add(new Location(12.9715990,76.1945630, ""));
    }};
    EventManager eventManager = new EventManager();
    
    public void add() {
        System.out.println("Enter total value of goods sold : ");
        int test = Integer.parseInt(input.nextLine());
        eventManager.sentEvent(getJson(test, null));
        return;
    }
    
    public void enterItems()
    {
        int test = 0;
        int subtotal = 0;
        int itemoption;
        do
        {
            System.out.println("Choose items from below options");
            System.out.println("1. Apple");
            System.out.println("2. Banana");
            System.out.println("3. Return to main menu");
            itemoption = Integer.parseInt(input.nextLine());
            switch(itemoption)
            {
            case 1:
                System.out.println("Enter amount for apple : ");
                test = Integer.parseInt(input.nextLine());
                System.out.println("The amount for apple is "+test);
                subtotal += test;
                break;
            case 2:
                System.out.println("Enter amount for banana : ");
                test = Integer.parseInt(input.nextLine());
                System.out.println("The amount for banana is "+test);
                subtotal += test;
                break;
            case 3:
                return;
            }
        }while(itemoption<3);
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
            System.out.println("|        3. Exit                         |");
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
                eventManager.closeConnection();
                System.exit(0);
            default:
                System.out.println("Invalid selection");
            }
        }while(swValue < 4);
    }
    
    private String getJson(int total, List<Item> items) {
        StringBuilder builder = new StringBuilder();
        builder.append("{'total': " + total);
        Location location = getRandomLocation();
        builder.append(",'lat': " + location.getLat());
        builder.append(",'lng': " + location.getLng());
        if(items != null && items.size() > 0) {
            builder.append(", items: [");
            for (Item item : items) {
                builder.append("{\"name\": \""+ item.getName()+ "\", \"quantity\": " + item.getQuantity() + ", \"subtotal\": " + item.getSubtotal() + "}");
            }
            builder.append("]");
        }
        builder.append("}");
        return builder.toString();
    }
    
    private Location getRandomLocation() {
        return locations.get(ThreadLocalRandom.current().nextInt(0, locations.size()));
    }
    
    class Item {
        private String name;
        private int quantity;
        private int subtotal;
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
        
    }
    
    public static void main(String[] args) {
        Interactive a = new Interactive();
        a.menu(a);
        
        // Display menu graphics

    }

}

