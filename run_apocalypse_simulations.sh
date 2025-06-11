#!/bin/sh

echo "Starting simulations"

echo "Running originClassRoom.txt (1 run)"
java -Xmx512M -cp target:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim "$@" -b 1 apocalypse_settings/originClassRoom.txt
sleep 2

echo "Running FingerHallwayInfectionOrigin.txt (1 run)"
java -Xmx512M -cp target:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim "$@" -b 1 apocalypse_settings/FingerHallwayInfectionOrigin.txt
sleep 2

echo "Running ClassRoomSameFinger.txt (19 runs)"
java -Xmx512M -cp target:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim "$@" -b 19 apocalypse_settings/ClassRoomSameFinger.txt

echo "Running BalconyNordEtage1.txt (1 run)"
java -Xmx512M -cp target:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim "$@" -b 1 apocalypse_settings/BalconyNordEtage1.txt
sleep 2

echo "Running Bridge.txt (2 runs)"
java -Xmx512M -cp target:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim "$@" -b 2 apocalypse_settings/Bridge.txt

echo "Running FingerHallwayUninfected.txt (5 runs)"
java -Xmx512M -cp target:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim "$@" -b 5 apocalypse_settings/FingerHallwayUninfected.txt
sleep 2

echo "Running classRoomSameFloor.txt (1 run)"
java -Xmx512M -cp target:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim "$@" -b 1 apocalypse_settings/classRoomSameFloor.txt

echo "Running Stairs.txt (4 runs)"
java -Xmx512M -cp target:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim "$@" -b 4 apocalypse_settings/Stairs.txt
sleep 2

echo "Running Magistrale.txt (1 run)"
java -Xmx512M -cp target:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim "$@" -b 1 apocalypse_settings/Magistrale.txt

echo "All simulations complete."
