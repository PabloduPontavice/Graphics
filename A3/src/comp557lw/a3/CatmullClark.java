//Pablo du Pontavice
//260674100
package comp557lw.a3;

import javax.vecmath.Vector3d;

/**
 * Class implementing the Catmull-Clark subdivision scheme
 * 
 * @author TODO: Pablo du Pontavice, 260674100
 */
public class CatmullClark {

    /**
     * Subdivides the provided half edge data structure
     * @param heds
     * @return the subdivided mesh
     */
    public static HEDS subdivide( HEDS heds ) {
        HEDS heds2 = new HEDS();
        
        
        // TODO: Objectives 2,3,4: finish this method!!
        // you will certainly want to write lots of helper methods!
        
        for(Face face : heds.faces) {
            // objective 2
        	face.evenVertices();
        	// objective 3, part 1
        	face.makeChild();
        }
        	
        for(Face face : heds.faces) {
        	// objective 3, part 2
        	HalfEdge he = face.he;
        	 while(true) {
        		assert(he != null);
        		he.divideEdge();
        		if((he = he.next) != face.he) {
        			continue;
        		}
        		else {
        			break;
        		}
        	}
        }
        
        // objective 4
        heds.faces.forEach((Face f) -> f.subdivide(heds2));
        
        // objective 5
        // "Refer to Equation 4.1 on Page 70 of the siggraph 2000 course notes on subdividion surfaces" I don't see it
        for(Face face : heds2.faces) {
        	HalfEdge he = face.he;
        	// The greater the distance, the greater the contribution.
        	while(true) {
        		assert(he != null);
        		if(he.head.n == null) he.head.n = new Vector3d();
                Vector3d v1 = new Vector3d();
                Vector3d v2 = new Vector3d();
                Vector3d n = new Vector3d();
                v1.sub(he.head.p, he.prev().head.p);
                v2.sub(he.head.p, he.next.head.p);
                n.cross( v2,v1 );
                he.head.n.add(n);
                if((he = he.next) != face.he) {
                	continue;
                }
                else {
                	break;
                }
        	}
        }
        // normalization step
        for(Face face : heds2.faces) {
        	HalfEdge he = face.he;
        	while(true) {
                he.head.n.normalize();
                if((he = he.next) != face.he) {
                	continue;
                }
                else {
                	break;
                }
        	}
        }
        
        return heds2;        
    }
    
}
