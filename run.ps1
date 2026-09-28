Write-Host "=========================================================================" -ForegroundColor Cyan
Write-Host "  Smart Urban Subway Kinetic Floor Energy Harvesting System" -ForegroundColor Green
Write-Host "  Java 21 + Spring Boot + Spring Data JPA + MySQL" -ForegroundColor Yellow
Write-Host "=========================================================================" -ForegroundColor Cyan
Write-Host "Database : kinetic_energy_db (MySQL localhost:3306)" -ForegroundColor White
Write-Host "Currency : Indian Rupees (INR)" -ForegroundColor White
Write-Host "URL      : http://localhost:8080" -ForegroundColor Yellow
Write-Host ""

if (Test-Path "target\kinetic-energy-system-1.0.0.jar") {
    Write-Host "Starting pre-built JAR package..." -ForegroundColor Green
    java -jar target\kinetic-energy-system-1.0.0.jar
} else {
    Write-Host "JAR not found. Running with Maven..." -ForegroundColor Yellow
    mvn spring-boot:run
}
