//Pablo du Pontavice, 260674100
package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class Arm extends DAGNode {

	DoubleParameter tx, ty, tz;
		
	public Arm( String name ) {
		super(name);
		
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		//float degToRad = (float)(180.0*(Math.PI));
		
		// TODO: implement the rest of this method
		
		gl.glColor3f(0.5f, 0.35f, 0.05f); // set drawing color to white
		
		
		gl.glPushMatrix();
		//gl.glTranslatef( tx.getFloatValue(), ty.getFloatValue(), tz.getFloatValue() );
	    
	    gl.glPushMatrix();
		
		glut.glutSolidCylinder(0.3, 5, 50, 50);
		gl.glPopMatrix();
		
		super.display(drawable);
		gl.glPopMatrix();
	}

	
}

