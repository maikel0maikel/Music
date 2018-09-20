// IPlayService.aidl
package com.sinohb.music.sdk;
import com.sinohb.music.sdk.entities.Song;
import com.sinohb.music.sdk.IPlayCallbacker;
interface IPlayService {
  void play(in Song song);

  void playNext();

  void playPre();

  boolean pause();

  boolean resume();

  void setMode(int mode);

  int getMode();

  boolean isPlaying();

  void seekTo(int position);

  int getCurrentPos();

  Song getCurrentPlay();

  long getCurrentPlayId();

  void addSongToPlay(in Song song);

  void pushPlayQueue(in List<Song> songs);

  void randanPlay(in List<Song> songs);

  void pushSongsToPlay(in List<Song> songs,int pos);

  int getAudioSessionId();

  List<Song> getPlaySongs();

   void clearPlayQueue();
//  void removeSong(in Song song,int type);

  void removeSong(in Song song);

//  void removeSongs(in List<Song> songs,int type);

  void removeSongs(in List<Song> song);

  void registPlayCallbacker(IPlayCallbacker callbacker);

  void unRegistPlayCallbacker(IPlayCallbacker callbacker);

  void linkClientDeath(IBinder client);

}
