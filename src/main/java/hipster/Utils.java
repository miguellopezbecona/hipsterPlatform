package hipster;

import es.usc.citius.hipster.graph.HashBasedHipsterDirectedGraph;
import es.usc.citius.hipster.algorithm.AStar;
import es.usc.citius.hipster.algorithm.BellmanFord;
import es.usc.citius.hipster.algorithm.BreadthFirstSearch;
import es.usc.citius.hipster.algorithm.DepthFirstSearch;
import es.usc.citius.hipster.model.impl.WeightedNode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class Utils implements Constants {

    /**
     * Builds a HashBasedHipsterDirectedGraph object from a list of links
     * @param list - Data source
     * @returns The equivalent HashBasedHipsterDirectedGraph object
     */
    public static HashBasedHipsterDirectedGraph initializeGraph(List<Link> list){
        HashBasedHipsterDirectedGraph g = HashBasedHipsterDirectedGraph.create();
        for(Link l : list){
            // Adds source and target nodes. It doesn't matter if they already exist
            g.add(Integer.toString(l.getSource()));
            g.add(Integer.toString(l.getTarget()));

            // Adds the link
            g.connect(Integer.toString(l.getSource()), Integer.toString(l.getTarget()), l.getWeight());
        }
        
        return g;
    }

    /**
     * Obtains the available example graphs in the system
     * @return A string list containing example graphs' filenames
     */
    public static List<String> obtainAvailableGraphs(){
        List<String> list = new ArrayList<>();

        File folder = new File(GRAPH_BASE_PATH + GRAPH_EXAMPLES_FOLDER);
        for (File file : folder.listFiles())
            list.add(file.getName());

        return list;
    }

    /**
     * Converts from bytes to String (using hex values)
     * @param Bytes to convert from
     * @return Converted string
     */
    private static String toHexadecimal(byte[] digest){
        String hash = "";
        for(byte aux : digest) {
            int b = aux & 0xff;
            if (Integer.toHexString(b).length() == 1) hash += "0";
            hash += Integer.toHexString(b);
        }
        return hash;
    }

    /**
     * Generates a hash from a graph's content
     * @param is - InputStream which holds the file
     * @return Associated hash from file's content
     */
    public static String generateHash(InputStream is){
        byte[] buffer = new byte[CHUNK_SIZE];
        int numRead;
        is.mark(Integer.MAX_VALUE);

        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);

            do {
                numRead = is.read(buffer);
                if (numRead > 0)
                    md.update(buffer, 0, numRead);
            } while (numRead != -1);
            is.reset(); // Necessary as it will be used to save its content as well

            return toHexadecimal(md.digest());
        } catch (NoSuchAlgorithmException | IOException ex) {
            return null;
        }
    }

    /**
     * Copies an InputStream object in order to reuse it when reset is not possible.
     * This happens in the XML parser, as it automatically closes the source and is
     * necessary to reuse it.
     * @param is - The InputStream object to be copied
     * @return A copy of the object
     */
    public static InputStream copyInputStream(InputStream is){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[CHUNK_SIZE];
        int len;
        is.mark(Integer.MAX_VALUE);

        try {
            while ((len = is.read(buffer)) > -1 )
                baos.write(buffer, 0, len);
            baos.flush();
            is.reset();
        } catch (IOException e) {
            return null;
        }
        return new ByteArrayInputStream(baos.toByteArray()); 
    }

    /**
     *  Advances one step in the iterator and returns useful data.
     *  The handling is different depending on the type of iterator
     *  @param it - The iterator to be processed
     *  @param goal - Search's goal
     *  @return A string list containing:
     *  If the search ended or not (field 0)
     *  The next node (field 1) and its updated cost (field 2)
     *  Possible nodes to be expanded (fields 2 onwards if available)
     */
    public static List<String> handleIterator(Iterator it, String goal){
        List<String> ret = new ArrayList<>();
        WeightedNode nextNode;
        String nextState;
        String cost;
        String isEndToken = "N";
        
        if(it instanceof AStar.Iterator){
            AStar.Iterator aux = (AStar.Iterator) it;
            
            if(aux.hasNext()){
                nextNode = (WeightedNode) aux.next();
                nextState = nextNode.state().toString();
                cost = nextNode.getScore().toString();

                // End reached
                if(nextState.equals(goal)){    
                    it = null;
                    isEndToken = "Y";
                }
                ret.add(isEndToken);
                ret.add(nextState);
                ret.add(cost);
                ret.addAll(aux.getOpen().keySet());
                return ret;
            }
            else
                return null;
        } else if(it instanceof BreadthFirstSearch.Iterator){
            BreadthFirstSearch.Iterator aux = (BreadthFirstSearch.Iterator) it;
            
            if(aux.hasNext()){
                nextNode = (WeightedNode) aux.next();
                nextState = nextNode.state().toString();
                cost = nextNode.getCost().toString();

                // End reached
                if(nextState.equals(goal)){    
                    it = null;
                    isEndToken = "Y";
                }
                ret.add(isEndToken);
                ret.add(nextState);
                ret.add(cost);
                Queue<WeightedNode> q = aux.getQueue();
                for(WeightedNode n : q)
                    ret.add(n.state().toString());

                return ret;
            }
            else
                return null;
        } else if(it instanceof DepthFirstSearch.Iterator){
            DepthFirstSearch.Iterator aux = (DepthFirstSearch.Iterator) it;
            
            if(aux.hasNext()){
                nextNode = (WeightedNode) aux.next();
                nextState = nextNode.state().toString();
                cost = nextNode.getCost().toString();

                // End reached
                if(nextState.equals(goal)){    
                    it = null;
                    isEndToken = "Y";
                }
                ret.add(isEndToken);
                ret.add(nextState);
                ret.add(cost);
                return ret;
            }
            else
                return null;
        } else if(it instanceof BellmanFord.Iterator){
            BellmanFord.Iterator aux = (BellmanFord.Iterator) it;
            
            if(aux.hasNext()){
                nextNode = (WeightedNode) aux.next();
                nextState = nextNode.state().toString();
                cost = nextNode.getCost().toString();

                // End reached when the iterator is empty after retrieving last element
                if(!aux.hasNext()){    
                    it = null;
                    isEndToken = "Y";
                }
                ret.add(isEndToken);
                ret.add(nextState);
                ret.add(cost);
                return ret;
            }
            else
                return null;
        } else
            return null;
    }
    
}
