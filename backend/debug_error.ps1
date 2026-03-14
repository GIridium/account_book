$baseUrl = "http://localhost:8080/api/transactions"
try {
    $response = Invoke-WebRequest -Uri $baseUrl -Method Get
    Write-Host "Success: $($response.Content)"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    $responseBody = $reader.ReadToEnd()
    Write-Host "Response Body: $responseBody"
}

