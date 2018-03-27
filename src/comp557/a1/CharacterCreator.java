//Pablo du Pontavice, 260674100

package comp557.a1;

public class CharacterCreator {

	static public String name = "Crazy Snowmanoid, PABLO DU PONTAVICE, 260674100";
	
	/** 
	 * Creates a character.
	 * @return root DAGNode
	 */
	static public DAGNode create() {
		// TODO: use for testing, and ultimately for creating a character
		// Here we just return null, which will not be very interesting, so write
		// some code to create a character and return the root node.
		FreeJoint body = new FreeJoint("body");
		BallJoint leftShoulder = new BallJoint("leftShoulder", -2f, 2f, 1f, -90.0, 90.0, 1f);
		body.add(leftShoulder);
		
		BallJoint rightShoulder = new BallJoint("rightShoulder", 2f, 2f, 1f, -90.0, 90.0, 1f);
		body.add(rightShoulder);
		
		BallJoint lowerBody = new BallJoint("lowerBody", 0, -2.5f, 0, -180.0, 180.0, 1.8f);
		body.add(lowerBody);
		
		LowerHalf lowerHalf = new LowerHalf("lowerHalf");
		lowerBody.add(lowerHalf);
		
		Arm leftArm = new Arm("leftArm");
		leftShoulder.add(leftArm);
		
		Arm rightArm = new Arm("rightArm");
		rightShoulder.add(rightArm);
		
		HingeJoint leftElbow = new HingeJoint("leftElbow", 0, 0, 5f, 1f, 0, 0, -360, 360);
		leftArm.add(leftElbow);
		
		HingeJoint rightElbow = new HingeJoint("rightElbow", 0, 0, 5f, 1f, 0, 0, -360, 360);
		rightArm.add(rightElbow);
		
		ForeArm leftForeArm = new ForeArm("leftForeArm");
		leftElbow.add(leftForeArm);
		
		ForeArm rightForeArm = new ForeArm("rightForeArm");
		rightElbow.add(rightForeArm);
		
		Head head = new Head("head");
		body.add(head);
		
		return body;
	}
}
