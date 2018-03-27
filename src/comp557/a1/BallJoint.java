//Pablo du Pontavice, 260674100
package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class BallJoint extends DAGNode{
	
	DoubleParameter rotation;
	float x, y, z;	
	float tranX, tranY, tranZ;
	DoubleParameter tx, ty, tz;
	DoubleParameter rx, ry, rz;
	float scaler;
	
	public BallJoint(String name, float transX, float transY, float transZ, double min, double max, float givenScaler) {
		super(name);
		dofs.add(rx = new DoubleParameter( name+" rotation", 0, min, max ) );
		dofs.add(ry = new DoubleParameter( name+" rotation", 0, min, max ) );
		dofs.add(rz = new DoubleParameter( name+" rotation", 0, min, max ) );
		
		/*dofs.add( tx = new DoubleParameter( name+" tx", 0, -3, 3 ) );		
		dofs.add( ty = new DoubleParameter( name+" ty", 0, -3, 3 ) );
		dofs.add( tz = new DoubleParameter( name+" tz", 0, -3, 3 ) );*/
		
		tranX = transX; 
		tranY = transY; 
		tranZ = transZ;
		
		scaler = givenScaler;
		
	}
	
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glColor3f(0, 0, 1.0f);
		
		gl.glPushMatrix();
		gl.glTranslatef(tranX, tranY, tranZ);
		//gl.glTranslatef( tx.getFloatValue(), ty.getFloatValue(), tz.getFloatValue() );
		gl.glRotatef(rx.getFloatValue(), 1.0f, 0, 0);
		gl.glRotatef(ry.getFloatValue(), 0, 1.0f, 0);
		gl.glRotatef(rz.getFloatValue(), 0, 0, 1.0f);
		
		gl.glPushMatrix();
		gl.glScalef(scaler, scaler, scaler);
		glut.glutSolidSphere(1, 50, 50);
		gl.glPopMatrix();
		
		super.display(drawable);
		gl.glPopMatrix();
		
	}
}
