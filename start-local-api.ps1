param(
    [int]$Port = 8080,
    [switch]$Build
)

$ErrorActionPreference = "Stop"

$ApiDir = $PSScriptRoot
$ProjectRoot = Resolve-Path (Join-Path $ApiDir "..\..")
$CredentialsPath = Join-Path $ProjectRoot "firebase-service-account.json"
$JarPath = Join-Path $ApiDir "target\padelstack-api-1.0.0.jar"

if (!(Test-Path $CredentialsPath)) {
    throw "No encuentro firebase-service-account.json en $ProjectRoot"
}

if ($Build -or !(Test-Path $JarPath)) {
    Push-Location $ApiDir
    try {
        mvn -q -DskipTests package
    } finally {
        Pop-Location
    }
}

$env:FIREBASE_CREDENTIALS_PATH = $CredentialsPath
$env:FIREBASE_PROJECT_ID = "padelstack"
$env:FIREBASE_STORAGE_BUCKET = "padelstack.firebasestorage.app"
$env:APP_PUBLIC_BASE_URL = "http://10.0.2.2:$Port"

Write-Host "PadelStack API local en http://localhost:$Port"
Write-Host "Emulador Android: http://10.0.2.2:$Port"

Push-Location $ApiDir
try {
    java "-Dserver.address=0.0.0.0" "-Dserver.port=$Port" -jar $JarPath
} finally {
    Pop-Location
}
