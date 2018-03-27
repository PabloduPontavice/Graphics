//Pablo du Pontavice
//260674100
// mode3 - ambient and Lambertian and specular lighting

varying vec3 N;  // surface normal in camera 
varying vec3 v;  // surface fragment location in camera 
 
void main(void) {

	// TODO: OBJECTIVE 3: compute ambient, Lambertian, and Specular lighting


    float cosAngle = max(0, dot(N, normalize(gl_LightSource[0].position.xyz - v)));
	
	vec4 diffuseLambert = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse * cosAngle;
   
   	vec4 ambientLambert = gl_FrontLightProduct[0].ambient;

	vec3 normalV = normalize(-v);
	
	vec3 H = normalize(normalV + normalize(gl_LightSource[0].position.xyz - v));
	
	
	float maxWanted = pow(max(0, dot(N, H)), gl_FrontMaterial.shininess);

   	vec4 specularLambert = gl_FrontMaterial.specular * gl_LightSource[0].specular * maxWanted;
   
   	gl_FragColor = ambientLambert + diffuseLambert + specularLambert;
    
    //gl_FragColor = vec4(v.xyz,1);
}