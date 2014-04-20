package net.cqs.engine.scores;

public interface Scoreable<T extends Scoreable<T>>
{

Score<T> getScore();
Score<T> removeScore();
Score<T> newScore(ScoreManager<T> sm);

}
