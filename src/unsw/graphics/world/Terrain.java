package unsw.graphics.world;



import java.awt.Color;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Point2DBuffer;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private int width;
    private int depth;
    private float[][] altitudes;
    private List<Tree> trees;
    private List<Road> roads;
    private Vector3 sunlight;
    private Point3DBuffer vertexBuffer;
    private IntBuffer indicesBuffer;
    private Texture texture;
    private TriangleMesh mesh;
    

    /**
     * Contains the normals for all vertices.
     */
    private Point3DBuffer normals;

    /**
     * Contains the texture coordinates for all vertices.
     */
    private Point2DBuffer texCoords;

    /**
     * Contains indices into the buffer of vertices and normals. Each set of 3
     * indices forms a triangle.
     */
    private IntBuffer indices;

    /**
     * The name of the vertex buffer according to OpenGL
     */
    private int verticesName;
    
    /**
     * The name of the normal buffer according to OpenGL
     */
    private int normalsName;
    
    /**
     * The name of the normal buffer according to OpenGL
     */
    private int texCoordsName;

    /**
     * The name of the indices buffer according to OpenGL
     */
    private int indicesName;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth, Vector3 sunlight) {
        this.width = width;
        this.depth = depth;
        altitudes = new float[width][depth];
        trees = new ArrayList<Tree>();
        roads = new ArrayList<Road>();
        this.sunlight = sunlight;
    }

    public List<Tree> trees() {
        return trees;
    }

    public List<Road> roads() {
        return roads;
    }

    public Vector3 getSunlight() {
        return sunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        sunlight = new Vector3(dx, dy, dz);      
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return altitudes[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, float h) {
        altitudes[x][z] = h;
    }
    

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * @param x
     * @param z
     * @return
     */
    public float altitude(float x, float z) {
        float altitude = 0;
        
        System.out.println("Received these x, z in altitude " + x + " " + z);
        // TODO: Implement this
        
        /**
         * 
         * (x0,0,z0)  (x1,0.5,z0)
			   +-----+
			   |    /|
			   |  /  |
			   |/    |
			   +-----+
			(x0,0,z1)  (x1,0.3,z1)
         */
        
        
//        if(x<0 || x>=width ||z<0 || z>=depth) {
        if(x<0 || x>=width ||z<0 || z>=width) {
        	return altitude;
        }
        
        float x_remain = x % 1;
        float z_remain = z % 1;
        int x0 = (int) x;
        int z0 = (int) z;
        System.out.println("x0, z0," + x0 + " " + z0);
        int x1 = x0<width-1?x0+1:x0;
        int z1 = z0<depth-1?z0+1:z0;
        float a0 = ((1-x_remain)*altitudes[x0][z0]) + (x_remain*altitudes[x1][z0]);
        float a1 = ((1-x_remain)*altitudes[x0][z1]) + (x_remain*altitudes[x1][z1]);
        altitude = ((1-z_remain)*a0)+(z_remain*a1);
        System.out.println("Altitude is " + altitude);
        return altitude;
    }
    
    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     * @throws IOException 
     */
    public void addTree(float x, float z) {
        float y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        trees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(float width, List<Point2D> spine) {
        Road road = new Road(width, spine);
        roads.add(road);        
    }
    
  
    public int getWidth() {
    	return width;
    }
    
    public int getDepth() {
    	return depth;
    }
    
    public void init(GL3 gl) {
        // Generate the names for the buffers.
        int dx, dz, index_vert, cnt;
        
        // vertex
        // ==========================================================
        List<Point3D> p_list = new ArrayList<Point3D>();

        for(dz=0; dz<width; dz++){
        	for(dx=0; dx<width; dx++){
//        		p_list.add(new Point3D(dx, altitudes[dz][dx], dz));
        		p_list.add(new Point3D(dx, altitudes[dx][dz], dz));
        	}
        }
       
//        vertexBuffer = new Point3DBuffer(p_list);
        
        // texture
        // ==========================================================
        List<Point2D> t_list = new ArrayList<Point2D>();
        for(dz=0; dz<width; dz++){
        	for(dx=0; dx<width; dx++){
        		t_list.add(new Point2D((float)dz/(depth-1), (float)dx/(width-1)));
        	}
        }
        
//        texCoords = new Point2DBuffer(t_list);     
        
        // indices
        // ==========================================================
        int[] i_list = new int[3*2*(width-1)*(depth-1)];
        
        index_vert=0;
        cnt = 0;
        
        for(dz=0; dz<width-1; dz++){
        	for(dx=0; dx<width-1; dx++){
        		index_vert = dz*width+dx;
        		i_list[cnt++] = (index_vert);
        		i_list[cnt++] = (index_vert+width);
        		i_list[cnt++] = (index_vert+1);
        		
        		i_list[cnt++] = (index_vert+1);
        		i_list[cnt++] = (index_vert+width);
        		i_list[cnt++] = (index_vert+width+1);
        	}
        }
        
        Integer[] index_list = ArrayUtils.toObject(i_list);
        List<Integer> indice_points = Arrays.asList(index_list);
        mesh = new TriangleMesh(p_list, indice_points, true, t_list);
        mesh.init(gl);
        
//        indicesBuffer = GLBuffers.newDirectIntBuffer(i_list);
//        
//        int[] names = new int[4];
//        gl.glGenBuffers(4, names, 0);
//        
//        verticesName = names[0];
//        indicesName = names[1];
//        normalsName = names[2];
//        texCoordsName = names[3];
//        
//        // Copy the data for the vertices
//        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
//        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 3 * Float.BYTES, 
//        		vertexBuffer.getBuffer(),GL.GL_STATIC_DRAW);
//        
//        if (normals != null) {
//            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, normalsName);
//            gl.glBufferData(GL.GL_ARRAY_BUFFER,
//                    normals.capacity() * 3 * Float.BYTES, normals.getBuffer(),
//                    GL.GL_STATIC_DRAW);
//        }
//        
//        if (texCoords != null) {
//            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordsName);
//            gl.glBufferData(GL.GL_ARRAY_BUFFER,
//                    texCoords.capacity() * 2 * Float.BYTES, texCoords.getBuffer(),
//                    GL.GL_STATIC_DRAW);
//        }
//
//        if (indicesBuffer != null) {
//            // Copy the data for the indices
//            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
//            gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * Integer.BYTES,
//                    indicesBuffer, GL.GL_STATIC_DRAW);
//        }
        
        // texture
        // ========================================
        texture = new Texture(gl, "res/textures/grass.bmp", "bmp", true);
        
//		Shader shader = new Shader(gl, "shaders/vertex_tex_3d.glsl", "shaders/fragment_tex_3d.glsl");
//		Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl", "shaders/fragment_tex_phong.glsl");
        Shader shader = new Shader(gl, "shaders/vertex_dir_phong.glsl", "shaders/fragment_dir_phong.glsl");	// lighting
//        Shader shader = new Shader(gl, "shaders/vertex_phong.glsl", "shaders/fragment_phong.glsl");
		
        shader.use(gl);
        
        // lighting
     // ===========================================
//        Shader.setPoint3D(gl, "lightPos", new Point3D(sunlight.getX(), sunlight.getY(), sunlight.getZ()));
//        
//        Shader.setColor(gl, "lightIntensity", Color.WHITE);
//        Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
//        
//        // Set the material properties
//        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
//        Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
//        Shader.setColor(gl, "specularCoeff", new Color(0.8f, 0.8f, 0.8f));
//        Shader.setFloat(gl, "phongExp", 16f);
        
        
        // texture
        // ===========================================
//        texture = new Texture(gl, "res/textures/grass.bmp", "bmp", true);
//        Shader.setInt(gl, "tex", 0);
//        gl.glActiveTexture(GL.GL_TEXTURE0);
//        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
        
    }
    
    public void drawTrees(GL3 gl, CoordFrame3D frame) {
    	for (Tree t: trees) {
    		t.draw(gl, frame);
    	}
    }
    
    
    public void draw(GL3 gl, CoordFrame3D frame) {
    	
    	// Bind Texture
    	Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture.getId());
        
        Shader.setPenColor(gl, Color.WHITE);
        
        Shader.setModelMatrix(gl, frame.getMatrix());
        
        // Set light properties
        Shader.setPoint3D(gl, "lightPos", new Point3D(sunlight.getX(), sunlight.getY(), sunlight.getZ()));
        Shader.setColor(gl, "lightIntensity", Color.WHITE);
        Shader.setColor(gl, "ambientIntensity", new Color(0.5f, 0.5f, 0.5f));
        
        // Set the material properties
        Shader.setColor(gl, "ambientCoeff", Color.WHITE);
        Shader.setColor(gl, "diffuseCoeff", new Color(0.9f, 0.9f, 0.9f));
        Shader.setColor(gl, "specularCoeff", new Color(0.0f, 0.0f, 0.0f));
        Shader.setFloat(gl, "phongExp", 16f);
    	
        mesh.draw(gl, frame);
//        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, indicesName);
//
//        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, verticesName);
//        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);
//        if (normals != null) {
//            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, normalsName);
//            gl.glVertexAttribPointer(Shader.NORMAL, 3, GL.GL_FLOAT, false, 0, 0);
//        }
//        if (texCoords != null) {
//            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, texCoordsName);
//            gl.glVertexAttribPointer(Shader.TEX_COORD, 2, GL.GL_FLOAT, false, 0, 0);
//        }
//        if (indicesBuffer != null) {
//            gl.glDrawElements(GL3.GL_TRIANGLES, indicesBuffer.capacity(),
//                    GL.GL_UNSIGNED_INT, 0);
//        	
//        } else {
//            gl.glDrawArrays(GL3.GL_TRIANGLES, 0, vertexBuffer.capacity());
//        }
        
        drawTrees(gl, frame);
    }
    
    public void draw(GL3 gl) {
        draw(gl, CoordFrame3D.identity());
    }
    
    
}
