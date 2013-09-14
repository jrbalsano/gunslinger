package gunslinger.g4jb;

import java.util.PriorityQueue;

public class EventManager implements RoundListener {
	private PriorityQueue<Event> mEvents; 
	
	public EventManager() {
		mEvents = new PriorityQueue<Event>();
	}
	
	@Override
	public void onNewRound(GameHistory history) {
		for (Event event : mEvents) {
			event.onRoundPassed(history);
		}
		
		for (int shooter = 0; shooter < history.getNPlayers(); shooter++) {
			int shotAt = history.playerShotAt(shooter);
			if (history.isAlive(shooter) && history.isAlive(shotAt)) {
				mEvents.add(new Event(history, shooter));
			}
		}
	}

	public int getBestShot() {
		return mEvents.poll().getTarget();
	}
}