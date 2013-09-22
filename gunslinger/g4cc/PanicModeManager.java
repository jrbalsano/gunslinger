package gunslinger.g4cc;

public class PanicModeManager implements RoundListener {
    private int mTarget;
    private boolean mIsPanicMode;
    

    public PanicModeManager(GameHistory history) {
        mTarget = -1;
        mIsPanicMode = false;
    }
	
    @Override
    public void onNewRound(GameHistory history) {
        mTarget = -1;
      
        //get SELF int value
        int self;
        for(int i=0; i<history.getNPlayers(); i++){
            if(history.getPlayerType(i) == GameHistory.PlayerType.SELF)
                self = i;
        }

        //threat retaliation failed (?) just threat case or help friend case
        if(history.isAlive(history.playerShotAt(self, 0)))
            mIsPanicMode = true;
       
        //2 threats alive
        int numThreats = 0;
        for(int i=0; i<history.getNPlayers(); i++){
            if(history.isAlive(i) && history.getPlayerType(i) == GameHistory.PlayerType.THREAT)
                numThreats++;
        }
        if(numTHreats == 2)
            mIsPanicMode = true;

        //enemies/friends ratio == .2 (?)
        double efRatio = (double)history.getFriendCount() / (double)history.getEnemyCount();
        if(efRatio <= .2)
            mIsPanicMode = true;
        
        //preserve self
        for(int i=0; i<history.getNplayers(); i++){
            if(history.playerShotAt(i) == self) 
                    mTarget = i;
        }
        
        //preserve friends
        if(history.getFriendCount() > 0){
            for(int i=0; i<history.getNplayers(); i++){
                if(history.getPlayerType(i) == GameHistory.PlayerType.NEUTRAL){
                    if(history.getPlayerType(history.playerShotAt(i)) 
                                                == GameHistory.PlayerType.FRIEND){
                        mTarget = i;
                    }
                }
            }
             for(int i=0; i<history.getNplayers(); i++){
                if(history.getPlayerType(i) == GameHistory.PlayerType.THREAT){
                    if(history.getPlayerType(history.playerShotAt(i)) 
                                                == GameHistory.PlayerType.FRIEND){
                        mTarget = i;
                    }
                }
            }
             for(int i=0; i<history.getNplayers(); i++){
                if(history.getPlayerType(i) == GameHistory.PlayerType.ENEMY){
                    if(history.getPlayerType(history.playerShotAt(i)) 
                                                == GameHistory.PlayerType.FRIEND){
                        mTarget = i;
                    }
                }
            }
        }

        //target priorites: enemies, preserve friends, preserve self
        ArrayList<int> Enemies;

        for(int i=0; i<history.getNplayers(); i++){
            if(history.getPlayerType(i) == GameHistory.PlayerType.ENEMY && history.isAlive(i))
                Enemies.add(i);
        }

        if(history.getRoundsCount()





       
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
                mWorthKillingNeutral)
                mTarget = target;
            mIsLateGame = true;
        }
        if (aliveNonFriend == 0) {
            mTarget = -1;
            mIsLateGame = true;
        }
	}

    public boolean isPanicMode() {
        return mIsPanicMode;
    }

	public int shoot() {
        return mTarget;
    }
}
