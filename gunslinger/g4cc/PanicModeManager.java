package gunslinger.g4cc;

import java.util.*;

public class PanicModeManager implements RoundListener {
    private int mTarget;
    private boolean mIsPanicMode;
    private LinkedList<Integer> mEnemies; //to shoot
    private LinkedList<Integer> enemies; //is alive

    public PanicModeManager(GameHistory history) {
        mTarget = -1;
        mIsPanicMode = false;
    }
	
    @Override
    public void onNewRound(GameHistory history) {
        mTarget = -1;
      
        //get SELF int value
        int self = 0;
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
        if(numThreats == 2)
            mIsPanicMode = true;

        //enemies/friends ratio == .2 (?)
        double efRatio = (double)history.getFriendCount() / (double)history.getEnemyCount();
        if(efRatio <= .2)
            mIsPanicMode = true;
        
        //preserve self
        for(int i=0; i<history.getNPlayers(); i++){
            if(history.playerShotAt(i) == self) 
                    mTarget = i;
        }
        
        //preserve friends
        if(history.getFriendCount() > 0){
            for(int i=0; i<history.getNPlayers(); i++){
                if(history.getPlayerType(i) == GameHistory.PlayerType.NEUTRAL){
                    if(history.getPlayerType(history.playerShotAt(i)) 
                                                == GameHistory.PlayerType.FRIEND){
                        mTarget = i;
                    }
                }
            }
             for(int i=0; i<history.getNPlayers(); i++){
                if(history.getPlayerType(i) == GameHistory.PlayerType.THREAT){
                    if(history.getPlayerType(history.playerShotAt(i)) 
                                                == GameHistory.PlayerType.FRIEND){
                        mTarget = i;
                    }
                }
            }
             for(int i=0; i<history.getNPlayers(); i++){
                if(history.getPlayerType(i) == GameHistory.PlayerType.ENEMY){
                    if(history.getPlayerType(history.playerShotAt(i)) 
                                                == GameHistory.PlayerType.FRIEND){
                        mTarget = i;
                    }
                }
            }
        }

        //target priorites: enemies, preserve friends, preserve self

        //check if all element of mEnemies are still alive
        boolean reset = false;
        if(history.getRoundsCount() < 2)
            reset = true;
        
        for(int i=0; i<mEnemies.size(); i++){
                if(!history.isAlive(mEnemies.get(i)))
                    reset = true;
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
