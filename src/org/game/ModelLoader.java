package org.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Hashtable;
import java.util.UUID;
import java.util.Vector;

public class ModelLoader {
    
    public static final UUID NAME = UUID.randomUUID();
    public static final UUID POSITION = UUID.randomUUID();
    public static final UUID AMBIENT_COLOR = UUID.randomUUID();
    public static final UUID DIFFUSE_COLOR = UUID.randomUUID();
    public static final UUID TEXTURE = UUID.randomUUID();

    public static class OBJVertex {
        public final Vec3 position = new Vec3();
        public final Vec2 textureCoordinate = new Vec2();
        public final Vec3 normal = new Vec3();
    }

    public static class OBJModelMeshPart {
        public String name = "";
        public Texture texture = null;
        public final AABB bounds = new AABB();
        public final Vector<OBJVertex> vertices = new Vector<>();
        public final Vector<int[]> faces = new Vector<>();
        public int indexCount = 0;

        public void calcBounds() {
            bounds.clear();
            for(OBJVertex vertex : vertices) {
                bounds.union(vertex.position);
            }
        }
    }

    public static class OBJModelMesh {
        public String name = "";
        public final Vec3 position = new Vec3();
        public final AABB bounds = new AABB();
        public final Vector<OBJModelMeshPart> parts = new Vector<>();

        public void calcBounds() {
            bounds.clear();
            for(OBJModelMeshPart part : parts) {
                part.calcBounds();
                bounds.union(part.bounds);
            }
        }

        public void transform() {
            bounds.center(position);

            for(OBJModelMeshPart part : parts) {
                for(OBJVertex vertex : part.vertices) {
                    vertex.position.sub(position);
                }
            }
        }
    }

    public static class OBJModel {
        public String name = "";
        public final AABB bounds = new AABB();
        public final Vector<OBJModelMesh> meshes = new Vector<>();

        public void calcBounds() {
            bounds.clear();
            for(OBJModelMesh mesh : meshes) {
                mesh.calcBounds();
                bounds.union(mesh.bounds);
            }
        }

        public void transform() {
            for(OBJModelMesh mesh : meshes) {
                mesh.transform();
            }
        }
    }

    public static Model load(File file, ResourceManager resources) throws Exception {
        String[] lines = loadText(file).split("\\n+");
        OBJModel objModel = new OBJModel();
        OBJModelMesh iMesh = null;
        OBJModelMeshPart iPart = null;
        Vector<Vec3> vList = new Vector<>(10000);
        Vector<Vec2> tList = new Vector<>(10000);
        Vector<Vec3> nList = new Vector<>(10000);
        String material = "";
        Hashtable<String, String> textureNames = new Hashtable<>();
        Hashtable<String, Texture> textures = new Hashtable<>(); 

        for(String line : lines) {
            String tLine = line.trim();
            String[] tokens = tLine.split("\\s+");

            if(tLine.startsWith("mtllib ")) {
                String[] mLines = loadText(new File(file.getParentFile(), tLine.substring(6).trim())).split("\\n+");
                String m = "";

                for(String mLine : mLines) {
                    String tmLine = mLine.trim();
                    
                    if(tmLine.startsWith("newmtl ")) {
                        m = tmLine.substring(6).trim();
                        textureNames.put(m, "");
                    } else if(tmLine.startsWith("map_Kd ")) {
                        textureNames.put(m, tmLine.substring(6).trim());
                    }
                }
            } else if(tLine.startsWith("usemtl ")) {
                material = tLine.substring(6).trim();
                if(iMesh == null) {
                    iMesh = new OBJModelMesh();
                    objModel.meshes.add(iMesh);
                }
                iPart = new OBJModelMeshPart();
                iPart.name = material;
                loadTexture(file, textureNames.get(material), textures, resources, iPart);
                iMesh.parts.add(iPart);
            } else if(tLine.startsWith("o ")) {
                iMesh = new OBJModelMesh();
                iMesh.name = tLine.substring(1).trim();
                objModel.meshes.add(iMesh);
                iPart = new OBJModelMeshPart();
                iPart.name = "";
                loadTexture(file, textureNames.get(material), textures, resources, iPart);
                iMesh.parts.add(iPart);
            } else if(tLine.startsWith("v ")) {
                vList.add(new Vec3(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
            } else if(tLine.startsWith("vt ")) {
                tList.add(new Vec2(Float.parseFloat(tokens[1]), 1 - Float.parseFloat(tokens[2])));
            } else if(tLine.startsWith("vn ")) {
                nList.add(new Vec3(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
            } else if(tLine.startsWith("f ")) {
                if(iMesh == null) {
                    iMesh = new OBJModelMesh();
                    objModel.meshes.add(iMesh);
                } 
                if(iPart == null) {
                    iPart = new OBJModelMeshPart();
                    iPart.name = material;
                    loadTexture(file, textureNames.get(material), textures, resources, iPart);
                    iMesh.parts.add(iPart);
                }

                int[] face = new int[tokens.length - 1];
                int i = 0;
                int n = iPart.vertices.size();

                for(int j = 1; j != tokens.length; j++) {
                    String[] iTokens = tokens[j].split("/");
                    int iV = Integer.parseInt(iTokens[0]) - 1;
                    int iT = Integer.parseInt(iTokens[1]) - 1;
                    int iN = Integer.parseInt(iTokens[2]) - 1;
                    OBJVertex vertex = new OBJVertex();

                    vertex.position.set(vList.get(iV));
                    vertex.textureCoordinate.set(tList.get(iT));
                    vertex.normal.set(nList.get(iN));

                    iPart.vertices.add(vertex);
                    face[i] = n + i;
                    i++;
                }
                iPart.indexCount += (face.length - 2) * 3;
                iPart.faces.add(face);
            }
        }

        objModel.calcBounds();
        objModel.transform();
        objModel.calcBounds();

        Model model = resources.manage(new Model());

        model.put(NAME, file.getName());
        model.bounds.set(objModel.bounds);
        for(OBJModelMesh objMesh : objModel.meshes) {
            int icount = 0;

            for(OBJModelMeshPart objPart : objMesh.parts) {
                icount += objPart.indexCount;
            }
            if(icount != 0) {
                ModelMesh mesh = new ModelMesh();

                mesh.put(NAME, objMesh.name);
                mesh.put(POSITION, objMesh.position);
                mesh.put(AMBIENT_COLOR, new Vec4(0.2f, 0.2f, 0.2f, 1));
                mesh.put(DIFFUSE_COLOR, new Vec4(1, 1, 1, 1));
                mesh.bounds.set(objMesh.bounds);
                mesh.model.identity().translate(objMesh.position);
                mesh.modelIT.set(mesh.model).invert().transpose();

                model.addMesh(mesh);

                for(OBJModelMeshPart objPart : objMesh.parts) {
                    if(objPart.indexCount != 0) {
                        ModelMeshPart part = new ModelMeshPart(0, 0);

                        if(objPart.texture != null) {   
                            part.put(TEXTURE, objPart.texture);
                        }
                        part.put(NAME, objPart.name);
                        part.bounds.set(objPart.bounds);

                        part.vertexBuffer.begin(objPart.vertices.size() * BasicRenderer.VERTEX_STRIDE);
                        for(OBJVertex vertex : objPart.vertices) {
                            part.vertexBuffer.push(vertex.position);
                            part.vertexBuffer.push(vertex.textureCoordinate);
                            part.vertexBuffer.push(vertex.normal);
                        }
                        part.vertexBuffer.end(BufferUsage.STATIC);

                        part.vertexElementBuffer.begin(objPart.indexCount);
                        for(int[] face : objPart.faces) {
                            part.vertexElementBuffer.push(face);
                        }
                        part.vertexElementBuffer.end(BufferUsage.STATIC);

                        mesh.addPart(part);
                    }
                }
            }
        }
        return model;
    }

    private static String loadText(File file) throws IOException {
        LineNumberReader reader = null;
        String text = "";

        try {
            reader = new LineNumberReader(new InputStreamReader(new FileInputStream(file)));
            
            String line = null;

            while((line = reader.readLine()) != null) {
                text = text + line + "\n";
            }
        } finally {
            if(reader != null) {
                reader.close();
            }
        }
        return text;
    }

    private static void loadTexture(File objFile, String name, Hashtable<String, Texture> textures, ResourceManager resources, OBJModelMeshPart part) throws Exception {
        if(name != null) {
            if(!name.isEmpty()) {
                File texFile = new File(objFile.getParentFile(), name);

                if(texFile.exists()) {
                    if(!textures.containsKey(name)) {
                        textures.put(name, resources.manage(Texture.load(texFile)));
                    }
                    if(textures.containsKey(name)) {
                        part.texture = textures.get(name);
                    }
                }
            }
        }
    }
}
