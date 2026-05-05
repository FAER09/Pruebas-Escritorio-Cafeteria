@echo off
echo Compilando Cafeteria...
if not exist out mkdir out

javac -encoding UTF-8 -d out -sourcepath src ^
  src\Main.java ^
  src\controlador\*.java ^
  src\modelo\*.java ^
  src\persistencia\*.java ^
  src\util\*.java ^
  src\vista\*.java

if %ERRORLEVEL% == 0 (
    echo Compilacion exitosa. Iniciando aplicacion...
    java -cp out Main
) else (
    echo Error en la compilacion.
    pause
)
