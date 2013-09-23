package gunslinger.g4cc;

public interface RoundListener {
	/**
	 * Called by a GameHistory object when a new round has been created and saved.
	 */
	public void onNewRound(GameHistory history);
}
