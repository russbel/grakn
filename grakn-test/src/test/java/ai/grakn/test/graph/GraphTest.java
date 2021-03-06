package ai.grakn.test.graph;

import ai.grakn.Grakn;
import ai.grakn.GraknGraph;
import ai.grakn.concept.ConceptId;
import ai.grakn.concept.Instance;
import ai.grakn.concept.RoleType;
import ai.grakn.factory.GraphFactory;
import ai.grakn.test.AbstractRollbackGraphTest;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class GraphTest extends AbstractRollbackGraphTest {

    @Test
    public void testSwitchingBetweenNormalAndBatchGraphCleanly() throws Exception {
        String thing = "thing";
        graph.putEntityType(thing);
        graph.commit();

        graph = factory.getGraph();
        assertNotNull(graph.getEntityType(thing));

        String related = "related";
        String related1 = "related1";
        String related2 = "related2";
        graph.putRelationType("related").hasRole(graph.putRoleType(related1)).hasRole(graph.putRoleType(related2));
        graph.commit();

        graph = factory.getGraph();
        assertNotNull(graph.getRoleType(related1));
        assertNotNull(graph.getRoleType(related2));
        assertNotNull(graph.getRelationType(related));

        ConceptId e1 = graph.getEntityType(thing).addEntity().getId();
        graph.commit();

        graph = factory.getGraphBatchLoading();
        ConceptId e2 = graph.getEntityType(thing).addEntity().getId();
        graph.commit();

        graph = factory.getGraph();
        graph.getEntityType(thing).playsRole(graph.getRoleType(related1)).playsRole(graph.getRoleType(related2));
        graph.commit();

        graph = factory.getGraphBatchLoading();
        ConceptId r1 = graph.getRelationType(related).addRelation()
                .putRolePlayer(graph.getRoleType(related1),graph.getConcept(e1))
                .putRolePlayer(graph.getRoleType(related2),graph.getConcept(e2)).getId();
        graph.commit();

        graph = factory.getGraph();
        Map<RoleType, Instance> rps = graph.getConcept(r1).asRelation().rolePlayers();
        assertEquals(2,rps.size());
        assertEquals(graph.getConcept(e1), rps.get(graph.getRoleType(related1)));
        assertEquals(graph.getConcept(e2), rps.get(graph.getRoleType(related2)));

        graph = factory.getGraphBatchLoading();
        graph.getConcept(r1).delete();
        graph.commit();

        graph = factory.getGraph();
        assertNull(graph.getConcept(r1));
    }

    @Test
    public void isClosedTest() throws Exception {
        GraknGraph graph = Grakn.factory(Grakn.DEFAULT_URI, "isitclosed").getGraph();
        graph.putEntityType("thing");
        graph.commit();

        assertFalse(graph.isClosed());

        HashSet<Future> futures = new HashSet<>();
        futures.add(Executors.newCachedThreadPool().submit(this::addThingToBatch));

        for (Future future : futures) {
            future.get();
        }

        assertFalse(graph.isClosed());
        assertFalse(graph.getEntityType("thing").instances().isEmpty());
    }

    private void addThingToBatch(){
        try(GraknGraph graphBatchLoading = Grakn.factory(Grakn.DEFAULT_URI, "isitclosed").getGraph()) {
            graphBatchLoading.getEntityType("thing").addEntity();
            graphBatchLoading.commit();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSameGraphs(){
        String key = "mykeyspace";
        GraknGraph graph1 = Grakn.factory(Grakn.DEFAULT_URI, key).getGraph();
        GraknGraph graph2 = GraphFactory.getInstance().getGraph(key);
        assertEquals(graph1, graph2);
    }
}
