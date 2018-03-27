//Pablo du Pontavice
//260674100
// mode 1 - ambient lighting

void main(void) {
	//equivalent to g.... = glFrontMaterial.ambient * glLightSource[0].ambient
   gl_FragColor = gl_FrontLightProduct[0].ambient; // TODO: Objective 1: set the ambient light colour
   
}