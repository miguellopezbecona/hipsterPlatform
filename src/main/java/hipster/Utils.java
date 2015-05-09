package hipster;

import es.usc.citius.hipster.util.graph.HashBasedHipsterDirectedGraph;
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

public class Utils implements Constants{
    public static HashBasedHipsterDirectedGraph initializeGraph(List<Link> list){
        HashBasedHipsterDirectedGraph g = new HashBasedHipsterDirectedGraph<>();
        for(Link l : list)
            g.connect(Integer.toString(l.getSource()), Integer.toString(l.getTarget()), l.getWeight());
        
        return g;
    }

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
     * @return Hash
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
     * @param is - The InputStream object to be copies
     * @return A copy of the object
     */
    public static InputStream copyInputStream(InputStream is){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[CHUNK_SIZE];
        int len;
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

    /** Returns a list containing the next node and the ones to be expanded
     * depending on the type of iterator
     */
    public static List<String> handleIterator(Iterator it, String goal){
        List<String> ret = new ArrayList<>();
        String nextNode;
        
        if(it instanceof AStar.Iterator){
            AStar.Iterator aux = (AStar.Iterator) it;
            
            if(aux.hasNext()){
                nextNode = aux.next().state().toString();
                ret.add(nextNode);
                ret.addAll(aux.getOpen().keySet());
                return ret;
            }
            else
                return null;
        } else if(it instanceof BreadthFirstSearch.Iterator){
            BreadthFirstSearch.Iterator aux = (BreadthFirstSearch.Iterator) it;
            
            if(aux.hasNext()){
                nextNode = aux.next().state().toString();
                ret.add(nextNode);
                Queue<WeightedNode> q = aux.getQueue();
                for(WeightedNode n : q)
                    ret.add(n.state().toString());
                if(nextNode.equals(goal))      
                    it = null;
                return ret;
            }
            else
                return null;
        } else if(it instanceof DepthFirstSearch.Iterator){
            DepthFirstSearch.Iterator aux = (DepthFirstSearch.Iterator) it;
            
            if(aux.hasNext()){
                nextNode = aux.next().state().toString();
                ret.add(nextNode);
                if(nextNode.equals(goal))      
                    it = null;
                return ret;
            }
            else
                return null;
        } else if(it instanceof BellmanFord.Iterator){
            BellmanFord.Iterator aux = (BellmanFord.Iterator) it;
            
            if(aux.hasNext()){
                nextNode = aux.next().state().toString();
                ret.add(nextNode);
                if(nextNode.equals(goal))      
                    it = null;
                return ret;
            }
            else
                return null;
        } else
            return null;
    }
    
}
