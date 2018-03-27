//Pablo du Pontavice
//260674100

package comp557lw.a3;

import static org.lwjgl.opengl.GL11.*;

import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Point3d;

/**
 * Class containing the half edge pointers, and a method for drawing
 * the half edge for debugging and evaluation.
 */
public class HalfEdge {
    
	public Vertex half;
	
    public HalfEdge twin;
    public HalfEdge next;
    public Vertex head;
    public HalfEdge child1;
    public HalfEdge child2;
    public HalfEdge parent;
    public Face leftFace;

    /** @return the previous half edge (could just be stored) */
    public HalfEdge prev() {
        HalfEdge prev = this;
        while ( prev.next != this ) {
        	prev = prev.next;        
        }
        return prev;
    }
    
    /**
     * Displays the half edge as a half arrow pointing to the head vertex.
     * @param drawable
     */
    public void display() {
        Point3d p0 = prev().head.p;
        Point3d p1 = head.p;
        Point3d p2 = next.head.p;
        double x,y,z;
        
        glLineWidth(3);
        glDisable( GL_LIGHTING );
        glBegin( GL_LINE_STRIP );
        glColor4f(1,1,1,0.8f);
        x = p0.x * 0.8 + (p1.x + p2.x) * 0.1;
        y = p0.y * 0.8 + (p1.y + p2.y) * 0.1;
        z = p0.z * 0.8 + (p1.z + p2.z) * 0.1;
        glVertex3d( x, y, z );
        x = p1.x * 0.8 + (p0.x + p2.x) * 0.1;
        y = p1.y * 0.8 + (p0.y + p2.y) * 0.1;
        z = p1.z * 0.8 + (p0.z + p2.z) * 0.1;
        glVertex3d( x, y, z );
        x = p1.x * 0.7 + p0.x * 0.1 + p2.x * 0.2;
        y = p1.y * 0.7 + p0.y * 0.1 + p2.y * 0.2;
        z = p1.z * 0.7 + p0.z * 0.1 + p2.z * 0.2;
        glVertex3d( x, y, z );        
        glEnd();
        glLineWidth(1);
        glEnable( GL_LIGHTING );
    }
    
    public void divideEdge() {
    	// already has it from the twin
    	if(half != null) {
    		assert(twin != null && half == twin.half);
    		return;
    	}
		half = new Vertex();
    	if(twin == null) {
    		// boundary odd
    		Point3d p = half.p;
    		p.add(head.p);
    		p.add(prev().head.p);
    		p.scale(0.5);
    	} else {
    		assert(twin.half == null);
    		twin.half = half;
    		// internal odd
    		Point3d p = half.p;
    		p.add(head.p);
    		p.add(leftFace.child.p);
    		p.add(twin.head.p);
    		p.add(twin.leftFace.child.p);
    		p.scale(0.25);
    	}
    }
    
    public HalfEdge headGetFwE() {
    	HalfEdge cw = this;
    	while(true) {
    		cw = cw.next;
    		if(cw.twin == null) {
    			return cw;
    		}
    		cw = cw.twin;
    		
    		if(cw != this) {
    			continue;
    		}
    		else {
    			break;
    		}
    	}
    	return null;
    }
    
    public HalfEdge headGetBwE() {
    	HalfEdge ccw = this;
    	while(ccw.twin != null) {
    		ccw = ccw.twin.prev();
    		if(ccw == this) {
    			return null;
    		}
    	}
    	return ccw;
    }
    
    public Set<HalfEdge> headOut() {
    	Set<HalfEdge> vs = new HashSet<>();
    	HalfEdge cw = this;
    	while(true) {
    		// interior
    		cw = cw.next;
    		if(cw == null) break;
    		assert(cw != this);
    		vs.add(cw);
    		cw = cw.twin;
    		
    		if(cw != this && cw != null) {
    			continue;
    		}
    		else {
    			break;
    		}
    	}
    	if(cw == null) {
	    	// edge case
    		HalfEdge ccw = this;
    		while(ccw.twin != null) {
	    		ccw = ccw.twin;
	    		vs.add(ccw);
	    		ccw = ccw.prev();
	    		assert(ccw != this);
    		}
    		// HalfEdge that doesn't have an outgoing he
    		ccw = ccw.prev();
    		vs.add(ccw);
    	}
    	assert(vs.size() >= 3);
    	return vs;
    }
    
}
