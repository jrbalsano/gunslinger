program: simulator dumb G4Player

simulator: gunslinger/sim/Gunslinger.java gunslinger/sim/Player.java
	javac gunslinger/sim/Gunslinger.java gunslinger/sim/Player.java

dumb: gunslinger/dumb/Player.java gunslinger/sim/Player.java
	javac gunslinger/dumb/Player.java gunslinger/sim/Player.java

G4Player: gunslinger/G4PlayerV1/Player.java gunslinger/sim/Player.java
	javac gunslinger/G4PlayerV1/Player.java gunslinger/sim/Player.java

