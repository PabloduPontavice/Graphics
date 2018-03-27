//Pablo du Pontavice
//260674100
package comp557lw.a2;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3d;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import mintools.parameters.BooleanParameter;
import mintools.parameters.DoubleParameter;
import mintools.viewer.FancyAxis;
import mintools.viewer.FlatMatrix4f;
import mintools.viewer.GLCylinder;
import mintools.viewer.GLSolidCube;
import mintools.viewer.GLSphere;
import mintools.viewer.GLU;
import mintools.viewer.GLWireCube;
import mintools.viewer.TrackBallCamera;

/**
 * Main class for per pixel lighting and shadow map assignment
 * 
 * TODO: Objective 0: Add your student number 
 * 
 * @author YOUR NAME AND STUDENT NUMBER HERE
 */
public class A2PFLApp{

	public static void main( String[] args ) {		
		A2PFLApp a2 = new A2PFLApp();
		a2.run();
	}
	
	public A2PFLApp() {
		control = new A2Control( this );
	}
	
	A2Control control;
	    
    int windowWidth = 1280/2; // half 720p resolution
    int windowHeight = 720/2;
    final int depthFBOSizeWidth = 512;
	final int depthFBOSizeHeight = 512;
    
    DoubleParameter lightPosx = new DoubleParameter( "light pos x", 0, -10, 10 );	
	DoubleParameter lightPosy = new DoubleParameter( "light pos y", 10, -10, 20 );
	DoubleParameter lightPosz = new DoubleParameter( "light pos z", 3, -10, 10 );
	DoubleParameter lightFOV = new DoubleParameter( "light fov", 45, 30, 100 );

	BooleanParameter drawFrustum = new BooleanParameter( "draw frustum", false );
	
	// TODO: Objective 6: Set the second parameter, default value of sigma, to be as small as possible while avoiding self shadowing
	DoubleParameter sigma = new DoubleParameter( "sigma", 0.01, 0, 0.5 );  
	
	TrackBallCamera tbc = new TrackBallCamera();
	
	private int depthTexture;
	private int depthFBO;
	private int depthDrawProgramID;

	/**
	 * IDs for the GLSL programs created in different objectives.  We will set
	 * programID[0] to zero to likewise allow use of the fixed functionality pipeline
	 */
	private int[] programID = new int[5]; 

	/**
	 * Index to say which GLSL programID we will use to draw the camera view
	 */
	int drawMode = 0;

	private long window;	// window handler
	
	public void run() {
			// TODO: Objective 0: add your name to the window title
			System.out.println("YOUR NAME HERE " + Version.getVersion() + "!");	    
			windowInit();
			init();
			loop();
			// Free the window callbacks and destroy the window
			glfwFreeCallbacks(window);			
			glfwDestroyWindow(window);
			// Terminate GLFW and free the error callback
			glfwTerminate();
			glfwSetErrorCallback(null).free();
			System.exit(0);
		}
	
	private void windowInit() {
		
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		
	    // Configure GLFW
 		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(windowWidth, windowHeight, "Shadow Maps", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			control.invoke(window, key, scancode, action, mods);			
		});

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
		
		glfwSetWindowSizeCallback( window, new GLFWWindowSizeCallbackI() {
			@Override
			public void invoke(long window, int width, int height) {
				windowWidth = width;
				windowHeight = height;
		        glViewport( 0, 0, width, height );
			}
		});
		glfwSetMouseButtonCallback( window, new GLFWMouseButtonCallbackI() {			
			@Override
			public void invoke(long window, int button, int action, int mods) {
				tbc.mouseButtonCallback( window, button, action, mods );
			}
		});
		glfwSetCursorPosCallback( window, new GLFWCursorPosCallbackI() {			
			@Override
			public void invoke(long window, double xpos, double ypos) {
				tbc.cursorPosCallback( window, xpos, ypos );				
			}});					
	}


	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			display();			
			glfwSwapBuffers(window); // swap the color buffers
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}



	/**
	 * Creates a GLSL program from the .vp and .fp code provided in the shader directory 
	 * @param drawable
	 * @param name
	 * @return
	 */
	private int createProgram( String name ) {
		List<ShaderInfo> shaders = new LinkedList<ShaderInfo>();
		shaders.add( new ShaderInfo( GL_VERTEX_SHADER, name+".vp" ) );
		shaders.add( new ShaderInfo( GL_FRAGMENT_SHADER, name+".fp" ) );
		return LoadShader.loadShaders(shaders);		
	}

	/**
	 * Set up the OpenGL rendering context, including the shader programs and 
	 */
	public void init() {
    	GL.createCapabilities();

		// don't need this if we're doing it in the per fragment lighting program
		// but we'll include it for when PFL is not enabled
		glEnable( GL_NORMALIZE );

		glClearColor(0.0f, 0.0f, 0.0f, 0.5f);    // Black Background
		glClearDepth(1.0f);                      // Depth Buffer Setup
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_LIGHTING);
		glEnable( GL_BLEND );
		glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
		glEnable( GL_LINE_SMOOTH );
		glEnable( GL_POINT_SMOOTH );

		// CREATE THE FRAGMENT PROGRAM FOR DRAWING DEPTH
		depthDrawProgramID = createProgram( "shader/depthDraw" );

		// CREATE THE FRAGMENT PROGRAM FOR DIFFERENT OBJECTIVES
		// TODO: OBJECTIVE 1: add ambient lighting to file shader/mode1.fp
		// TODO: OBJECTIVE 2: add ambient lighting to file shader/mode2.fp
		// TODO: OBJECTIVE 3: add ambient lighting to file shader/mode3.fp
		// TODO: OBJECTIVE 6: add ambient lighting to file shader/mode4.fp
		programID[0] = 0;
		for ( int i = 1; i <= 4; i++ ) {
			programID[i] = createProgram( "shader/mode" + i );
		}

		// SET UP RENDER TO TEXTURE FOR LIGHT DEPTH OFF SCREEN RENDERING
		depthTexture = glGenTextures();
		glBindTexture( GL_TEXTURE_2D, depthTexture );
		// By clamping texture lookups to the border, we can force the use of an arbitrary depth value
		// on the edge and outside of our depth map. {1,1,1,1} is max depth, while {0,0,0,0} is min depth
		// Ultimately, you may alternatively want to deal with clamping issues in a fragment program.
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, new float[] {1,1,1,1} );
		// The default filtering parameters not appropriate for depth maps, so we set them here! 
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);  
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		// You can also try GL_DEPTH_COMPONENT16, GL_DEPTH_COMPONENT24 for the internal format.
		// Alternatively GL_DEPTH24_STENCIL8_EXT can be used (GL_EXT_packed_depth_stencil).
		// Here, null means reserve texture memory without initializing the contents.
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, depthFBOSizeWidth, depthFBOSizeHeight, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_INT, (IntBuffer) null);
		depthFBO = glGenFramebuffers(); 
		glBindFramebuffer( GL_FRAMEBUFFER, depthFBO );
		glFramebufferTexture2D( GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);
		checkFramebufferStatus();

		// Restore the original screen rendering frame buffer binding
		glBindFramebuffer( GL_FRAMEBUFFER, 0 );
	}

	private int checkFramebufferStatus() {
		String statusString = "";
		int framebufferStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		switch (framebufferStatus) {
		case GL_FRAMEBUFFER_COMPLETE:
			statusString = "GL_FRAMEBUFFER_COMPLETE"; break;
		case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
			statusString = "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENTS"; break;
		case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
			statusString = "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT"; break;
		case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
			statusString = "GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER"; break;
		case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
			statusString = "GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER";  break;
		case GL_FRAMEBUFFER_UNSUPPORTED:
			statusString = "GL_FRAMEBUFFER_UNSUPPORTED"; break;
		}
		if ( framebufferStatus != GL_FRAMEBUFFER_COMPLETE ) {
			System.err.println("Problems checking framebuffer status");
			System.err.println(statusString);
			System.exit(-1);
		}
		return framebufferStatus;
	}
	
	public void setupLightsInWorld( ) {
		float[] position = { lightPosx.getFloatValue(), lightPosy.getFloatValue(), lightPosz.getFloatValue(), 1 };
		float[] colour = { 0.8f, 0.8f, 0.8f, 1 };
		float[] acolour = { 0.3f, 0.3f, 0.3f, 1 };
		glLightfv( GL_LIGHT0, GL_SPECULAR, colour );
		glLightfv( GL_LIGHT0, GL_DIFFUSE, colour );
		glLightfv( GL_LIGHT0, GL_AMBIENT, acolour );
		glLightfv( GL_LIGHT0, GL_POSITION, position );
		glEnable( GL_LIGHT0 );
		// no extra ambient light 
		glLightModelfv( GL_LIGHT_MODEL_AMBIENT, new float[] {0,0,0,1} );
	}

	/**
	 * The light projection must be provided to the GLSL program so that the positions
	 * of vertices transformed by modelview matrix can likewise be transformed into
     * the [-1,1]^3 canonical view volume of the light view.
	 * NOTE: FlatMatrix4f is a convenient wrapper that combines a vecmath Matrix4f, 
	 * as well as methods asArray() and reconstitute(), which are useful for using 
	 * the matrix with OpenGL 
	 */
	public FlatMatrix4f lightProjectionMatrix = new FlatMatrix4f();        
	
	FlatMatrix4f LightViewingTransformation = new FlatMatrix4f(); 	
	FlatMatrix4f LightViewingTransformationInverse = new FlatMatrix4f(); 
	FlatMatrix4f LightProjectionTransformation = new FlatMatrix4f(); 
	FlatMatrix4f LightProjectionTransformationInverse = new FlatMatrix4f(); 
	
	private double time = 0;
	boolean animate = false;

	public void display() {

		if ( animate ) {
			time = System.nanoTime()*1e-9; // store the time for doing animation
		}
		
		/////////////////////////////////////////////////////////////////////////
		//
		//  Prepare matrices!
		//
		/////////////////////////////////////////////////////////////////////////
				
		glMatrixMode( GL_PROJECTION );
		glLoadIdentity();        
		// TODO: Objective 4: set up the light viewing transformation
		// Here is some example code that switches to using the projection matrix
		// stack, and then gets the projection matrix as a flat matrix, copying the 
		// array data to reconstitute the backing Matrix4f.  The inverse is also 
		// computed for your convenience
		float tempf = lightPosx.getFloatValue();
		if(lightPosx.getFloatValue() == 0.0f && lightPosz.getFloatValue() == 0.0f) {
			tempf = 0.0001f;
		}
		
		GLU.gluLookAt(tempf, lightPosy.getFloatValue(), lightPosz.getFloatValue(), 0, 1, 0, 0, 1, 0);
		
		glGetFloatv( GL_PROJECTION_MATRIX, LightViewingTransformation.asArray() );        
		LightViewingTransformation.reconstitute();
		LightViewingTransformationInverse.getBackingMatrix().invert(LightViewingTransformation.getBackingMatrix());

		glMatrixMode( GL_PROJECTION );
		glLoadIdentity();
		// TODO: Objective 5: set up the light projection transformation
		//glGetFloatv( GL_PROJECTION_MATRIX, LightViewingTransformation.asArray() );        
		//LightViewingTransformation.reconstitute();
		//LightViewingTransformationInverse.getBackingMatrix().invert(LightViewingTransformation.getBackingMatrix());

		float far = (float) Math.sqrt(lightPosx.getFloatValue()*lightPosx.getFloatValue() + lightPosy.getFloatValue()*lightPosy.getFloatValue() + lightPosz.getFloatValue()*lightPosz.getFloatValue()) + 0.5f;
		GLU.gluPerspective(lightFOV.getFloatValue(), 1, 2, far + 5);
		
		glGetFloatv( GL_PROJECTION_MATRIX, LightProjectionTransformation.asArray() );        
		LightProjectionTransformation.reconstitute();
		LightProjectionTransformationInverse.getBackingMatrix().invert(LightProjectionTransformation.getBackingMatrix());
		// TODO: Objective 6: Build the matrix that transforms from camera frame to the light's NDC
		// you can use vecmath multiplication to compose transformations to form this matrix.
		// you will likewise want to use some of the track ball camera matrices.  See the following
		// functions, and consider extracing their matrices from the matrix stack in a manner
		// similar to the above examples.
		glLoadIdentity();
		Matrix4f LPM = lightProjectionMatrix.getBackingMatrix();
		LPM.setIdentity();
		
		FlatMatrix4f tbcInvView = new FlatMatrix4f();
		tbc.applyInverseViewTransformation();
		glGetFloatv( GL_PROJECTION_MATRIX, tbcInvView.asArray() );
		tbcInvView.reconstitute();
		
		LPM.mul( LightProjectionTransformation.getBackingMatrix() );
		LPM.mul( LightViewingTransformation.getBackingMatrix() );
		LPM.mul( tbcInvView.getBackingMatrix() );
		//tbc.applyInverseViewTransformation();
		//tbc.applyProjectionTransformation();
		//tbc.applyViewTransformation();
		
		/////////////////////////////////////////////////////////////////////////
		//
		// Render to the off-screen depth frame buffer object (render to texture)
		//
		/////////////////////////////////////////////////////////////////////////
		
		glBindFramebuffer( GL_FRAMEBUFFER, depthFBO );
		glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
		glViewport( 0, 0, depthFBOSizeWidth, depthFBOSizeHeight ); 
	
		glMatrixMode( GL_PROJECTION );
		glLoadMatrixf( LightProjectionTransformation.asArray() );
		glMatrixMode( GL_MODELVIEW );
		glLoadMatrixf( LightViewingTransformation.asArray() );
		
		drawScene(); 
		
		//////////////////////////////////////////////////////////////////        
		//
		// Render to the screen
		//
		//////////////////////////////////////////////////////////////////
		
		glBindFramebuffer( GL_FRAMEBUFFER, 0 );
		glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
		control.setGlViewportWithPlatform( 0, 0, windowWidth, windowHeight );
		
		tbc.prepareForDisplay();
		setupLightsInWorld();
		
		int PID = programID[drawMode];
		glUseProgram( PID ); 
		
		if ( drawMode == 4 ) {
			// these uniform locations won't change once we've compiled, so it is silly
			// to query every time, but it is likewise not expensive.
			int shadowMapID = glGetUniformLocation(	PID, "shadowMap" );
			int lightProjectionID = glGetUniformLocation(	PID, "lightProjection" );	
			int sigmaID = glGetUniformLocation(	PID, "sigma" );	
			glUniform1i( shadowMapID, 0 ); // use are using texture unit zero!
			glUniformMatrix4fv( lightProjectionID, false, lightProjectionMatrix.asArray() ); 
			glUniform1f( sigmaID, sigma.getFloatValue() );
		}
		
		drawScene();

		glUseProgram( 0 ); 

		if ( drawFrustum.getValue() ) {

			glPushMatrix();
			// TODO: Objective 4: Use the appropriate transformations for drawing the light's camera frame
			glMultMatrixf(LightViewingTransformationInverse.asArray());
			glLightModelfv( GL_LIGHT_MODEL_AMBIENT, new float[] {.5f,.5f,.5f,1});
			FancyAxis.DEFAULT.draw();
			glLightModelfv( GL_LIGHT_MODEL_AMBIENT, new float[] {0,0,0,1});
			// Note the above code draws the light's eye frame, with some ambient 			
			// light turned on so we can better see it (the fancy axis is otherwise only
			// lit by the light which is found at its origin.			
			glPopMatrix();

			glPushMatrix();
			glMultMatrixf(LightViewingTransformationInverse.asArray());
			glMultMatrixf(LightProjectionTransformationInverse.asArray());
			// TODO: Objective 5: setup the appropriate matrices to draw the light frustum's NDC
			// to allow the frustubm to be drawn with a wire cube, and a textured quad to be drawn
			// on the near plane (provided the light rendering pass and matrices are correctly set up)
			glDisable( GL_LIGHTING );
			glColor4f( 1, 1, 1, 0.5f );
			glLineWidth( 3 );     
			GLWireCube.DEFAULT.draw();
			glEnable( GL_LIGHTING );
			// The following code draws the depth map on the near plane of the light view frustum
			// provided the correct matrix has been set above
			glUseProgram( depthDrawProgramID );
			glBindTexture( GL_TEXTURE_2D, depthTexture );
			int textureUnitID = glGetUniformLocation( depthDrawProgramID, "depthTexture" );
			int alphaID = glGetUniformLocation( depthDrawProgramID, "alpha" );  
			glUniform1i( textureUnitID, 0 );  // Texture unit ID, not the texture ID 
			glUniform1f( alphaID,  0.5f ); 		
			glDisable( GL_LIGHTING );
			glEnable( GL_TEXTURE_2D );
			// Draw a quad with texture coordinates that span the entire texture.  
			// Note that we put the quad on the near plane by providing z = -1
			glBegin( GL_QUADS );
			glTexCoord2d( 0, 0 ); glVertex3f(-1, -1, -1 );
			glTexCoord2d( 1, 0 ); glVertex3f( 1, -1, -1 );
			glTexCoord2d( 1, 1 ); glVertex3f( 1,  1, -1 );
			glTexCoord2d( 0, 1 ); glVertex3f(-1,  1, -1 );
			glEnd();
			glDisable( GL_TEXTURE_2D );
			glEnable( GL_LIGHTING );
			glUseProgram( 0 );
			glPopMatrix();
		}
	}

	public void drawScene( ) {
		final float[] orange = new float[] {1,.5f,0,1};
		final float[] red    = new float[] {1,0,0,1};
		final float[] green  = new float[] {0,1,0,1};
		final float[] blue   = new float[] {0,0,1,1};

		FancyAxis.DEFAULT.draw();

		glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, red );
		glMaterialfv( GL_FRONT_AND_BACK, GL_SPECULAR, new float[] {1,1,1,1} );
		glMaterialf( GL_FRONT_AND_BACK, GL_SHININESS, 50 );

		glDisable(GL_CULL_FACE);       
		glPushMatrix();
		
		double c = 2 * Math.cos(time);
		double s = 2 * Math.sin(time);
		
		glTranslated(-c, 0.5+Math.abs(Math.cos(time)),s);
		glRotated(45+time*180, 0, 1, 0);
		glScaled(0.5,0.5,0.5);
		glTranslated(0,0,-0.5);
		GLCylinder.DEFAULT.draw();
		glPopMatrix();

		glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, green );
		glMaterialfv( GL_FRONT_AND_BACK, GL_SPECULAR, new float[] {1,1,1,1} );
		glMaterialf( GL_FRONT_AND_BACK, GL_SHININESS, 50 );
		glPushMatrix();
		
		glTranslated( c, 0.5+Math.abs(Math.sin(time)), s );
		glScaled(0.5,0.5,0.5);
		GLSphere.DEFAULT.draw();
		glPopMatrix();

		glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, blue );
		glMaterialfv( GL_FRONT_AND_BACK, GL_SPECULAR, new float[] {1,1,1,1} );
		glMaterialf( GL_FRONT_AND_BACK, GL_SHININESS, 50 );
		glPushMatrix();
		glTranslated(0,2.5,2*Math.sin(time+1.20));
		glRotated(time*180,1,2,3);
		GLSolidCube.DEFAULT.draw();
		glPopMatrix();

		glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, orange );
		glMaterialfv( GL_FRONT_AND_BACK, GL_SPECULAR, new float[] {1,1,1,1} );
		glMaterialf( GL_FRONT_AND_BACK, GL_SHININESS, 50 );
		glPushMatrix();
		glScaled(15,0.1,15);
		glTranslated(0,-.5,0);
		GLSolidCube.DEFAULT.draw();
		glPopMatrix();
	}
}
