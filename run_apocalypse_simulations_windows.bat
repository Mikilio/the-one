@echo off


echo Starting simulations

echo Running originClassRoom0.txt (1 run)
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1   apocalypse_settings/originClassRoom0.txt
timeout /t 2 >nul

echo Running FingerHallwayInfectionOrigin1.txt (1 run)
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1   apocalypse_settings/FingerHallwayInfectionOrigin1.txt
timeout /t 2 >nul

echo Running ClassRoomSameFingerXX.txt (19 runs)
for /l %%i in (2,1,20) do (
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1   apocalypse_settings/ClassRoomSameFinger%%i.txt
)

echo Running BalconyNordEtage121.txt (1 run)
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1   apocalypse_settings/BalconyNordEtage121.txt
timeout /t 5 >nul


echo Running FingerHallwayUninfectedXX.txt (5 runs)
for /l %%i in (22,1,26) do (
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1   apocalypse_settings/FingerHallwayUninfected%%i.txt
)

echo Running BridgeXX.txt (2 runs)
for /l %%i in (27,1,28) do (
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1   apocalypse_settings/Bridge%%i.txt
)
timeout /t 2 >nul


echo Running classRoomSameFloorXX.txt (80 runs)
for /l %%i in (29,1,108) do (
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1   apocalypse_settings/classRoomSameFloor%%i.txt
)

echo Running StairsXX.txt (4 runs)
for /l %%i in (109,1,112) do (
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1   apocalypse_settings/Stairs%%i.txt
)
timeout /t 2 >nul

echo Running Magistrale114.txt (1 run)
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1   apocalypse_settings/Magistrale114.txt

echo Running OtherFloors113.txt (1 run)
java -Xmx512M -cp out\production\the-one;lib/ECLA.jar;lib/DTNConsoleConnection.jar core.DTNSim %* -b 1   apocalypse_settings/OtherFloors113.txt

echo All simulations complete.
pause
