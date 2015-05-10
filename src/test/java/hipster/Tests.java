package hipster;

import static org.junit.Assert.*;

import es.usc.citius.hipster.util.graph.HashBasedHipsterDirectedGraph;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;


public class Tests implements Constants{
    private static final String GOOD_JSON_FILE = GRAPH_BASE_PATH + GRAPH_EXAMPLES_FOLDER + "fewLinksDirected.json";
    private static InputStream goodJSONIS;

    @BeforeClass
    public static void setUp() throws Exception {
        try {
            goodJSONIS = new BufferedInputStream(new FileInputStream(GOOD_JSON_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void badGEXFFile() {
        assertNull("Incorrect graph",GEXFParser.getGraph(GOOD_JSON_FILE));
    }

    @Test
    public void hash() {
        // Obtained with: openssl sha1 graphs/examples/fewLinksDirected.json | cut -d " " -f 2
        String hash = "e2533784078910caf7f78d5e9aad040c91c532b7";

        String calculatedHash = Utils.generateHash(goodJSONIS);
        assertEquals("Hash calculation", hash, calculatedHash);
    }

    @Test
    public void copyInputStream() {
        InputStream copy = Utils.copyInputStream(goodJSONIS);

        goodJSONIS.mark(Integer.MAX_VALUE);

        // Gets literal content from each InputStream (the test graph is small, so it shouldn't give problems)
        Scanner s = new Scanner(copy).useDelimiter("\\A");
        String copyC = s.hasNext() ? s.next() : "";

        s = new Scanner(goodJSONIS).useDelimiter("\\A");
        String isC = s.hasNext() ? s.next() : "";

        try {
            goodJSONIS.reset();
        } catch (IOException e) {
        }

        assertEquals("InputStream copy", copyC, isC);
    }

    @Test
    public void hipsterIntegration() {
        // Very simple example to test if Hipster is well integrated in the app
        List<Link> list = new ArrayList<>();
        list.add(new Link(0, 1, 1.0));
        list.add(new Link(1, 2, 1.0));
        HashBasedHipsterDirectedGraph g = Utils.initializeGraph(list);

        List<String> ret = HipsterFacade.dijkstraOS(g, "0", "2");
        List<String> path = Arrays.asList("0", "1", "2");

        assertEquals("Hipster integration", ret, path);
    }

    @Test
    public void gridLayout() {
        // Builds the graph
        int numNodes = 4; // side = 2
        MyGraph g = new MyGraph();
        List<MyNode> nodes = new ArrayList<>();
        for(int i=0;i<numNodes;i++){
            MyNode n = new MyNode();
            n.setId(i);
            nodes.add(n);
        }
        g.setNodes(nodes);
        g.setLinks(new ArrayList<Link>());
        g.setWidth(800); // baseW = 150
        g.setHeight(600); // baseH = 100
        // nodeDistance = 100

        LayoutServices service = new LayoutServices();
        service.applyLayout("grid", g);

        // pos(X|Y) = base(W|H) + (column|row)Index*nodeDistance
        /* 0--nD--1
         * |      |
         * nD    nD
         * |      |
         * 2--nD--3
         */
        assertEquals("Grid test", nodes.get(0).getX(), 150.0 + 0*100.0, 1e-6);
        assertEquals("Grid test", nodes.get(0).getY(), 100.0 + 0*100.0, 1e-6);
        assertEquals("Grid test", nodes.get(1).getX(), 150.0 + 1*100.0, 1e-6);
        assertEquals("Grid test", nodes.get(1).getY(), 100.0 + 0*100.0, 1e-6);
        assertEquals("Grid test", nodes.get(2).getX(), 150.0 + 0*100.0, 1e-6);
        assertEquals("Grid test", nodes.get(2).getY(), 100.0 + 1*100.0, 1e-6);
        assertEquals("Grid test", nodes.get(3).getX(), 150.0 + 1*100.0, 1e-6);
        assertEquals("Grid test", nodes.get(3).getY(), 100.0 + 1*100.0, 1e-6);
    }
}
