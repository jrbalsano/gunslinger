package gunslinger.g4;

import gunslinger.g4.GameHistory.PlayerType;

public class Event implements Comparable<Event> {
	// The threat level that this event poses. Higher numbers denote greater
	// threat.
    private GameHistory mHistory;
	private double mDangerLevel;
	private double mDangerMultiplier;
	private int mShooterId;
	private int mShotId;
	private int mTarget;
	private PlayerType mShooterType;
	private PlayerType mShotType;
	
	/*
	Sample weights array
	Use -1 for events that don't result in retaliation
	//Weights                    F  	N  		T  		E		M
	private int[][] weights = {{-1, 	-1, 	-1, 	-1, 	-1},
	                           {17, 	6, 		-1, 	-1, 	-1},
	                           {19, 	14, 	14, 	14, 	16},
	                           {20, 	15, 	15, 	15, 	18},
	                           {-1,		-1,		-1,		-1,		-1}}};
	 */
	
    private boolean mToDelete;

	//Weights
	private int[][] weights = {{0, 		0, 		0, 		0,	 	0},
	                           {17, 	6, 		-1, 	-1, 	-1},
	                           {19, 	14, 	14, 	14, 	16},
	                           {20, 	15, 	15, 	15, 	18},
	                           {0,		0,		0,		0,		0}};
	
	// Exponential backoff rate
	private static double MULTIPLIER = .55;
	
	
	public Event(GameHistory history, int shooter) {
		mShooterId = shooter;
		mShotId = history.playerShotAt(mShooterId);
		mShooterType = history.getPlayerType(mShooterId); 
		mShotType = history.getPlayerType(mShotId);
		mHistory = history;
		System.out.println(mShooterId + " shot at " + mShotId);
        mToDelete = false;
	
		mDangerMultiplier = 1;
		resetDangerLevel();
		if (mDangerLevel == -1) {
			mToDelete = true;
		}
        adjustForTarget();
	}
	
	public void onRoundPassed(GameHistory history) {
		if (!history.isAlive(mShotId) || !history.isAlive(mShooterId)) {
			mToDelete = true;
			return;
		}
        resetDangerLevel();
        adjustForTarget();
		adjustBackoffMultiplier();
	}
	
	public int compareTo(Event other) {
		return (int) Math.signum(other.getDangerScore() - getDangerScore()); 
	}
	
	public double getDangerScore() {
		return mDangerLevel * mDangerMultiplier;
	}
    
    public boolean getToDelete() {
        return mToDelete;
    }
	
	public int getTarget() {
		return mTarget;
	}
	
	private void adjustBackoffMultiplier() {
		mDangerMultiplier *= MULTIPLIER;
	}
	
	private void resetDangerLevel() {
		mDangerLevel = weights[mShooterType.ordinal()][mShotType.ordinal()];
		mTarget = mShooterId;
	}

    private void adjustForTarget() {
        mDangerLevel += mHistory.getRetaliateRate(mShotId);
    }
}
