#!/bin/bash
nplayers=9
make
for f in $(seq 0 $nplayers)
do
    emax=$(($nplayers-$f))
    for e in $(seq 0 $emax)
    do
	echo "f=$f, e=$e"
	java gunslinger.sim.Gunslinger gunslinger/players.list $e $f false true false false 10
    done
done