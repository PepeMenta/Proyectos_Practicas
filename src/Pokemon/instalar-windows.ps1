param(
    [string]$DbName = "juegopokemon",
    [string]$GameDbUser = "alex",
    [string]$GameDbPassword = "1234",
    [string]$PostgresSuperPassword = "postgres",
    [switch]$SkipInstallDependencies
)

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

if ($DbName -ne "juegopokemon") {
    throw "Ahora mismo los scripts SQL crean la base 'juegopokemon'. Usa -DbName juegopokemon o actualiza postgresql\schema.sql."
}

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Test-Admin {
    $identity = [Security.Principal.WindowsIdentity]::GetCurrent()
    $principal = New-Object Security.Principal.WindowsPrincipal($identity)
    return $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

function Get-CommandPath {
    param(
        [string]$Command,
        [string[]]$Candidates = @()
    )

    $cmd = Get-Command $Command -ErrorAction SilentlyContinue
    if ($cmd) {
        return $cmd.Source
    }

    foreach ($pattern in $Candidates) {
        $matches = Get-ChildItem $pattern -ErrorAction SilentlyContinue | Sort-Object FullName -Descending
        if ($matches) {
            return $matches[0].FullName
        }
    }

    return $null
}

function Install-WingetPackage {
    param(
        [string]$PackageId,
        [string]$DisplayName,
        [string]$Override = ""
    )

    Write-Step "Instalando $DisplayName"

    $args = @(
        "install", "-e", "--id", $PackageId,
        "--accept-package-agreements",
        "--accept-source-agreements"
    )

    if ($Override -ne "") {
        $args += @("--override", $Override)
    } else {
        $args += "--silent"
    }

    & winget @args
    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo instalar $DisplayName con winget."
    }
}

function ConvertTo-SqlLiteral {
    param([string]$Value)
    return "'" + $Value.Replace("'", "''") + "'"
}

function Start-PostgresService {
    $services = Get-Service -Name "postgresql*" -ErrorAction SilentlyContinue
    foreach ($service in $services) {
        if ($service.Status -ne "Running") {
            Start-Service $service.Name
        }
    }
}

if (-not (Test-Admin)) {
    throw "Abre PowerShell como administrador y ejecuta de nuevo: powershell -ExecutionPolicy Bypass -File .\instalar-windows.ps1"
}

if (-not $SkipInstallDependencies) {
    if (-not (Get-Command winget -ErrorAction SilentlyContinue)) {
        throw "No se encontro winget. Instala 'App Installer' desde Microsoft Store o actualiza Windows."
    }

    if (-not (Get-CommandPath "javac.exe" @(
        "$env:ProgramFiles\Microsoft\jdk-*\bin\javac.exe",
        "$env:ProgramFiles\Eclipse Adoptium\jdk-*\bin\javac.exe"
    ))) {
        Install-WingetPackage "Microsoft.OpenJDK.21" "OpenJDK 21"
    }

    if (-not (Get-CommandPath "chafa.exe" @(
        "$env:LOCALAPPDATA\Microsoft\WinGet\Links\chafa.exe",
        "$env:ProgramFiles\WinGet\Links\chafa.exe"
    ))) {
        Install-WingetPackage "hpjansson.Chafa" "Chafa"
    }

    if (-not (Get-CommandPath "psql.exe" @("$env:ProgramFiles\PostgreSQL\*\bin\psql.exe"))) {
        Install-WingetPackage "PostgreSQL.PostgreSQL" "PostgreSQL" "--mode unattended --unattendedmodeui none --superpassword $PostgresSuperPassword"
    }
}

$javac = Get-CommandPath "javac.exe" @(
    "$env:ProgramFiles\Microsoft\jdk-*\bin\javac.exe",
    "$env:ProgramFiles\Eclipse Adoptium\jdk-*\bin\javac.exe"
)
$java = Get-CommandPath "java.exe" @(
    "$env:ProgramFiles\Microsoft\jdk-*\bin\java.exe",
    "$env:ProgramFiles\Eclipse Adoptium\jdk-*\bin\java.exe"
)
$psql = Get-CommandPath "psql.exe" @("$env:ProgramFiles\PostgreSQL\*\bin\psql.exe")

if (-not $javac) { throw "No se encontro javac.exe. Reinstala OpenJDK 21 o ejecuta sin -SkipInstallDependencies." }
if (-not $java) { throw "No se encontro java.exe. Reinstala OpenJDK 21 o ejecuta sin -SkipInstallDependencies." }
if (-not $psql) { throw "No se encontro psql.exe. Reinstala PostgreSQL o ejecuta sin -SkipInstallDependencies." }

Write-Step "Arrancando servicio de PostgreSQL"
Start-PostgresService

Write-Step "Preparando rol de PostgreSQL para el juego"
$env:PGPASSWORD = $PostgresSuperPassword
$roleSql = @"
DO `$`$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = $(ConvertTo-SqlLiteral $GameDbUser)) THEN
        CREATE ROLE $GameDbUser LOGIN PASSWORD $(ConvertTo-SqlLiteral $GameDbPassword);
    ELSE
        ALTER ROLE $GameDbUser LOGIN PASSWORD $(ConvertTo-SqlLiteral $GameDbPassword);
    END IF;
END
`$`$;
"@
& $psql -h localhost -U postgres -d postgres -v ON_ERROR_STOP=1 -c $roleSql
if ($LASTEXITCODE -ne 0) {
    throw "No se pudo conectar a PostgreSQL como postgres. Revisa la clave de -PostgresSuperPassword."
}

Write-Step "Creando base de datos e importando datos"
Push-Location (Join-Path $ProjectRoot "postgresql")
try {
    & $psql -h localhost -U postgres -d postgres -v ON_ERROR_STOP=1 -f "schema.sql"
    if ($LASTEXITCODE -ne 0) { throw "Fallo schema.sql." }

    & $psql -h localhost -U postgres -d $DbName -v ON_ERROR_STOP=1 -f "import.sql"
    if ($LASTEXITCODE -ne 0) { throw "Fallo import.sql." }
} finally {
    Pop-Location
}

Write-Step "Dando permisos al usuario del juego"
$grantSql = @"
GRANT CONNECT ON DATABASE $DbName TO $GameDbUser;
GRANT USAGE ON SCHEMA public TO $GameDbUser;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO $GameDbUser;
GRANT SELECT ON ALL SEQUENCES IN SCHEMA public TO $GameDbUser;
"@
& $psql -h localhost -U postgres -d $DbName -v ON_ERROR_STOP=1 -c $grantSql
if ($LASTEXITCODE -ne 0) { throw "No se pudieron aplicar permisos." }

Write-Step "Compilando JuegoRPG"
Push-Location $ProjectRoot
try {
    & $javac -encoding UTF-8 -cp ".\lib\postgresql.jar" ".\JuegoRPG.java"
    if ($LASTEXITCODE -ne 0) { throw "No se pudo compilar JuegoRPG.java." }
} finally {
    Pop-Location
}

Write-Step "Creando lanzador local"
$launcher = @"
@echo off
setlocal
cd /d "%~dp0"
chcp 65001 >nul

set "JUEGORPG_DB_URL=jdbc:postgresql://localhost:5432/$DbName"
set "JUEGORPG_DB_USER=$GameDbUser"
set "JUEGORPG_DB_PASSWORD=$GameDbPassword"

where chafa >nul 2>nul
if errorlevel 1 (
    echo Aviso: chafa no esta en PATH. El juego usara el renderizador Java de respaldo.
)

java -Dfile.encoding=UTF-8 -cp ".;lib\postgresql.jar" JuegoRPG
pause
"@
Set-Content -Path (Join-Path $ProjectRoot "jugar-windows.local.bat") -Value $launcher -Encoding ASCII

Write-Step "Probando conexion con el usuario del juego"
$env:PGPASSWORD = $GameDbPassword
& $psql -h localhost -U $GameDbUser -d $DbName -v ON_ERROR_STOP=1 -c "SELECT COUNT(*) AS pokemons FROM pokemons;"
if ($LASTEXITCODE -ne 0) { throw "La prueba de conexion con $GameDbUser fallo." }

Write-Step "Instalacion terminada"
Write-Host "Para jugar, abre una terminal en esta carpeta y ejecuta: .\jugar-windows.local.bat" -ForegroundColor Green
