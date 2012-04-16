call mvn clean install
cd symbion-profiler
copy /y target\symbion-profiler-1.0.0-jar-with-dependencies.jar D:\Programming\thesis\research\FirmManagement\bin
start D:\Programming\thesis\research\FirmManagement\bin\symbion.bat
cd ..
cd symbion-console
java -jar target\symbion-console-1.0.0-jar-with-dependencies.jar