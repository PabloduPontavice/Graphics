//Pablo du Pontavice
//260674100

package comp557lw.a3;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Half edge data structure.
 * Maintains a list of faces (i.e., one half edge of each) to allow for easy display of geometry.
 * 
 * @author Pablo du Pontavice 260674100
 */
public class HEDS {

    /**
     * List of faces 
     */
    List<Face> faces = new ArrayList<Face>();
        
    /**
     * Constructs an empty mesh (used when building a mesh with subdivision)
     */
    public HEDS() {
        // do nothing
    }
        
    /**
     * Builds a half edge data structure from the polygon soup   
     * @param soup
     */
    public HEDS( PolygonSoup soup ) {
        
        // TODO: Objective 1: create the half edge data structure from a polygon soup
    	
    	Map<Tuple, HalfEdge> twins = new HashMap<>();
    	
    	for(int[] face : soup.faceList) {
        	if(face.length < 3) {
        		System.out.println("The face " + face + " is degenarate skipping.");
        		continue;
        	}
			int vertexIndexPrev = face[face.length - 1];
			HalfEdge prev = null, first = null, curr = null;
        	for(int vertexIndex : face) {
				curr = new HalfEdge();
				curr.head = soup.vertexList.get(vertexIndex);
				if(prev != null) {
					prev.next = curr;
				}
				else {
					// first doesn't change
					first = curr;
				}
				
				// If twins contains inverse, otherwise add it
				final Tuple hash = new Tuple(vertexIndexPrev, vertexIndex);
				HalfEdge twin = twins.get(hash);
				if(twin == null) {
					twins.put(hash, curr);
				}
				else {
        			assert(twin.twin == null);
        			twins.remove(hash);
					curr.twin = twin;
					twin.twin = curr;
				}

				// update for the next iteration
				vertexIndexPrev = vertexIndex;
				prev = curr;
        	}
        	assert(first != null && curr != null && curr.next == null);
        	curr.next = first;
        	faces.add(new Face(first));
        }
        if(!twins.isEmpty()) {
        	System.out.println("Some edges are with unpaired twins. I don't know why.");
        	twins.clear();
        }
        
    }
    
    /**
     * Draws the half edge data structure by drawing each of its faces.
     * Per vertex normals are used to draw the smooth surface when available,
     * otherwise a face normal is computed. 
     * @param drawable
     */
    public void display() {
        // note that we do not assume triangular or quad faces, so this method is slow! :(     
        Point3d p;
        Vector3d n;        
        for ( Face face : faces ) {
            HalfEdge he = face.he;
            if ( he.head.n == null ) { // don't have per vertex normals? use the face
                glBegin( GL_POLYGON );
                n = he.leftFace.n;
                glNormal3d( n.x, n.y, n.z );
                HalfEdge e = he;
                while(true) {
                    p = e.head.p;
                    glVertex3d( p.x, p.y, p.z );
                    e = e.next;
                    
                    if( e != he ) {
                    	continue;
                    }
                    else {
                    	break;
                    }
                }
                glEnd();
            } else {
                glBegin( GL_POLYGON );                
                HalfEdge e = he;
                while(true) {
                    p = e.head.p;
                    n = e.head.n;
                    glNormal3d( n.x, n.y, n.z );
                    glVertex3d( p.x, p.y, p.z );
                    e = e.next;
                    
                    if( e != he ) {
                    	continue;
                    }
                    else {
                    	break;
                    }
                }
                glEnd();
            }
        }
    }
    
    /** 
     * Draws all child vertices to help with debugging and evaluation.
     * (this will draw each points multiple times)
     * @param drawable
     */
    public void drawChildVertices() {
    	glDisable( GL_LIGHTING );
        glPointSize(8);
        glBegin( GL_POINTS );
        for ( Face face : faces ) {
            if ( face.child != null ) {
                Point3d p = face.child.p;
                glColor3f(0,0,1);
                glVertex3d( p.x, p.y, p.z );
            }
            HalfEdge loop = face.he;
            while(true) {
                if ( loop.head.child != null ) {
                    Point3d p = loop.head.child.p;
                    glColor3f(1,0,0);
                    glVertex3d( p.x, p.y, p.z );
                }
                if ( loop.child1 != null && loop.child1.head != null ) {
                    Point3d p = loop.child1.head.p;
                    glColor3f(0,1,0);
                    glVertex3d( p.x, p.y, p.z );
                }
                loop = loop.next;
                
                
                if( loop != face.he ) {
                	continue;
                }
                else {
                	break;
                }
            }
        }
        glEnd();
        glEnable( GL_LIGHTING );
    }
    
    // found online
    final class Tuple {
    	
    	private final int a, b;
    	
    	public Tuple(int a, int b) { this.a = a; this.b = b; }
   
    	public final boolean equals(Object o) {
    		if(this == o || this == null || getClass() != o.getClass()) return false;
    		Tuple x = (Tuple) o;
    		return this.a == x.b && this.b == x.a;
    	}
    	// must be symmetric!
    	public int hashCode() {
    		return a + b;
    	}
        
    }
}
