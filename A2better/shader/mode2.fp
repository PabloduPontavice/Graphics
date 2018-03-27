//Pablo du Pontavice
//260674100
// mode 2 - ambient and Lambertian lighting

varying vec3 N;  // surface normal in camera 
varying vec3 v;  // surface fragment location in camera 
 
void main(void) {

	// TODO: Objective 2: compute ambient and Lambertial lighting
	
	float cosAngle = max(0, dot(N, normalize(gl_LightSource[0].position.xyz - v)));
	
	vec4 diffuseLambert = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse * cosAngle;
   
   vec4 ambientLambert = gl_FrontLightProduct[0].ambient;

   //gl_FragColor = gl_FrontLightProduct[0].diffuse( N.xyz*0.5+vec3(0.5,0.5,0.5), 1 );
   gl_FragColor = ambientLambert + diffuseLambert;
}