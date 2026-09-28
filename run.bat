@echo off
title Smart Urban Subway Kinetic Floor Energy Harvesting System
echo =========================================================================
echo   Smart Urban Subway Kinetic Floor Energy Harvesting System
echo   Java 21 + Spring Boot + Spring Data JPA + MySQL
echo =========================================================================
echo.
echo Database: kinetic_energy_db (MySQL localhost:3306)
echo Currency: Indian Rupees (INR)
echo Access at: http://localhost:8080
echo.

if exist "target\kinetic-energy-system-1.0.0.jar" (
    echo Launching pre-built JAR package...
    java -jar target\kinetic-energy-system-1.0.0.jar
) else (
    echo JAR not found. Running with Maven Spring Boot plugin...
    mvn spring-boot:run
)
pause
