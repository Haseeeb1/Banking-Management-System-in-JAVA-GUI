package com.example.banking_managemen_system;

public class history {
   protected String type;
   protected String date;
   protected Double amount;
   protected Double balance;

   public history(String t, String d, Double a, Double b){
       this.type=t;
       this.date=d;
       this.amount=a;
       this.balance=b;
   }

   public String getType(){
       return type;
   }

   public String getDate(){
       return date;
   }

   public Double getAmount(){
       return amount;
   }
   public Double getBalance(){
       return balance;
   }

}
