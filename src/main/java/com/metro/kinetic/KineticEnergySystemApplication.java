package com.metro.kinetic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KineticEnergySystemApplication {

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println(" SMART URBAN SUBWAY KINETIC FLOOR ENERGY HARVESTING SYSTEM ");
        System.out.println(" Starting Spring Boot with MySQL (Database: kinetic_energy_db)   ");
        System.out.println(" Currency: Indian Rupees (INR ₹)                                 ");
        System.out.println("==================================================================");
        SpringApplication.run(KineticEnergySystemApplication.class, args);
        System.out.println(">>> APPLICATION STARTED! Access at: http://localhost:8080");
    }
}
