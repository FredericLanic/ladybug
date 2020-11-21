package com.kycox.ladybug.contract;

import com.kycox.ladybug.body.ghost.GhostsGroup;
import com.kycox.ladybug.body.ladybug.Ladybug;
import com.kycox.ladybug.constant.ladybug.KinematicLadybugDeath;
import com.kycox.ladybug.level.ScreenData;
import com.kycox.ladybug.model.GameStatus;
import com.kycox.ladybug.score.GameScore;
import com.kycox.ladybug.score.GroupIncrementScores;

public interface IGameModelForView {
	public GameScore getGameScore();

	public GameStatus getGameStatus();

	public GhostsGroup getGroupGhosts();

	public GroupIncrementScores getGroupIncrementScores();

	public KinematicLadybugDeath getKinematicLadybugDeath();

	public Ladybug getLadybug();

	public int getNbrPlayers();

	public ScreenData getScreenData();

	public boolean isBeginNewLevel();

	// FIXME : hmmm, c'est pas interdit ?
	public void setBeginNewLevel(boolean beginNewLevel);
}
