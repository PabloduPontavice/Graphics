//Pablo du Pontavice, 260674100
package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class LowerHalf extends DAGNode {

	DoubleParameter tx, ty, tz;
		
	public LowerHalf( String name ) {
		super(name);
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		//float degToRad = (float)(180.0*(Math.PI));
		
		// TODO: implement the rest of this method
		
		gl.glColor3f(1.0f, 1.0f, 1.0f); // set drawing color to white
		
		
		gl.glPushMatrix();
		gl.glTranslatef( 0, -5.5f, 0 );
	    
	    gl.glPushMatrix();
		gl.glScaled(5, 5, 5);
		glut.glutSolidSphere(1, 50, 50);
		gl.glPopMatrix();
		
		super.display(drawable);
		gl.glPopMatrix();
	}

	
}

