package gunslinger.g4jb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class GameHistory {
	private int mId;
	private int mRoundsCount;
	private int mNPlayers;
	private ArrayList<int[]> mShotHistory;
	private ArrayList<boolean[]> mAliveHistory;
	private int mCurrentScore;
	private LinkedList<RoundListener> mRoundListeners;
	public enum PlayerType {NEUTRAL, FRIEND, THREAT, ENEMY, SELF};
	private PlayerType[] mPlayerTypes;
	
	/**
	 * Creates a new game history object for the current player
	 * @param currentPlayerId gets the current player's id.
	 */
	public GameHistory(int currentPlayerId, int nPlayers, int[] friends, int[] enemies) {
		mId = currentPlayerId;
		mRoundsCount = 0;
		mNPlayers = nPlayers;
		// Initialize player types list
		int friendsCount = 0;
		mPlayerTypes = new PlayerType[nPlayers];
		
		Arrays.fill(mPlayerTypes, PlayerType.NEUTRAL);
		mPlayerTypes[mId] = PlayerType.SELF;
		for (int player : friends) {
			mPlayerTypes[player] = PlayerType.FRIEND;
			friendsCount++;
		}
		for (int player : enemies) {
			mPlayerTypes[player] = PlayerType.ENEMY;
		}
		
		// Initialize score
		mCurrentScore = 1 + friendsCount;
		
		mRoundListeners = new LinkedList<RoundListener>();
	}
	
	public void addRound(int[] prevRound, boolean[] alive) {
		if (prevRound != null) {
			mRoundsCount++;
			mShotHistory.add(prevRound);
			mAliveHistory.add(alive);
			
			// Calculate score in this round
			mCurrentScore = alive[mId] ? 1 : 0;
			for (int player = 0; player < mNPlayers; player++) {
				if (alive[player] && mPlayerTypes[player] == PlayerType.FRIEND) {
					mCurrentScore++;
				}
				else if (!alive[player] && mPlayerTypes[player] == PlayerType.ENEMY) {
					mCurrentScore++;
				}
			}
			
			/* Check for new threats - those players who shot at us and whose
			 * enemy list we are likely on 
			 */
			for (int i : prevRound) {
				if (prevRound[i] == mId && mPlayerTypes[i] == PlayerType.NEUTRAL) {
					mPlayerTypes[i] = PlayerType.THREAT;
				}
			}
			
			// Notify round listeners that a new round is available
			notifyRoundListeners();
		}
	}
	
	public void addRoundListener(RoundListener rl) {
		mRoundListeners.add(rl);
	}
	
	public int getCurrentScore() {
		return mCurrentScore;
	}
	
	public int getNPlayers() {
		return mNPlayers;
	}
	
	/**
	 * Tells which player a given player shot in the previous round.
	 * @param player The shot to check
	 * @return The id of the shot player.
	 */
	public int playerShotAt(int player) {
		return playerShotAt(player, 0);
	}
	
	/**
	 * Tells which player a given player shot at in a given round.
	 * @param round The 1 indexed number of the round to fetch the history for.
	 * Use 0 to fetch the last round.
	 * @return -1 if no shot fired, id of player shot otherwise
	 */
	public int playerShotAt(int player, int round) {
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
	
	public boolean isAlive(int player) {
		return isAlive(player, 0);
	}
	
	public boolean isAlive(int player, int round) {
		if (round > mRoundsCount || player >= mNPlayers) {
			throw new IllegalArgumentException();
		}
		else if (round == 0) {
			return mAliveHistory.get(mRoundsCount - 1)[player];
		}
		else {
			return mAliveHistory.get(round)[player];
		}
	}
	
	public PlayerType getPlayerType(int player) {
		return mPlayerTypes[player];
	}
	
	private void notifyRoundListeners() {
		for (RoundListener rl : mRoundListeners) {
			rl.onNewRound(this);
		}
	}
}