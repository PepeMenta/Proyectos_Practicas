@echo off
setlocal
cd /d "%~dp0"
chcp 65001 >nul

set "JUEGORPG_DB_URL=jdbc:postgresql://localhost:5432/juegopokemon"
set "JUEGORPG_DB_USER=alex"
set "JUEGORPG_DB_PASSWORD=1234"

where chafa >nul 2>nul
if errorlevel 1 (
    echo Aviso: chafa no esta en PATH. El juego usara el renderizador Java de respaldo.
)

java -Dfile.encoding=UTF-8 -cp ".;lib\postgresql.jar" JuegoRPG
pause
