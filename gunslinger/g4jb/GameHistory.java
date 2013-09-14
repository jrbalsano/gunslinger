package gunslinger.g4jb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GameHistory {
	private int mId;
	private int mRoundsCount;
	private int mNPlayers;
	private ArrayList<int[]> mShotHistory;
	private ArrayList<boolean[]> mAliveHistory;
	private Set<Integer> mFriends;
	private Set<Integer> mEnemies;
	private Set<Integer> mThreats; // Neutral players who have shot at us
	private int mCurrentScore;
	
	/**
	 * Creates a new game history object for the current player
	 * @param currentPlayerId gets the current player's id.
	 */
	public GameHistory(int currentPlayerId, int nPlayers, int[] friends, int[] enemies) {
		mId = currentPlayerId;
		mRoundsCount = 0;
		mNPlayers = nPlayers;
		// Initialize friends and enemies list as well as threats
		mFriends = new HashSet<Integer>();
		mEnemies = new HashSet<Integer>();
		mThreats = new HashSet<Integer>();
		for (Integer i : friends) {
			mFriends.add(i);
		}
		for (Integer i : enemies) {
			mEnemies.add(i);
		}
		
		// Initialize score
		mCurrentScore = 1 + mFriends.size();
	}
	
	public void addRound(int[] prevRound, boolean[] alive) {
		if (prevRound != null) {
			mRoundsCount++;
			mShotHistory.add(prevRound);
			mAliveHistory.add(alive);
			
			// Calculate score in this round
			mCurrentScore = alive[mId] ? 1 : 0;
			for (Integer i : mFriends) {
				if (alive[i]) {
					mCurrentScore++;
				}
			}
			for (Integer i : mEnemies) {
				if (!alive[i]) {
					mCurrentScore++;
				}
			}
			
			/* Check for new threats - those players who shot at us and whose
			 * enemy list we are likely on 
			 */
			for (int i : prevRound) {
				if (prevRound[i] == mId) {
					mThreats.add(i);
				}
			}
		}
	}
	
	public int getCurrentScore() {
		return mCurrentScore;
	}
	
	/**
	 * Tells which player a given player shot at in a given round.
	 * @param round The 1 indexed number of the round to fetch the history for.
	 * Use 0 to fetch the last round.
	 * @return -1 if no shot fired, id of player shot otherwise
	 */
	public int playerShotAt(int round, int player) {
		if (round > mRoundsCount || player >= mNPlayers) {
			throw new IllegalArgumentException();
		}
		else if (round == 0) {
			return mShotHistory.get(mRoundsCount - 1)[player];
		}
		else {
			return mShotHistory.get(round)[player];
		}
	}
}
