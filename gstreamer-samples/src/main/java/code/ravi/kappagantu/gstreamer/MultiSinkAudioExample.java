/**
 * 
 */
package code.ravi.kappagantu.gstreamer;

import java.io.IOException;

import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.ElementFactory;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;

/**
 * @author Ravi Kappagantu
 * This program records audio from microphon e or default recording device
 * splits the audio stream into two streams and saves them as OGG and WAV files
 * in a specified location. The purpose of this program is to demonstrate   
 * forking capability of gstreamer using "tee" command.
 */
 
public class MultiSinkAudioExample {
 
	String oggPath;
	String wavPath;
	Pipeline audioPipe;
	MultiSinkAudioExample(){
		Gst.init();
		audioPipe = new Pipeline("AudioPileline");
	}
	
	public void setOggPath(String pOggPath){
		oggPath = pOggPath;
	}
	public void setWavPath(String pWavPath){
		wavPath = pWavPath;
	}
	String getOggPath(){
		return oggPath;
	}
	String getWavPath(){
		return wavPath;
	}
	Pipeline getAudioPipe(){
		return audioPipe;
	}
    /**
     * Captures the sound and record into a WAV file and an OGG file
     */
    void start() {
    	Element audioSource = ElementFactory.make("autoaudiosrc", "audioSource");
    	Element fileSinkOgg = ElementFactory.make("filesink", "OggFile");
    	fileSinkOgg.set("location", getOggPath());
    	Element fileSinkWav = ElementFactory.make("filesink", "WavFile"); 
    	fileSinkWav.set("location", getWavPath());
    	Element audioConverter1 =  ElementFactory.make("audioconvert", "audioConverter1");
    	Element audioConverter2 =  ElementFactory.make("audioconvert", "audioConverter2");
    	Element audioFork =  ElementFactory.make("tee", "forkAudio");
    	Element audioFork1 =  ElementFactory.make("queue", "audioFork1");
    	Element audioFork2 =  ElementFactory.make("queue", "audioFork2");
    	Element vorbisEncoder = ElementFactory.make("vorbisenc", "vorbisEncoder");
    	Element waveEncoder = ElementFactory.make("wavenc", "waveEncoder");
    	Element oggMultiplexer = ElementFactory.make("oggmux", "oggMux");
    	
    	audioPipe.addMany(audioSource,
    					  audioFork,
    					  audioFork1,
    					  audioConverter1,
    					  vorbisEncoder,
    					  oggMultiplexer,
    					  fileSinkOgg,
    					  audioFork2,
    					  audioConverter2,
    					  waveEncoder,
    					  fileSinkWav
    					  );
    	Pipeline.linkMany(audioSource,
    					  audioFork);
    	/* Here is where the fork happens in the pipeline */
    	Pipeline.linkMany(audioFork, audioFork1);
    	Pipeline.linkMany(audioFork, audioFork2);

    	/* Link elements in Path 1 */
    	Pipeline.linkMany(audioFork1,audioConverter1,vorbisEncoder,
    			oggMultiplexer,fileSinkOgg);

    	/* Link elements in Path 2 */
    	Pipeline.linkMany(audioFork2,audioConverter2,waveEncoder,fileSinkWav);

    	audioPipe.play();
/**    	
    	String pipeSpec = "autoaudiosrc ! "
        		+ "tee name=t ! queue ! audioconvert ! vorbisenc !oggmux ! filesink name=dst1 "
                + "t. ! queue ! audioconvert! wavenc ! filesink name=dst2";
    
        Pipeline pipe = (Pipeline) Gst.parseLaunch(pipeSpec);

        pipe.getElementByName("dst1").set("location", getOggPath());
        pipe.getElementByName("dst2").set("location", getWavPath());
        pipe.play();
**/        
    }
 
    /**
     * Entry to run the program
     */
    public static void main(String[] args) {
        
        if (args.length < 2) {
        	System.out.println("usage: MultiSinkAudioExample OggDestinationPath WAVDestinationPath");
        } else {
            final MultiSinkAudioExample recorder = new MultiSinkAudioExample();
            recorder.setOggPath(args[0]);
            recorder.setWavPath(args[1]);
	        // creates a new thread that waits for a keyboard interrupt before stopping
	        Thread stopper = new Thread(new Runnable() {
	            public void run() {
	            	try {
						System.in.read();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        });
	 
	        stopper.start();
	 
	        System.out.println("Now say something into your microphone...");
	        // start recording
	        recorder.start();
	        System.out.println("Press any key to stop recording...");
	    }
    }
}
