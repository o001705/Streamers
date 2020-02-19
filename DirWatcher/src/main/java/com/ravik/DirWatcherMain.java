package com.ravik;

public class DirWatcherMain{
	public static void main(String[] args){
		DirWatcher watcher = new DirWatcher("d:\\test");
		watcher.watch();
	}
}