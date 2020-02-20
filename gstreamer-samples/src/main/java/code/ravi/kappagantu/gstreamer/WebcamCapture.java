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
 * 
 * The purpose of this program is to capture video from Webcam and store it in a file 
 *
 */
public class WebcamCapture {
	 
	Pipeline webcamPipe;
	WebcamCapture(){
		Gst.init();
	}
	
	Pipeline getWebcamPipe(){
		return webcamPipe;
	}
    /**
     * Captures the sound and record into a WAV file and an OGG file
     */
    void start() {
    	String pipeSpec = "ksvideosrc ! queue ! "
    			+ "video/x-raw, framerate=30/1 ! queue ! videoconvert ! queue ! "
    			+ "videorate ! x264enc noise-reduction=10000 tune=zerolatency "
    			+ "byte-stream=true threads=4 key-int-max=15 intra-refresh=true ! "
    			+ "queue ! mpegtsmux ! queue ! rtpmp2tpay ! "
    			+ "udpsink host=localhost port=5000 sync=false";
    
        webcamPipe = (Pipeline) Gst.parseLaunch(pipeSpec);

        webcamPipe.play();
    }
 
    /**
     * Entry to run the program
     */
    public static void main(String[] args) {
    
        final WebcamCapture recorder = new WebcamCapture();
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
 
        System.out.println("You can watch your webcam output in VLC Plater via rtp://localhost:5000");
        recorder.start();
    }
}
