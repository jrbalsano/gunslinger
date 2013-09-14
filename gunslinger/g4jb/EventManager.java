package gunslinger.g4jb;

public class EventManager implements RoundListener {

	@Override
	public void onNewRound(GameHistory history) {
		// TODO: Tell the existing Events that a round has passed so that they 
		// can decay
		
		for (int shooter = 0; shooter < history.getNPlayers(); shooter++) {
			int shotAt = history.playerShotAt(shooter);
			if (history.isAlive(shooter) && history.isAlive(shotAt)) {
				// TODO: Generate an Event and add it to list
			}
		}
	}

	public void getBestShot() {
		// TODO: Grab the best event and return its target
	}
}
