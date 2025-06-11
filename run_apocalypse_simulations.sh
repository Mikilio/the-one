#!/bin/sh

echo "Starting simulations"

echo "Running originClassRoom0.txt (1 run)"
java -Xmx512M -cp "target:lib/ECLA.jar:lib/DTNConsoleConnection.jar" core.DTNSim "$@" -b 1 apocalypse_settings/originClassRoom0.txt &
wait

echo "Running FingerHallwayInfectionOrigin1.txt (1 run)"
java -Xmx512M -cp "target:lib/ECLA.jar:lib/DTNConsoleConnection.jar" core.DTNSim "$@" -b 1 apocalypse_settings/FingerHallwayInfectionOrigin1.txt &
wait

echo "Running ClassRoomSameFingerXX.txt (19 runs)"
i=2
while [ "$i" -le 20 ]; do
  java -Xmx512M -cp "target:lib/ECLA.jar:lib/DTNConsoleConnection.jar" core.DTNSim "$@" -b 1 apocalypse_settings/ClassRoomSameFinger${i}.txt &
  i=`expr "$i" + 1`
done

echo "Running BalconyNordEtage121.txt (1 run)"
java -Xmx512M -cp "target:lib/ECLA.jar:lib/DTNConsoleConnection.jar" core.DTNSim "$@" -b 1 apocalypse_settings/BalconyNordEtage121.txt &
wait

echo "Running FingerHallwayUninfectedXX.txt (5 runs)"
i=22
while [ "$i" -le 26 ]; do
  java -Xmx512M -cp "target:lib/ECLA.jar:lib/DTNConsoleConnection.jar" core.DTNSim "$@" -b 1 apocalypse_settings/FingerHallwayUninfected${i}.txt &
  i=`expr "$i" + 1`
done

echo "Running BridgeXX.txt (2 runs)"
i=27
while [ "$i" -le 28 ]; do
  java -Xmx512M -cp "target:lib/ECLA.jar:lib/DTNConsoleConnection.jar" core.DTNSim "$@" -b 1 apocalypse_settings/Bridge${i}.txt &
  i=`expr "$i" + 1`
done
wait

echo "Running classRoomSameFloorXX.txt (80 runs)"
i=29
while [ "$i" -le 108 ]; do
  java -Xmx512M -cp "target:lib/ECLA.jar:lib/DTNConsoleConnection.jar" core.DTNSim "$@" -b 1 apocalypse_settings/classRoomSameFloor${i}.txt &
  i=`expr "$i" + 1`
done

echo "Running StairsXX.txt (4 runs)"
i=109
while [ "$i" -le 112 ]; do
  java -Xmx512M -cp "target:lib/ECLA.jar:lib/DTNConsoleConnection.jar" core.DTNSim "$@" -b 1 apocalypse_settings/Stairs${i}.txt &
  i=`expr "$i" + 1`
done
wait

echo "Running Magistrale114.txt (1 run)"
./one.sh apocalypse_settings/Magistrale114.txt
