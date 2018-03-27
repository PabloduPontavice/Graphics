//Pablo du Pontavice, 260674100
package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class FreeJoint extends DAGNode {

	DoubleParameter tx;
	DoubleParameter ty;
	DoubleParameter tz;
	DoubleParameter rx;
	DoubleParameter ry;
	DoubleParameter rz;
		
	public FreeJoint( String name ) {
		super(name);
		dofs.add( tx = new DoubleParameter( name+" tx", 0, -2, 2 ) );		
		dofs.add( ty = new DoubleParameter( name+" ty", 0, -2, 2 ) );
		dofs.add( tz = new DoubleParameter( name+" tz", 0, -2, 2 ) );
		dofs.add( rx = new DoubleParameter( name+" rx", 0, -180, 180 ) );		
		dofs.add( ry = new DoubleParameter( name+" ry", 0, -180, 180 ) );
		dofs.add( rz = new DoubleParameter( name+" rz", 0, -180, 180 ) );
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		//float degToRad = (float)(180.0*(Math.PI));
		
		// TODO: implement the rest of this method
		
		gl.glColor3f(1.0f, 1.0f, 1.0f); // set drawing color to white
		
		
		gl.glPushMatrix();
		gl.glTranslatef( tx.getFloatValue(), ty.getFloatValue(), tz.getFloatValue() );
	    gl.glRotatef(rx.getFloatValue(), 1.0f, 0, 0);
	    gl.glRotatef(ry.getFloatValue(), 0, 1.0f, 0);
	    gl.glRotatef(rz.getFloatValue(), 0, 0, 1.0f);
	    
	    gl.glPushMatrix();
		gl.glScaled(4, 5, 4);
		glut.glutSolidSphere(0.75, 50, 50);
		gl.glPopMatrix();
		
		gl.glColor3f(0, 0, 0);
		gl.glPushMatrix();
		gl.glTranslatef(0, 2f, 2.75f);	
		gl.glScaled(0.25, 0.25, 0.25);
		glut.glutSolidSphere(1, 50, 50);	
		gl.glPopMatrix();
		
		gl.glColor3f(0, 0, 0);
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 3f);	
		gl.glScaled(0.25, 0.25, 0.25);
		glut.glutSolidSphere(1, 50, 50);	
		gl.glPopMatrix();
		
		gl.glColor3f(0, 0, 0);
		gl.glPushMatrix();
		gl.glTranslatef(0, -2f, 2.75f);	
		gl.glScaled(0.25, 0.25, 0.25);
		glut.glutSolidSphere(1, 50, 50);	
		gl.glPopMatrix();
		
		super.display(drawable);
		gl.glPopMatrix();
	}

	
}
