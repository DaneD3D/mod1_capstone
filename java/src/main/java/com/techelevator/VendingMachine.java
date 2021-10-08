package com.techelevator;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class VendingMachine
{
    public LinkedHashMap<String,VendingItem> inventor = new LinkedHashMap<String, VendingItem>();
    private Scanner scanner = new Scanner(System.in);
    public BigDecimal userBalance = new BigDecimal(0.00);

    public VendingMachine()
    {
            // Gets stock of items from Inventory File
            try
            {
                File inventoryTemp = new File("vendingmachine.csv");
                BufferedReader reader = new BufferedReader(new FileReader(inventoryTemp));
                String line = reader.readLine();
                while (line != null)
                {
                    String[] invSegments = line.split("\\|");
                    inventor.put(invSegments[0] ,new VendingItem(invSegments[0],invSegments[1],new BigDecimal(invSegments[2]),invSegments[3]));
                    line = reader.readLine();
                }
            } catch (IOException e)
            {
                System.out.println("Vending Machine File Could Not Be Found Shutting Down...");
                System.exit(0);
            }

    }

    public void purchaseMenu()
    {
        boolean isTransactionFinished = false;
        do
        {
            System.out.println("\n(1) Feed Money\n(2) Select Product\n(3) Display Items\n(4) Finish Transaction\n\nRemaining Balance: $" + userBalance);
            System.out.print("Please enter an option >>> ");
            String choice = scanner.nextLine();

            switch (choice)
            {
                case "1":
                    getCustomerMoney();
                    break;
                case "2":
                    itemSelectionProccess();
                    break;
                case "3":
                    printVendingContents();
                    break;


                case "4":
                    isTransactionFinished = true;

                    System.out.println("Thank for using the Home Alone Snack Machine");
                    break;


                default:
                {
                    System.out.println("Invalid Entry");
                }
            }
        }while(isTransactionFinished == false);
    }


    public void getCustomerMoney()
    {
        boolean validResponse = false;
        while (!validResponse) {
            System.out.print("Please enter your money: ");
            String moneyInputString = scanner.nextLine();
            if (isNumeric(moneyInputString)) {
                userBalance = userBalance.add(new BigDecimal(moneyInputString));
                userBalance.setScale(2, RoundingMode.HALF_UP);
                break;
            }
        }
        System.out.println("$" + userBalance + ", Great! Let's get some snacks!");
    }

    public void itemSelectionProccess()
    {
        System.out.println("Which item do you want");
        String userInput = scanner.nextLine();
        userInput = userInput.toUpperCase(Locale.ROOT);
        if(inventor.containsKey(userInput))
        {
            if(userBalance.doubleValue() - inventor.get(userInput).getPrice().doubleValue() > 0.00)
            {
                if(inventor.get(userInput).getInStockAmount() != 0)
                {
                    BigDecimal startingBal = inventor.get(userInput).getPrice();
                    BigDecimal itemPrice = inventor.get(userInput).getPrice();
                    userBalance = userBalance.subtract(itemPrice);
                    inventor.get(userInput).itemIsPurchased();
                    System.out.println("You choose " + inventor.get(userInput).getItemName());
                    System.out.println(getSound(inventor.get(userInput).getItemType()));
                    System.out.println("Your New Balance is $" + userBalance);
                    logSale(startingBal);
                }
                else
                {
                    System.out.println("Item is SOLD OUT!");
                }
            }
            else
            {
                System.out.println("Not Enough Money");
            }
        }
        else
        {
            System.out.println("Not A Valid Location");
        }
    }

    public String getSound(String itemType)
    {
        switch (itemType)
        {
            case "Chip":
                return "Crunch Crunch, Yum!";
            case "Candy":
                return "Munch Munch, Yum!";
            case "Drink":
                return "Glug Glug, Yum!";
            case "Gum":
                return "Chew Chew, Yum!";
            default:
                return "THIS SNACK IS KINDA SUS";
        }


    }

    public void logSale(BigDecimal startBalance)
    {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime presentTime = LocalDateTime.now();
        try {

            FileWriter newPrint = new FileWriter("Log.txt", true);
            newPrint.write("\n" + timeFormat.format(presentTime) + " " + startBalance  + " " + userBalance);

            newPrint.close();
        } catch (IOException e) {}
    }

    public boolean isNumeric(String moneyInput) {
        try {
            if(Integer.parseInt(moneyInput) > 0)
            {
                return true;
            }
        } catch (NumberFormatException e)
        {
            System.out.println(moneyInput + " is not a proper value.");
        }
        return false;
    }

    public void printVendingContents()
    {
        char previousId = inventor.get("A1").getLocationId().charAt(0);
        int longestItemLength = 0;
        int longestItemTypeLength = 0;
        for(Map.Entry<String, VendingItem> item : inventor.entrySet())
        {
            if(item.getValue().getItemName().length() > longestItemLength)
            {
                longestItemLength = item.getValue().getItemName().length();
            }
            if(item.getValue().getItemType().length() > longestItemTypeLength)
            {
                longestItemTypeLength = item.getValue().getItemType().length();
            }
        }

        for(Map.Entry<String, VendingItem> item : inventor.entrySet())
        {
            String itemName = item.getValue().getItemName() + (" ").repeat(longestItemLength - item.getValue().getItemName().length());
            String itemType = item.getValue().getItemType() + (" ").repeat(longestItemTypeLength - item.getValue().getItemType().length());
            if(item.getValue().getLocationId().charAt(0) == previousId)
            {
                System.out.print("|"+ item.getKey() + " " + itemName + " " + itemType + " $" + item.getValue().getPrice()  + "  InStock: " + item.getValue().getInStockAmount() + " " );
            }
            else
            {
                System.out.print("\n|"+ item.getValue().getLocationId() + " " + itemName  + " " + itemType  + " $" + item.getValue().getPrice()  + "  InStock: " + item.getValue().getInStockAmount() + " " );
            }
            previousId = item.getValue().getLocationId().charAt(0);
        }

    }
}
