/**
 * 
 */
package code.ravi.kappagantu.gstreamer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;

import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.ElementFactory;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;

/**
 * @author Ravi Kappagantu
 * This program records audio from microphone or default recording device
 * and then provides an ability to pause and resume recording of audio based on external events
 * like button click
 */
 
public class DynamicPipeline {
 
	String oggPath;
	Pipeline audioPipe;
	Element audioSource;
	Element fileSinkOgg;
	Element audioConverter;
	Element oggMultiplexer;
	Element valve;
	DynamicPipeline(){
		Gst.init();
		audioPipe = new Pipeline("AudioPileline");
	}
	
	public void setOggPath(String pOggPath){
		oggPath = pOggPath;
	}
	String getOggPath(){
		return oggPath;
	}
	Pipeline getAudioPipe(){
		return audioPipe;
	}

	void setPause(Boolean value){
		valve.set("drop", value);
	}
	
	
    /**
     * Captures the sound and record into an OGG file
     */
    void start() {
    	audioSource = ElementFactory.make("autoaudiosrc", "audioSource");
    	audioPipe.setAutoFlushBus(true);
    	fileSinkOgg = ElementFactory.make("filesink", "OggFile");
    	fileSinkOgg.set("location", getOggPath());
    	audioConverter =  ElementFactory.make("audioconvert", "audioConverter1");
    	Element vorbisEncoder = ElementFactory.make("vorbisenc", "vorbisEncoder");
    	oggMultiplexer = ElementFactory.make("oggmux", "oggMux");
    	valve = ElementFactory.make("valve", "audioValve");
    	valve.set("drop", false);
    	
    	audioPipe.addMany(audioSource,
    					  audioConverter,
    					  vorbisEncoder,
    					  oggMultiplexer,
    					  valve,
    					  fileSinkOgg
    					  );

    	/* Link elements in Path 1 */
    	Pipeline.linkMany(audioSource,audioConverter,vorbisEncoder,
    			oggMultiplexer,valve, fileSinkOgg);

    	oggMultiplexer.set("max-page-delay", 10);
    	oggMultiplexer.set("max-delay", 10);
    	vorbisEncoder.set("hard-resync", true);
    	audioPipe.play();
    }
 
    /**
     * Entry to run the program
     */
    public static void main(String[] args) {
        
        if (args.length < 1) {
        	System.out.println("usage: DynamicPipeline OggDestinationPath");
        } else {
       		final DynamicPipeline recorder = new DynamicPipeline();
            recorder.setOggPath(args[0]);
	        // creates a new thread that waits for a keyboard interrupt before stopping
	        Thread rec = new Thread(new Runnable() {
	            public void run() {
						recorder.start();
	            }
	        });
        	rec.run();
        	EventQueue.invokeLater(() -> {
	            JFrame window = new JFrame("Multiple Sinks");
	            window.setPreferredSize(new Dimension(100, 80));
	            Box buttons = Box.createHorizontalBox();
	            JButton pauseButton = new JButton("Pause");
	            buttons.add(pauseButton);
	            JButton resumeButton = new JButton("Resume");
	            buttons.add(resumeButton);
	            window.add(buttons, BorderLayout.SOUTH);
	            window.pack();
	            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                pauseButton.setEnabled(true);
                resumeButton.setEnabled(false);
	
	            pauseButton.addActionListener(e -> {
	                    recorder.setPause(true);
	            		pauseButton.setEnabled(false);
	                    resumeButton.setEnabled(true);
	            });
	
	            resumeButton.addActionListener(e -> {
	            	recorder.setPause(false);
                    pauseButton.setEnabled(true);
                    resumeButton.setEnabled(false);
	            });
	
	            window.setVisible(true);
        	});
        	try {
        		rec.join();
        	}
        	catch(InterruptedException e) {
        		if (recorder.getAudioPipe().isPlaying())
        			recorder.getAudioPipe().stop();
        	}
	    }
    }
}
