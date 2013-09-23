package gunslinger.g4;

import java.util.*;

public class PanicModeManager implements RoundListener {
    private int mTarget;
    private boolean mIsPanicMode;
    private LinkedList<Integer> mEnemies; //to shoot
    private LinkedList<Integer> enemies; //is alive

    public PanicModeManager(GameHistory history) {
        mTarget = -1;
        mIsPanicMode = false;
	mEnemies = new LinkedList<Integer>();
	enemies = new LinkedList<Integer>();
    }
	
    @Override
    public void onNewRound(GameHistory history) {
        mIsPanicMode = false;
        mTarget = -1;
      
        //get SELF int value
        int self = history.getMyId();

        //threat retaliation failed (?) just threat case or help friend case

        if(history.getRoundsCount() > 1 && history.playerShotAt(self, 0) >=0){
            if(history.isAlive(history.playerShotAt(self, 0)))
                mIsPanicMode = true;
        }
       
        //2 threats alive
        int numThreats = 0;
        for(int i=0; i<history.getNPlayers(); i++){
            if(history.isAlive(i) && history.getPlayerType(i) == GameHistory.PlayerType.THREAT)
                numThreats++;
        }
        if(numThreats >= 2)
            mIsPanicMode = true;

        //enemies/friends ratio == .2 (?)
        int eAlive = 0;
        int fAlive = 0;
        int nAlive = 0;
        double feRatio = 3.1415926;
        double enRatio = 3.1415926;

        for(int i=0; i<history.getNPlayers(); i++){
            if(history.isAlive(i) && history.getPlayerType(i) == GameHistory.PlayerType.ENEMY)
                eAlive++;
            if(history.isAlive(i) && history.getPlayerType(i) == GameHistory.PlayerType.FRIEND)
                fAlive++;
            if(history.isAlive(i))
                nAlive++;
        }

        enRatio = (double)eAlive / (double)nAlive;

        if(eAlive != 0)
            feRatio = (double)fAlive / (double)eAlive;

        if(enRatio >= 0.5)
            mIsPanicMode = true;
        
        //preserve self
        if(history.getRoundsCount() > 1){
            for(int i=0; i<history.getNPlayers(); i++){
                if(history.playerShotAt(i) == self) 
                        mTarget = i;
            }
        }
            
        //preserve friends
        if(history.getRoundsCount() > 1){
            if(history.getFriendCount() > 0){
                for(int i=0; i<history.getNPlayers(); i++){
                    if(history.getPlayerType(i) == GameHistory.PlayerType.NEUTRAL
			&& history.playerShotAt(i) >=0){
                        if(history.getPlayerType(history.playerShotAt(i)) 
                                                    == GameHistory.PlayerType.FRIEND){
                            mTarget = i;
                        }
                    }
                }
                for(int i=0; i<history.getNPlayers(); i++){
                    if(history.getPlayerType(i) == GameHistory.PlayerType.THREAT
			&& history.playerShotAt(i) >=0){
                        if(history.getPlayerType(history.playerShotAt(i)) 
                                                    == GameHistory.PlayerType.FRIEND){
                            mTarget = i;
                        }
                    }
                }
                for(int i=0; i<history.getNPlayers(); i++){
                    if(history.getPlayerType(i) == GameHistory.PlayerType.ENEMY
			&& history.playerShotAt(i) >=0){
                        if(history.getPlayerType(history.playerShotAt(i)) 
                                                    == GameHistory.PlayerType.FRIEND){
                            mTarget = i;
                        }
                    }
                }
            }
        }

        //target priorites: enemies, preserve friends, preserve self

        //check if all element of mEnemies are still alive
        boolean reset = false;
        if(history.getRoundsCount() < 2)
            reset = true;
        else{ 
            for(int i=0; i<mEnemies.size(); i++){
                    if(!history.isAlive(mEnemies.get(i)))
                        reset = true;
            }
        }
        
        //if an enemy dies, reset the list
        if(reset){
                mEnemies.clear();
                enemies.clear();

            for(int i=0; i<history.getNPlayers(); i++){
                 if(history.getPlayerType(i) == GameHistory.PlayerType.ENEMY && history.isAlive(i))
                    enemies.add(i);
            }

            int size = enemies.size();
            if(enemies.size()%2 == 1) //if size = odd
            size -= 1;

            for(int i=0; i<size; i+=2){
                mEnemies.add(enemies.get(i));
                mEnemies.add(enemies.get(i+1));
                mEnemies.add(enemies.get(i));
                mEnemies.add(enemies.get(i+1));
            }

            if(enemies.size()%2 == 1) //if size is odd, add last enemy to the end
                mEnemies.add(enemies.get(size));
        }
        if(!mEnemies.isEmpty())
            mTarget = mEnemies.pop();

    }

    public boolean isPanicMode() {
        return mIsPanicMode;
    }

    public int shoot() {
        return mTarget;
    }
}
