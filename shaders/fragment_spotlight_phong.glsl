
out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;

// Light properties
uniform vec3 lightPos;
uniform vec3 lightIntensity;
uniform vec3 ambientIntensity;

uniform vec3 lightPosSpot;
uniform float attenuation;
uniform vec3 coneDirection;
uniform float coneAngle;
uniform vec3 lightIntensitySpot;

// Material properties
uniform vec3 ambientCoeff;
uniform vec3 diffuseCoeff;
uniform vec3 specularCoeff;
uniform float phongExp;

uniform sampler2D tex;

in vec4 viewPosition;
in vec3 m;

in vec2 texCoordFrag;

void main()
{
    // Compute the s, v and r vectors for directional light
    vec3 s = normalize(view_matrix*vec4(lightPos,0)).xyz;
    vec3 v = normalize(-viewPosition.xyz);
    vec3 r = normalize(reflect(-s,m));

    vec3 ambient = ambientIntensity*ambientCoeff; // no need to reset ambient
    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(m,s), 0.0);
    vec3 specular;

    // Only show specular reflections for the front face
    if (dot(m,s) > 0)
        specular = max(lightIntensity*specularCoeff*pow(dot(r,v),phongExp), 0.0);
    else
        specular = vec3(0);
        
    // spotlight
    // Compute the s, v and r vectors
    vec3 sSpot = normalize(view_matrix*vec4(lightPosSpot,1) - viewPosition).xyz;
    vec3 vSpot = normalize(-viewPosition.xyz);
    vec3 rSpot = normalize(reflect(-sSpot,m));
  	
  	// distance attenuation extension, not working  
//    float distanceToLight = length(view_matrix*vec4(lightPosSpot,1) - viewPosition);
//    float a = 1.0 / (1.0 + attenuation * pow(distanceToLight, 2));

    vec3 ambientSpot = ambientIntensity*ambientCoeff; // let ambient intensity to be the same
    vec3 diffuseSpot = max(lightIntensitySpot*diffuseCoeff*dot(m,sSpot), 0.0);
    vec3 specularSpot;

    // Only show specular reflections for the front face
    if (dot(m,sSpot) > 0)
        specularSpot = max(lightIntensitySpot*specularCoeff*pow(dot(rSpot,vSpot),phongExp), 0.0);
    else
        specularSpot = vec3(0);
       
    float a = attenuation;
    //cone restrictions (affects attenuation)
   	float lightToSurfaceAngle = degrees(acos(dot(sSpot, normalize(coneDirection))));
   	if(lightToSurfaceAngle > coneAngle){
        a = 0.0;
    } else {
    	a = pow(cos(radians(lightToSurfaceAngle)), a);
    	float distanceToLight = length(view_matrix*vec4(lightPosSpot,1) - viewPosition);
    	a = 1.0 / (1.0 + a * pow(distanceToLight, 2));
    }
    
    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);
    vec4 ambientAndDiffuseSpot = a*vec4(ambientSpot + diffuseSpot, 1);

	// sum all lights to get final
    outputColor = ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1)
    			+ ambientAndDiffuseSpot*input_color*texture(tex, texCoordFrag) + a*vec4(specularSpot, 1);
}
