package Flow;

import Core.Workspace;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/** A flow is the JSON file containing all the data structure of a workspace.
 * The web interface uses it, and only it, to create the view.
 *
 * Created by antoine on 26/05/2017.
 */
public class Flow implements FlowInterface {

    // Attributes
    private JSONObject flow = null;
    private Workspace owningWorkspace = null;
    private String componentsLibrary = "";
    // The below attributes have to be contained into the flow object.
    private String id = "";
    private String name = "";
    private String description = "";
    private String library = "";
    private ArrayList<Edge> edges = null;
    private ArrayList<Node> nodes = null;
    private ArrayList<Group> groups = null;

    // Constructor
    public Flow (Workspace workspace) {
        this.id = UUID.randomUUID().toString();
        this.owningWorkspace = workspace;
        this.flow = new JSONObject();

        this.edges = new ArrayList<Edge>();
        this.nodes = new ArrayList<Node>();
        this.groups = new ArrayList<Group>();

        componentsLibrary = owningWorkspace.getLibrary();
    }

    /** Constructor that creates a Flow from a JSONObject.
     * Use case : after server restart, re-create workspaces from the saved JSON files.
     *
     * @param source
     */
    public Flow (JSONObject source) {
        // TODO
    }

    /* =================================================================================================================
                                                    PUBLIC FUNCTIONS
       ===============================================================================================================*/

    public String serialize () {
        // Build the JSON file of the flow
        flow.put("id", id);
        flow.put("name", name);
        flow.put("library", library);
        flow.put("description", description);
        flow.put("edges", Utils.JSON.jsonArrayListToString(edges));
        flow.put("nodes", Utils.JSON.jsonArrayListToString(nodes));
        flow.put("groups", Utils.JSON.jsonArrayListToString(groups));

        // Return it as a JSON String to send it to frontend
        return flow.toJSONString();
    }

    public boolean addNode(String id, String component, JSONObject metadata, String graph) {
        Node n = new Node(id, component, metadata, graph, this);

        if(nodes.add(n)) return true;
        else return false;
    }

    public boolean removeNode(String id, String graph) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && nodeExist(id)) {
            // If so, retrieve the index of the node and remove it
            nodes.remove(indexOfNode(id));
            return true;
        } else {
             return false;
        }
    }

    public boolean renameNode(String from, String to, String graph) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && nodeExist(from)) {
            // If so, retrieve the node and modify its id
            Node n = nodes.get(indexOfNode(from));
            n.setId(to);
            return true;
        } else {
            return false;
        }
    }

    public boolean changeNode(String id, JSONObject metadata, String graph) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && nodeExist(id)) {
            // If so, retrieve the node and modify its id
            Node n = nodes.get(indexOfNode(id));
            n.setMetadata(metadata);
            return true;
        } else {
            return false;
        }
    }

    public boolean addEdge (JSONObject src, JSONObject tgt, JSONObject metadata, String graph) {
        String srcNodeId = (String) src.get("node");
        String tgtNodeId = (String) src.get("node");

        if(nodeExist(srcNodeId) && nodeExist(tgtNodeId) && graphExist(graph)) {
            Edge newEdge = new Edge(src, tgt, metadata, graph, this);
            edges.add(newEdge);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeEdge(String graph, JSONObject src, JSONObject tgt) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && edgeExist(src, tgt)) {
            // If so, retrieve the index of the edge and remove it
            edges.remove(indexOfEdge(src, tgt));
            return true;
        } else {
            return false;
        }
    }

    public boolean changeEdge(String graph, JSONObject metadata, JSONObject src, JSONObject tgt) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && edgeExist(src, tgt)) {
            // If so, retrieve the edge and modify its metadata
            Edge e = edges.get(indexOfEdge(src, tgt));
            e.setMetadata(metadata);
            return true;
        } else {
            return false;
        }
    }

    public boolean addInitial(String graph, JSONObject metadata, JSONObject src, JSONObject tgt) {
        // Not used for now
        return true;
    }

    public boolean removeInitial(String graph, JSONObject src, JSONObject tgt) {
        // Not used for now
        return true;
    }

    public boolean addInport(String name, String node, String port, JSONObject metadata, String graph) {
        // Not used for now
        return true;
    }

    public boolean removeInport(String name, String graph) {
        // Not used for now
        return true;
    }

    public boolean renameInport(String from, String to, String graph) {
        // Not used for now
        return true;
    }

    public boolean addOutport(String name, String node, String port, JSONObject metadata, String graph) {
        // Not used for now
        return true;
    }

    public boolean removeOutport(String name, String graph) {
        // Not used for now
        return true;
    }

    public boolean renameOutport(String from, String to, String graph) {
        // Not used for now
        return true;
    }

    public boolean addGroup(String name, JSONObject nodes, JSONObject metadata, String graph) {
        if(graphExist(graph)){
            Group g = new Group(name, nodes, metadata, graph, this);

            groups.add(g);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeGroup(String name, String graph) {
        if(graphExist(graph) && groupExist(name)) {
            groups.remove(indexOfGroup(name));
            return true;
        } else {
            return false;
        }
    }

    public boolean renameGroup(String from, String to, String graph) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && groupExist(from)) {
            // If so, retrieve the group and modify its name
            Group g = groups.get(indexOfGroup(from));
            g.setName(to);
            return true;
        } else {
            return false;
        }
    }

    public boolean changeGroup(String name, JSONObject metadata, String graph) {
        // Verify that the requested graph is the workspace
        if(graphExist(graph) && groupExist(name)) {
            // If so, retrieve the group and modify its metadata
            Group g = groups.get(indexOfGroup(name));
            g.setMetadata(metadata);
            return true;
        } else {
            return false;
        }
    }

    /* =================================================================================================================
                                                    GETTERS AND SETTERS
       ===============================================================================================================*/

    public String getComponentsLibrary() {
        return componentsLibrary;
    }

    public String getId() {
        return id;
    }

    /* =================================================================================================================
                                                    PRIVATE FUNCTIONS
       ===============================================================================================================*/

    private boolean nodeExist (String id) {
        // Go trough all the nodes and if it finds one with the given id return true, else return false
        for(int i = 0; i<nodes.size();i++){
            if(id.equals(this.nodes.get(i).getId())) return true;
        }

        return false;
    }

    private boolean edgeExist (JSONObject src, JSONObject tgt) {
        return indexOfEdge(src, tgt) != -1;
    }

    private boolean graphExist (String id) {
        if (this.id.equals(id)) return true;

        for (int i=0; i<groups.size(); i++) {
            if(id.equals(groups.get(i).getId())) return true;
        }

        return false;
    }

    private boolean groupExist (String name) {
        if (this.groups == null) return false;

        for (int i=0; i<groups.size(); i++) {
            if(groups.get(i).getName().equals(name)) return true;
        }

        return false;
    }

    private int indexOfEdge (JSONObject src, JSONObject tgt) {
        for(int i=0; i<edges.size(); i++) {
            if (edges.get(i).getSrc().equals(src) && edges.get(i).getTgt().equals(tgt)) return i;
        }

        return -1;
    }

    private int indexOfNode (String id) {
        for(int i=0; i<nodes.size(); i++) {
            if (nodes.get(i).getId().equals(id)) return i;
        }

        return -1;
    }

    private int indexOfGroup (String name) {
        for(int i=0; i<groups.size(); i++) {
            if (groups.get(i).getName().equals(name)) return i;
        }

        return -1;
    }

}
