main: gunslinger/sim/Gunslinger.class gunslinger/dumb/Player.class gunslinger/g4jb/Player.class gunslinger/G4PlayerV1/Player.class gunslinger/g1/Player.class gunslinger/g2/Player.class gunslinger/g3/Player.class gunslinger/g5/Player.class gunslinger/g6/Player.class gunslinger/g7/Player.class gunslinger/g8/Player.class gunslinger/g9/Player.class

gunslinger/sim/Gunslinger.class: gunslinger/sim/Gunslinger.java gunslinger/sim/Player.java
	javac gunslinger/sim/Gunslinger.java gunslinger/sim/Player.java

gunslinger/dumb/Player.class: gunslinger/dumb/Player.java gunslinger/sim/Player.java
	javac gunslinger/dumb/Player.java gunslinger/sim/Player.java

gunslinger/G4PlayerV1/Player.class: gunslinger/G4PlayerV1/Player.java gunslinger/sim/Player.java
	javac gunslinger/G4PlayerV1/Player.java gunslinger/sim/Player.java

gunslinger/g4jb/Player.class: gunslinger/g4jb/Player.java gunslinger/sim/Player.java
	javac gunslinger/g4jb/Player.java gunslinger/sim/Player.java

gunslinger/g1/Player.class: gunslinger/g1/Player.java gunslinger/g1/AiPlayer.java
	javac gunslinger/g1/Player.java gunslinger/g1/AiPlayer.java gunslinger/sim/Player.java

gunslinger/g2/Player.class: gunslinger/g2/Player.java
	javac gunslinger/g2/Player.java gunslinger/sim/Player.java

gunslinger/g3/Player.class: gunslinger/g3/Player.java
	javac gunslinger/g3/Player.java gunslinger/sim/Player.java

gunslinger/g5/Player.class: gunslinger/g5/Player.java
	javac gunslinger/g5/Player.java gunslinger/sim/Player.java

gunslinger/g6/Player.class: gunslinger/g6/Player.java
	javac gunslinger/g6/Player.java gunslinger/sim/Player.java

gunslinger/g7/Player.class: gunslinger/g7/Player.java
	javac gunslinger/g7/Player.java gunslinger/sim/Player.java

gunslinger/g8/Player.class: gunslinger/g8/Player.java
	javac gunslinger/g8/Player.java gunslinger/sim/Player.java

gunslinger/g9/Player.class: gunslinger/g9/Player.java
	javac gunslinger/g9/Player.java gunslinger/sim/Player.java

.PHONY:
tour: main
	java gunslinger.sim.Gunslinger gunslinger/players.list 2 2 false false false false 100
