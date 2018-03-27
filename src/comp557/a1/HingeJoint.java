//Pablo du Pontavice, 260674100
package comp557.a1;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import mintools.parameters.DoubleParameter;

public class HingeJoint extends DAGNode{
	
	DoubleParameter rotation;
	float x, y, z;	
	float tranX, tranY, tranZ;
	//DoubleParameter tx, ty, tz;
	
	public HingeJoint(String name, float transX, float transY, float transZ, float pickX, float pickY, float pickZ, double min, double max) {
		super(name);
		dofs.add(rotation = new DoubleParameter( name+" rotation", 0, min, max ) );
		
		/*dofs.add( tx = new DoubleParameter( name+" tx", 0, -2, 2 ) );		
		dofs.add( ty = new DoubleParameter( name+" ty", 0, -2, 2 ) );
		dofs.add( tz = new DoubleParameter( name+" tz", 0, -2, 2 ) );*/
		
		tranX = transX; 
		tranY = transY; 
		tranZ = transZ;
		
		x = pickX;
		y = pickY;
		z = pickZ;
	}
	
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		gl.glColor3f(1.0f, 0, 0);
		
		gl.glPushMatrix();
		gl.glTranslatef(tranX, tranY, tranZ);
		//gl.glTranslatef( tx.getFloatValue(), ty.getFloatValue(), tz.getFloatValue() );
		gl.glRotated(90f, 0, 1f, 0);
		gl.glRotatef(rotation.getFloatValue(), x, y, z);
		
		gl.glPushMatrix();
		gl.glScalef(0.3f, 0.3f, 0.3f);
		glut.glutSolidSphere(1, 50, 50);
		gl.glPopMatrix();
		
		super.display(drawable);
		gl.glPopMatrix();
	}
}
