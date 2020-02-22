/**
 * 
 */
package code.ravi.kappagantu.gstreamer;

import java.io.IOException;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;

/**
 * @author Ravi Kappagantu
 * 
 * The purpose of this program is to capture video from Webcam + Audio from Micro stream
 * Multiplex them in MPEG format, then convert it to RTP stream
 * Send the RTP stream to a UDP sink 
 *
 */
public class AVCapture {
	 
	Pipeline avPipe;
	AVCapture(){
		Gst.init();
	}
	
	Pipeline getavPipe(){
		return avPipe;
	}
    /**
     * Captures the sound and record into a WAV file and an OGG file
     */
    void start() {
    	
    	String pipeSpec ="";
    	//Capture Video and send it to mux
    	pipeSpec += "ksvideosrc ! video/x-raw, framerate=30/1 ! "
    			+ "videoconvert ! queue ! videorate ! x264enc "
    			+ "noise-reduction=10000 tune=zerolatency byte-stream=true threads=8 "
    			+ "key-int-max=15 intra-refresh=true ! queue ! mux. ";
    	
    	//Capture Audio and send it to Mux
    	pipeSpec += "autoaudiosrc ! audioconvert ! audioresample ! avenc_ac3! queue ! mux. ";
    	
    	//Mux using MPEG Muxer and then convert to RTP stream
    	pipeSpec += "mpegtsmux name=mux ! queue ! rtpmp2tpay ! ";
    	
    	//Send the stream to UDP location sink
    	pipeSpec += "udpsink port=3001 host=localhost sync=false";
    
        avPipe = (Pipeline) Gst.parseLaunch(pipeSpec);

        avPipe.play();
    }
 
    /**
     * Entry to run the program
     */
    public static void main(String[] args) {
    
        final AVCapture recorder = new AVCapture();
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
 
        System.out.println("You can watch your AV Stream output in VLC Plater via rtp://localhost:3001");
        recorder.start();
    }
}
