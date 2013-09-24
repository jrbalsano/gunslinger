main: gunslinger/sim/Gunslinger.class gunslinger/dumb/Player.class gunslinger/g4/Player.class

gunslinger/sim/Gunslinger.class: gunslinger/sim/Gunslinger.java gunslinger/sim/Player.java
	javac gunslinger/sim/Gunslinger.java gunslinger/sim/Player.java

gunslinger/dumb/Player.class: gunslinger/dumb/Player.java gunslinger/sim/Player.java
	javac gunslinger/dumb/Player.java gunslinger/sim/Player.java

gunslinger/g4/Player.class: gunslinger/g4/*.java gunslinger/sim/Player.java
	javac gunslinger/g4/Player.java gunslinger/g4/*.java

.PHONY:
tour: main
	java gunslinger.sim.Gunslinger gunslinger/players.list 2 2 false true false false 100

.PHONY:
round: main
	java gunslinger.sim.Gunslinger gunslinger/players.list 2 2 true true
