@echo off


echo Starting simulations

echo Running originClassRoom.txt (1 run)
java -Xmx512M -cp target;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1 example_settings/originClassRoom.txt
timeout /t 2 >nul

echo Running FingerHallwayInfectionOrigin.txt (1 run)
java -Xmx512M -cp target;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1 example_settings/FingerHallwayInfectionOrigin.txt
timeout /t 2 >nul

echo Running ClassRoomSameFinger.txt (19 runs)
java -Xmx512M -cp target;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 19 example_settings/ClassRoomSameFinger.txt

echo Running BalconyNordEtage1.txt (1 run)
java -Xmx512M -cp target;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1 example_settings/BalconyNordEtage1.txt
timeout /t 2 >nul

echo Running Bridge.txt (2 runs)
java -Xmx512M -cp target;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 2 example_settings/Bridge.txt

echo Running FingerHallwayUninfected.txt (5 runs)
java -Xmx512M -cp target;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 5 example_settings/FingerHallwayUninfected.txt
timeout /t 2 >nul

echo Running classRoomSameFloor.txt (1 run)
java -Xmx512M -cp target;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1 example_settings/classRoomSameFloor.txt

echo Running Stairs.txt (4 runs)
java -Xmx512M -cp target;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 4 example_settings/Stairs.txt
timeout /t 2 >nul

echo Running Magistrale.txt (1 run)
java -Xmx512M -cp target;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1 example_settings/Magistrale.txt

echo Running OtherFloors.txt (1 run)
java -Xmx512M -cp target;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1 example_settings/OtherFloors.txt
timeout /t 2 >nul

echo All simulations complete.
pause
