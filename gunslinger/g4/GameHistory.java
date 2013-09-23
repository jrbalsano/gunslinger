package gunslinger.g4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class GameHistory {
	private int mId;
	private int mRoundsCount;
	private int mNPlayers;
	private int mFriendCount; // Number of friends total
	private int mEnemyCount; // Number of enemies total
	private ArrayList<int[]> mShotHistory;
	private ArrayList<boolean[]> mAliveHistory;
	private int mCurrentScore;
	private LinkedList<RoundListener> mRoundListeners;
	public enum PlayerType {NEUTRAL, FRIEND, THREAT, ENEMY, SELF};
	private PlayerType[] mPlayerTypes;
    private int[] mNRetaliate;
    private int[] mMaxRetaliate;
    private boolean[] mInferior;

	/**
	 * Creates a new game history object for the current player
	 * @param currentPlayerId gets the current player's id.
	 */
	public GameHistory(int currentPlayerId, int nPlayers, int[] friends, int[] enemies) {
		mId = currentPlayerId;
		mShotHistory = new ArrayList<int[]>();
		mAliveHistory = new ArrayList<boolean[]>();
		mRoundsCount = 0;
		mNPlayers = nPlayers;
		// Initialize player types list
		mFriendCount = 0;
		mEnemyCount = 0;
		mPlayerTypes = new PlayerType[nPlayers];

		Arrays.fill(mPlayerTypes, PlayerType.NEUTRAL);
		mPlayerTypes[mId] = PlayerType.SELF;
		for (int player : friends) {
			mPlayerTypes[player] = PlayerType.FRIEND;
			mFriendCount++;
		}
		for (int player : enemies) {
			mPlayerTypes[player] = PlayerType.ENEMY;
			mEnemyCount++;
		}
		// Initialize score
		mCurrentScore = 1 + mFriendCount;

		mNRetaliate = new int[nPlayers];
        mMaxRetaliate = new int[nPlayers];
        mInferior = new boolean[nPlayers];
        for (int player = 0; player < mNPlayers; player++) {
            mNRetaliate[player] = 0;
            mMaxRetaliate[player] = 0;
            mInferior[player] = false;
		}

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
			for (int i = 0; i < mNPlayers; i++) {
				if (prevRound[i] == mId && mPlayerTypes[i] == PlayerType.NEUTRAL) {
					mPlayerTypes[i] = PlayerType.THREAT;
				}
			}
            
            for (int player = 0; player < mNPlayers; player++) {
                int target = playerShotAt(player);
                System.out.println(player + " alive? " + alive[player]);
                System.out.println(player + " shot " + target);
                if (target >= 0) {
                    boolean inferior = true;
                    // mark people that shot someone, who did not shoot any valid target
                    // in other word, the player that has no reason to be retaliated
                    for (int round = 1; round < mRoundsCount; round++) {
                        System.out.println("on round " + round + " " + target + " shot " + playerShotAt(target, round));
                        if ((playerShotAt(target, round) >= 0) &&
                            isAlive(playerShotAt(target, round), 0))
                            inferior = false;
                    } 
                    // If I declare him inferior, he will forever be...
                    if (inferior)
                        System.out.println("Inferior move by player " + player);
                    mInferior[player] |= inferior;
                 }
            }
            for (int player = 0; player < mNPlayers; player++) {
                int target = playerShotAt(player, mRoundsCount - 2);
                if (target >= 0) {
                    mMaxRetaliate[target]++;
                    if (playerShotAt(target) == player)
                        mNRetaliate[target]++;
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
        if (round < 0)
            return -1;
		if (round > mRoundsCount || player >= mNPlayers) {
			throw new IllegalArgumentException();
		}
		else if (round == 0) {
			return mShotHistory.get(mRoundsCount - 2)[player];
		}
		else {
			return mShotHistory.get(round - 1)[player];
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
			if (mAliveHistory.size() > 0) {
				return mAliveHistory.get(mAliveHistory.size() - 1)[player];
			}
			else {
				return true;
			}
		}
		else {
			return mAliveHistory.get(round)[player];
		}
	}
	
	public PlayerType getPlayerType(int player) {
		return mPlayerTypes[player];
	}
	
	public int getFriendCount() {
		return mFriendCount;
	}
	
	public int getEnemyCount() {
		return mEnemyCount;
	}

    public boolean getInferior(int player) {
        return mInferior[player];
    }
    
    public double getRetaliateRate(int player) {
        if (mMaxRetaliate[player] == 0) {
            return 1;
        }
        return (double)mNRetaliate[player]/mMaxRetaliate[player];
    }
	
	private void notifyRoundListeners() {
		for (RoundListener rl : mRoundListeners) {
			rl.onNewRound(this);
		}
	}
}
