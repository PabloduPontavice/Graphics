//Pablo du Pontavice, 260674100
package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class Head extends DAGNode {

	DoubleParameter tx, ty, tz;
		
	public Head( String name ) {
		super(name);
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		//float degToRad = (float)(180.0*(Math.PI));
		
		// TODO: implement the rest of this method
		
		gl.glColor3f(1.0f, 1.0f, 1.0f); // set drawing color to white
		
		
		gl.glPushMatrix();
		gl.glTranslatef( 0, 4.5f, 0 );
	    
	    gl.glPushMatrix();
		gl.glScaled(2.15, 2.15, 2.15);
		glut.glutSolidSphere(1, 50, 50);
		gl.glPopMatrix();
		
		gl.glColor3f(0, 0, 0);
		gl.glPushMatrix();
		gl.glTranslatef(-0.6f, 0.6f, 2.15f);	
		gl.glScaled(0.25, 0.25, 0.25);
		glut.glutSolidSphere(1, 50, 50);	
		gl.glPopMatrix();
		
		gl.glColor3f(0, 0, 0);
		gl.glPushMatrix();
		gl.glTranslatef(0.6f, 0.6f, 2.15f);	
		gl.glScaled(0.25, 0.25, 0.25);
		glut.glutSolidSphere(1, 50, 50);	
		gl.glPopMatrix();
		
		gl.glColor3f(1f, 0.5f, 0);
		gl.glPushMatrix();
		gl.glTranslated(0, 0, 2.15f);
		gl.glScaled(1, 1, 1);
		glut.glutSolidCone(0.2, 1, 50, 50);
		gl.glPopMatrix();
		
		super.display(drawable);
		gl.glPopMatrix();
	}

	
}

