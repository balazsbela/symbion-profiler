cd symbion-profiler
call mvn clean install
copy /y target\profiler-1.0-jar-with-dependencies.jar D:\Programming\thesis\research\FirmManagement\bin
start D:\Programming\thesis\research\FirmManagement\bin\symbion.bat
cd ..
cd symbion-console
call mvn clean install
java -jar target\console-1.0-jar-with-dependencies.jar