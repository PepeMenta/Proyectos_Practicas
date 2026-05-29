# Instalacion en Windows

Este proyecto incluye un instalador PowerShell para preparar el juego en Windows.

## Uso rapido

1. Abre PowerShell como administrador.
2. Entra en la carpeta `Pokemon`.
3. Ejecuta:

```powershell
powershell -ExecutionPolicy Bypass -File .\instalar-windows.ps1
```

Tambien puedes hacer doble click en `instalar-windows.bat`, pero si falla por permisos abre PowerShell como administrador.

## Que instala/prepara

- OpenJDK 21 con `winget`.
- PostgreSQL con `winget`.
- Chafa con `winget`, para que las imagenes se vean bien en terminal.
- Base de datos `juegopokemon`.
- Usuario de base de datos `alex` con clave `1234`.
- Imports desde `files/estados`, `files/movimientos`, `files/pokemons`, `files/evoluciones` y `files/record`.
- Compilacion de `JuegoRPG.java`.
- Lanzador `jugar-windows.local.bat`.

## Ejecutar el juego

Despues de instalar:

```bat
.\jugar-windows.local.bat
```

## Opciones utiles

Si PostgreSQL ya existe pero la clave del usuario `postgres` no es `postgres`:

```powershell
powershell -ExecutionPolicy Bypass -File .\instalar-windows.ps1 -PostgresSuperPassword "tu_clave"
```

Si ya tienes Java, PostgreSQL y Chafa instalados:

```powershell
powershell -ExecutionPolicy Bypass -File .\instalar-windows.ps1 -SkipInstallDependencies
```

Aviso: el instalador recrea la base `juegopokemon`, asi que borra los datos previos de esa base.
