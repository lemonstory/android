package com.xiaoningmeng.player;

public class PlayErrorMonitor {

	private long perPlayCompletionTime;
	private int totalError;
	private PlayerManager playerManager;
	
	public PlayErrorMonitor(PlayerManager playerManager){
		this.playerManager = playerManager;
	}
	

	public void clearError() {
		this.totalError = 0;
	}
	
	public void incrementTotalError(){
		this.totalError++;
	}
	
	public boolean isUpperPlayErrorCountLimit(){
		return totalError > playerManager.getPlayList().size() - 1;
	}
	
	//播放时间是否太短(如果在0.15秒内故事调用OnCompletion  算这个故事播放错误)
	public boolean isPlayCompleteTooFast(){
		boolean flag = false;
		if(perPlayCompletionTime != 0){
			long currentPlayCompletionTime =  System.currentTimeMillis();
			if(currentPlayCompletionTime -perPlayCompletionTime < 200){
				flag =  true;
			}
		}
		perPlayCompletionTime = System.currentTimeMillis();
		return flag;
	}
	
}
