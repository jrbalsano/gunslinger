package gunslinger.g4jl;

public class LateGameManager implements RoundListener {
    private int mTarget;
    private boolean mIsLateGame;
	
	public LateGameManager() {
        mTarget = -1;
        mIsLateGame = false;
	}
	
	@Override
	public void onNewRound(GameHistory history) {
        int target = -1;
        int aliveNonFriend = 0;
        for (int i = 0; i < history.getNPlayers(); i++) {
            if (history.isAlive(i) && history.getPlayerType(i) != GameHistory.PlayerType.SELF 
                && history.getPlayerType(i) != GameHistory.PlayerType.FRIEND) {
                target = i;
                aliveNonFriend++;
            }
        }
        if (aliveNonFriend == 1) {
            if (history.getPlayerType(target) == GameHistory.PlayerType.ENEMY)
                mTarget = target;
            if ((history.getPlayerType(target) == GameHistory.PlayerType.NEUTRAL ||
                 history.getPlayerType(target) == GameHistory.PlayerType.THREAT) &&
                history.getWorthKillingNeutral())
                mTarget = target;
            mIsLateGame = true;
        }
        if (aliveNonFriend == 0) {
            mTarget = -1;
            mIsLateGame = true;
        }
	}

    public boolean isLateGame() {
        return mIsLateGame;
    }

	public int shoot() {
        return mTarget;
    }
}
