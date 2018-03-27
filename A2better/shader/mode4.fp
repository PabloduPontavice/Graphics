//Pablo du Pontavice
//260674100
// mode 4 - ambient and Lambertian and Specular with shadow map

uniform sampler2D shadowMap; 
uniform float sigma;

varying vec3 N;  // surface normal in camera 
varying vec3 v;  // surface fragment location in camera 
varying vec4 vL; // surface fragment location in light view NDC
 
void main(void) {

	// TODO: Objective 6: ambient, Labertian, and Specular with shadow map.
	// Note that the shadow map lookup should only modulate the Lambertian and Specular component.

	
    float cosAngle = max(0, dot(N, normalize(gl_LightSource[0].position.xyz - v)));
	
	vec4 diffuseLambert = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse * cosAngle;
   
   	vec4 ambientLambert = gl_FrontLightProduct[0].ambient;

	vec3 normalV = normalize(-v);
	
	vec3 H = normalize(normalV + normalize(gl_LightSource[0].position.xyz - v));
	
	
	float maxWanted = pow(max(0, dot(N, H)), gl_FrontMaterial.shininess);

   	vec4 specularLambert = gl_FrontMaterial.specular * gl_LightSource[0].specular * maxWanted;
   
   	vec4 vLNorm = vL/vL.w;
   	vec4 ShadowCoords = ( vLNorm + vec4(1,1,1,1) ) * 0.5f;
   	float textureDepth = texture( shadowMap, ShadowCoords.xy ).z;
   	
   	float shadowCoeff = ( textureDepth + sigma < ShadowCoords.z ) ? 0.0f : 1.0f;
   	
   
   	gl_FragColor = ambientLambert + shadowCoeff*( diffuseLambert + specularLambert );

}