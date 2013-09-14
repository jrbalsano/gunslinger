package gunslinger.g4jb;

import gunslinger.g4jb.GameHistory.PlayerType;

public class Event implements Comparable<Event> {
	// The threat level that this event poses. Higher numbers denote greater
	// threat.
	private double mDangerLevel;
	private double mDangerMultiplier;
	private int mShooterId;
	private int mShotId;
	private int mTarget;
	private PlayerType mShooterType;
	private PlayerType mShotType;
	
	//Weights
//	private static int FRIEND_SHOOTS_FRIEND;
//	private static int FRIEND_SHOOTS_NEUTRAL;
	private static int FRIEND_SHOOTS_THREAT = 2;
	private static int FRIEND_SHOOTS_ENEMY = 1;
//	private static int FRIEND_SHOOTS_ME;
	private static int NEUTRAL_SHOOTS_FRIEND = 4;
	private static int NEUTRAL_SHOOTS_NEUTRAL = 3;
	private static int NEUTRAL_SHOOTS_THREAT = 2;
	private static int NEUTRAL_SHOOTS_ENEMY = 1;
	private static int THREAT_SHOOTS_ME = 7;
//	private static int THREAT_SHOOTS_FRIEND;
//	private static int THREAT_SHOOTS_NEUTRAL;
//	private static int THREAT_SHOOTS_THREAT;
//	private static int THREAT_SHOOTS_ENEMY;
	private static int ENEMY_SHOOTS_FRIEND = 6;
	private static int ENEMY_SHOOTS_NEUTRAL = 5;
	private static int ENEMY_SHOOTS_THREAT = 5;
	private static int ENEMY_SHOOTS_ENEMY = 5;
	private static int ENEMY_SHOOTS_ME = 8;
	
	// Exponential backoff rate
	private static double MULTIPLIER = .8;
	
	
	public Event(GameHistory history, int shooter) {
		mShooterId = shooter;
		mShotId = history.playerShotAt(mShooterId);
		mShooterType = history.getPlayerType(mShooterId); 
		mShotType = history.getPlayerType(mShotId);
	
		resetDangerLevel();
		mDangerMultiplier = 1;
		mTarget = -1;
	}
	
	public void onRoundPassed(GameHistory history) {
		boolean dangerLevelChanged = false;
		if (mShotType == PlayerType.NEUTRAL) {
			mShotType = history.getPlayerType(mShotId);
			dangerLevelChanged = true;
		}
		if (mShooterType == PlayerType.NEUTRAL) {
			mShooterType = history.getPlayerType(mShotId);
			dangerLevelChanged = true;
		}
		if (dangerLevelChanged) {
			resetDangerLevel();
		}
		adjustBackoffMultiplier();
	}
	
	public int compareTo(Event other) {
		return (int) Math.signum(getDangerScore() - other.getDangerScore()); 
	}
	
	public double getDangerScore() {
		return mDangerLevel * mDangerMultiplier;
	}
	
	public int getTarget() {
		return mTarget;
	}
	
	private void adjustBackoffMultiplier() {
		mDangerMultiplier *= MULTIPLIER;
	}
	
	private void resetDangerLevel() {
		switch (mShooterType) {
		case FRIEND:
			switch (mShotType) {
			case FRIEND:
			case NEUTRAL:
				break;
			case THREAT:
				mDangerLevel = FRIEND_SHOOTS_THREAT;
				mTarget = mShotId;
				break;
			case ENEMY:
				mDangerLevel = FRIEND_SHOOTS_ENEMY;
				mTarget = mShotId;
				break;
			case SELF:
				break;
			}
			break;
		case THREAT:
		case NEUTRAL:
			switch (mShotType) {
			case FRIEND:
				mDangerLevel = NEUTRAL_SHOOTS_FRIEND;
				mTarget = mShooterId;
				break;
			case NEUTRAL:
				mDangerLevel = NEUTRAL_SHOOTS_NEUTRAL;
				mTarget = mShooterId;
				break;
			case THREAT:
				mDangerLevel = NEUTRAL_SHOOTS_THREAT;
				mTarget = mShotId;
				break;
			case ENEMY:
				mDangerLevel = NEUTRAL_SHOOTS_ENEMY;
				mTarget = mShotId;
				break;
			case SELF:
				mDangerLevel = THREAT_SHOOTS_ME;
				mTarget = mShooterId;
				break;
			}
			break;
		case ENEMY:
			switch (mShotType) {
			case FRIEND:
				mDangerLevel = ENEMY_SHOOTS_FRIEND;
				mTarget = mShooterId;
				break;
			case NEUTRAL:
				mDangerLevel = ENEMY_SHOOTS_NEUTRAL;
				mTarget = mShooterId;
				break;
			case THREAT:
				mDangerLevel = ENEMY_SHOOTS_THREAT;
				mTarget = mShooterId;
				break;
			case ENEMY:
				mDangerLevel = ENEMY_SHOOTS_ENEMY;
				mTarget = mShooterId;
				break;
			case SELF:
				mDangerLevel = ENEMY_SHOOTS_ME;
				mTarget = mShooterId;
				break;
			}
			break;
		default:
			break;
		}
	}
}
