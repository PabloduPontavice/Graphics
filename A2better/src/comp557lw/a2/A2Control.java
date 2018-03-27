package comp557lw.a2;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.Dimension;

import javax.swing.JPanel;

import org.lwjgl.system.Platform;

import mintools.parameters.BooleanParameter;
import mintools.parameters.DoubleParameter;
import mintools.swing.ControlFrame;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.TrackBallCamera;

/**
 * This Class deals with platform specific ugly interface issues
 */
public class A2Control {
	
	private Dimension controlSize;
	ControlFrame controlFrame;

	A2PFLApp app;

	static final boolean isMacPlatform = (Platform.get() == Platform.MACOSX);

	public A2Control( A2PFLApp application ) {
		this.app = application;
		showMessage();
		if ( !isMacPlatform ) {
			createControlWindow();
		}
	}

	public void createControlWindow() {
		controlSize = new Dimension(500, 500);
		controlFrame = new ControlFrame("Controls");
		controlFrame.add("Scene", getControls());
		controlFrame.add("Trackball", app.tbc.getControls());
		controlFrame.setSelectedTab("Scene");
		controlFrame.setSize(controlSize.width, controlSize.height);
		controlFrame.setLocation(controlSize.width + 20, 0);
		controlFrame.setVisible(true);
	}

	public void showMessage() {
		String stars = "******************************************************";
		String message = stars + "\n" + "\nKeyboard Controls:\n\n" 
                + "0-4    - Change rendering mode\n" 
				+ " F     - Draw Frustum On/Off\n"
				+ " SPACE - Toggle Animation On/Off\n" 
				+ " ]     - Increase sigma by 0.01\n"
				+ " [     - Decrease sigma by 0.01\n"  
				+ " LEFT  - Light position move along positive x axis\n"
				+ " RIGHT - Light position move along negative x axis\n"
				+ " UP    - Light position move along positive y axis\n"
				+ " DOWN  - Light position move along negative y axis\n"
				+ " A     - Light position move along positive z axis\n"
				+ " Z     - Light position move along negative z axis\n\n" + stars;
		System.out.println(message);
	}

	public JPanel getControls() {
		VerticalFlowPanel vfp = new VerticalFlowPanel();
		vfp.add(app.lightPosx.getSliderControls(false));
		vfp.add(app.lightPosy.getSliderControls(false));
		vfp.add(app.lightPosz.getSliderControls(false));
		vfp.add(app.lightFOV.getSliderControls(false));
		vfp.add(app.drawFrustum.getControls());
		vfp.add(app.sigma.getSliderControls(false));
		return vfp.getPanel();
	}

	/**
	 *  GLFW keyboard call back function, critical for MACOSX interface, and available on other platforms
	 * @param window
	 * @param key
	 * @param scancode
	 * @param action
	 * @param mods
	 */
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (key == GLFW_KEY_F && action == GLFW_RELEASE) {
			if (app.drawFrustum.getValue()) {
				app.drawFrustum.setValue(false);
			} else {
				app.drawFrustum.setValue(true);
			}
		} else if (key == GLFW_KEY_SPACE && action == GLFW_RELEASE ) {
			app.animate = ! app.animate;
		} else if (key == GLFW_KEY_0 && action == GLFW_RELEASE ) {
			app.drawMode = 0;
		} else if (key == GLFW_KEY_1 && action == GLFW_RELEASE ) {
			app.drawMode = 1;
		} else if (key == GLFW_KEY_2 && action == GLFW_RELEASE ) {
			app.drawMode = 2;
		} else if (key == GLFW_KEY_3 && action == GLFW_RELEASE ) {
			app.drawMode = 3;
		} else if (key == GLFW_KEY_4 && action == GLFW_RELEASE ) {
			app.drawMode = 4;
		} else if (key == GLFW_KEY_LEFT && action == GLFW_RELEASE) {
			app.lightPosx.setValue(app.lightPosx.getValue() - 1.0);
		} else if (key == GLFW_KEY_RIGHT && action == GLFW_RELEASE) {
			app.lightPosx.setValue(app.lightPosx.getValue() + 1.0);
		} else if (key == GLFW_KEY_DOWN && action == GLFW_RELEASE) {
			app.lightPosy.setValue(app.lightPosy.getValue() - 1.0);
		} else if (key == GLFW_KEY_UP && action == GLFW_RELEASE) {
			app.lightPosy.setValue(app.lightPosy.getValue() + 1.0);
		} else if (key == GLFW_KEY_A && action == GLFW_RELEASE) {
			app.lightPosz.setValue(app.lightPosz.getValue() - 1.0);
		} else if (key == GLFW_KEY_Z && action == GLFW_RELEASE) {
			app.lightPosz.setValue(app.lightPosz.getValue() + 1.0);
		} else if (key == GLFW_KEY_LEFT_BRACKET && action == GLFW_RELEASE) {
			app.sigma.setValue(app.sigma.getValue() - 0.01);
		} else if (key == GLFW_KEY_RIGHT_BRACKET && action == GLFW_RELEASE) {
			app.sigma.setValue(app.sigma.getValue() + 0.01);
		} else if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
			glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
		}
	}
	
	public void setGlViewportWithPlatform(int x, int y, int windowWidth, int windowHeight ) {
		if(isMacPlatform) {
			// viewport scaling appears to be incorrect on many macs :(
			glViewport( x, y, windowWidth * 2 , windowHeight * 2); 
		} else {			
			glViewport( x, y, windowWidth, windowHeight); 
		}		
	}
}
