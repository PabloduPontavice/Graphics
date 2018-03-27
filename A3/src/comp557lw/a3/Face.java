// Pablo du Pontavice
// 260674100
package comp557lw.a3;

import java.util.Set;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Simple face class
 */
public class Face {    
    
    /** Face normal that can be used for flat shading */
    Vector3d n;
    
    /** Some half edge on the face */
    HalfEdge he;
    
    /** Child vertex in the middle of the face */
    Vertex child;
    
    /** 
     * Constructs a face from a half edge, and computes the flat normal.
     * This constructor also sets all of the leftFace members of the 
     * half edges that make up this face.
     * @param he
     */
    public Face( HalfEdge he ) {
        this.he = he;
        Point3d p0 = he.head.p;
        Point3d p1 = he.next.head.p;
        Point3d p2 = he.next.next.head.p;
        Vector3d v1 = new Vector3d();
        Vector3d v2 = new Vector3d();
        n = new Vector3d();
        v1.sub(p1,p0);
        v2.sub(p2,p1);
        n.cross( v1,v2 );
        HalfEdge loop = he;
        while (true) {
            loop.leftFace = this;
            loop = loop.next;
            if( loop != he ) {
            	continue;
            }
            else {
            	break;
            }
        }
    }
    
    public void evenVertices() {
    	Point3d temp;
    	HalfEdge he = this.he;
    	do {
    		// Multiple he.s going to the same vertex don't need recomputing
    		if(he.head.child != null) continue;
    		Vertex child = he.head.child = new Vertex();
    		HalfEdge forward = he.headGetFwE();
        	// boundary
        	if(forward != null) {
        		HalfEdge backward = he.headGetBwE();
        		temp = new Point3d();
        		temp.set(he.head.p);
        		temp.scale(0.75);
        		child.p.add(temp);
        		temp = new Point3d();
        		temp.add(forward.head.p);
        		temp.add(backward.prev().head.p);
        		temp.scale(0.125);
        		child.p.add(temp);
        	}
        	
        	else {
        		Set<HalfEdge> out = he.headOut();
        		int k = out.size();
        		double beta = 3.0 / (2.0 * k);
        		double gamma = 1.0 / (4.0 * k);
        		
        		temp = new Point3d();
        		temp.set(he.head.p);
        		temp.scale(1.0 - beta - gamma);
        		child.p.add(temp);
        		
        		temp = new Point3d();
        		for(HalfEdge o : out) temp.add(o.head.p);
        		temp.scale(beta / k);
        		child.p.add(temp);
        		
        		temp = new Point3d();
        		
        		for(HalfEdge o : out) temp.add(o.next.head.p);
        		temp.scale(gamma / k);
        		child.p.add(temp);
        	}
    	} 
    	while((he = he.next) != this.he);
    }
    
    public void makeChild() {
    	assert(this.child == null);
    	HalfEdge he = this.he;
    	this.child = new Vertex();
    	Point3d sum = this.child.p;
    	int count = 0;
    	while(true) {
    		
    		assert(he != null);
    		sum.add(he.head.p);
    		count++;
    		
    		if((he = he.next) != this.he) {
    			continue;
    		}
    		else {
    			break;
    		}
    	}
    	assert(count >= 3);
    	sum.scale(1.0 / count);
    }
    
    
    public void subdivide(HEDS heds) {
    	// Check that the preconditions have been met
    	assert(this.child != null);
    	HalfEdge he = this.he, prevFromFace = null;
    	
    	while(true) {
        	// Check that the preconditions have been met
    		assert(he != null && he.child1 == null && he.child2 == null && he.half != null && he.head.child != null);
    		
    		he.child1 = new HalfEdge();
    		he.child1.head = he.half;
    		he.child1.parent = he;
    		if(he.twin != null && he.twin.child2 != null) he.child1.twin = he.twin.child2;
    		
    		he.child2 = new HalfEdge();
    		he.child2.head = he.head.child;
    		he.child2.parent = he;
    		if(he.twin != null && he.twin.child1 != null) he.child2.twin = he.twin.child1;
    		
			HalfEdge toFace = new HalfEdge(), fromFace = new HalfEdge();
			toFace.head = this.child;
			fromFace.head = he.half;
			toFace.twin = fromFace;
			fromFace.twin = toFace;
			// link
			he.child1.next = toFace;
			fromFace.next = he.child2;
			// get previous
    		if(prevFromFace != null) {
    			
    			assert(prevFromFace.next.next == null);
    			prevFromFace.next.next = he.child1;
    			
    			assert(toFace.next == null);
    			toFace.next = prevFromFace;
    			Face sub = new Face(he.child1);
    			
    			heds.faces.add(sub);
    		}
    		prevFromFace = fromFace;
    		
    		if((he = he.next) != this.he) {
    			continue;
    		}
    		else {
    			break;
    		}
    	} 
		
		assert(prevFromFace != null && prevFromFace.next.next == null);
		prevFromFace.next.next = he.child1;
		
		assert(he.child1.next.next == null);
		he.child1.next.next = prevFromFace;
		Face sub = new Face(he.child1);
		heds.faces.add(sub);
    }
    
    
    int number;
    static int debug_no;
        
    public String toString() {
    	return "Face" + number;
    }
}
