/**
 * 
 */
package com.ravik;

import java.io.File;
//import java.io.FileInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
/**
 * @author Ravi Kappagantu
 *
 */
class DirWatcher {
	private Path _dirPath;
//	private WatchService _dirWatcher;
	
	public DirWatcher() {}
	public DirWatcher (String path) {_dirPath = FileSystems.getDefault().getPath(path);} 
	public void setDirPath(String path) {_dirPath = FileSystems.getDefault().getPath(path);}
	public String getDirPath() {return _dirPath.toString();}
	public void watch() {setupWatcher();}
	
	private void setupWatcher(){
        try {
            while (true) {
                WatchService watchService = _dirPath.getFileSystem().newWatchService();
                _dirPath.register(watchService, 
                		StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY, 
                        StandardWatchEventKinds.ENTRY_DELETE);
                WatchKey watchKey = watchService.take();
                for (final WatchEvent<?> event : watchKey.pollEvents()) {
                    takeActionOnChangeEvent(event);
                }
                watchService = null;
            }

        } catch (InterruptedException interruptedException) {
            System.out.println("Watcher is interrupted:" + interruptedException);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
	}

	private void takeActionOnChangeEvent(WatchEvent<?> event) {

        Kind<?> kind = event.kind();
        Path entry = (Path) event.context();
        File file = _dirPath.resolve(entry).toFile();

        if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
            if (file.exists()) {
                System.out.println("File " + file.getPath() + " is created");
            }
        }
        else if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
            if (file.exists()) {
                System.out.println("File " + file.getPath() + " is modified");
            }
        }
        else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
            System.out.println("File " + entry.toString() + " is deleted");
        }
        else {
            System.out.println("File " + entry.toString() + " has some unknown event");
        }
    }
}

public class DirWatcherMain{
	public static void main(String[] args){
		DirWatcher watcher = new DirWatcher("d:\\test");
		watcher.watch();
	}
}
