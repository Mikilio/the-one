@echo off


echo Starting simulations

echo Running originClassRoom.txt (1 run)
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar %* -b 1 apocalypse_settings/originClassRoom.txt
timeout /t 2 >nul

echo Running FingerHallwayInfectionOrigin.txt (1 run)
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar %* -b 1 apocalypse_settings/FingerHallwayInfectionOrigin.txt
timeout /t 2 >nul

echo Running ClassRoomSameFinger.txt (19 runs)
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar %* -b 19 apocalypse_settings/ClassRoomSameFinger.txt

echo Running BalconyNordEtage1.txt (1 run)
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar %* -b 1 apocalypse_settings/BalconyNordEtage1.txt
timeout /t 2 >nul

echo Running Bridge.txt (2 runs)
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar %* -b 2 apocalypse_settings/Bridge.txt

echo Running FingerHallwayUninfected.txt (5 runs)
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar %* -b 5 apocalypse_settings/FingerHallwayUninfected.txt
timeout /t 2 >nul

echo Running classRoomSameFloor.txt (1 run)
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar %* -b 1 apocalypse_settings/classRoomSameFloor.txt

echo Running Stairs.txt (4 runs)
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar %* -b 4 apocalypse_settings/Stairs.txt
timeout /t 2 >nul

echo Running Magistrale.txt (1 run)
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar %* -b 1 apocalypse_settings/Magistrale.txt

echo All simulations complete.
pause
