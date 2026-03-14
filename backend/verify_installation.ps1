$baseUrl = "http://localhost:8080/api"

Write-Host "Checking Backend Health..."
try {
    $health = Invoke-RestMethod -Method Get -Uri "$baseUrl/health"
    Write-Host "Health Check Passed: $health" -ForegroundColor Green
} catch {
    Write-Host "Error: Service is not running on port 8080. Please run 'gradlew bootRun' first." -ForegroundColor Red
    exit
}

Write-Host "`nTesting Database Write (Create Transaction)..."
$body = @{
    amount = 100.50
    category = "TestCategory"
    note = "Persistence Test Entry"
    date = (Get-Date).ToString("yyyy-MM-dd")
} | ConvertTo-Json

try {
    $tx = Invoke-RestMethod -Method Post -Uri "$baseUrl/transactions" -ContentType "application/json" -Body $body
    Write-Host "Success! Created Transaction ID: $($tx.id)" -ForegroundColor Green
} catch {
    Write-Host "Failed to create transaction. Check database connection." -ForegroundColor Red
    Write-Host $_
    exit
}

Write-Host "`nTesting Database Read (List Transactions)..."
try {
    $list = Invoke-RestMethod -Method Get -Uri "$baseUrl/transactions"
    $found = $list | Where-Object { $_.id -eq $tx.id }

    if ($found) {
        Write-Host "Success! Found the transaction we just created." -ForegroundColor Green
        Write-Host "ID: $($found.id)"
        Write-Host "Amount: $($found.amount)"
        Write-Host "Note: $($found.note)"
    } else {
        Write-Host "Error: Created transaction was not found in the list!" -ForegroundColor Red
    }
} catch {
    Write-Host "Failed to retrieve transactions." -ForegroundColor Red
    Write-Host $_
}

Write-Host "`n---------------------------------------------------"
Write-Host "To verify persistence:"
Write-Host "1. Stop the backend server (Ctrl+C)"
Write-Host "2. Restart the backend server (gradlew bootRun)"
Write-Host "3. Run this script again (or check via browser/curl)"
Write-Host "4. You should see the previous transaction(s) still listed."
Write-Host "---------------------------------------------------"

