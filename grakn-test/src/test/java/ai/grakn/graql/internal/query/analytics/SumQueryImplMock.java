package ai.grakn.graql.internal.query.analytics;

import ai.grakn.Grakn;
import ai.grakn.GraknComputer;
import ai.grakn.GraknGraph;
import ai.grakn.factory.GraknGraphFactoryMock;

import java.util.Optional;

/**
 *
 */
public class SumQueryImplMock extends SumQueryImpl{
    final int numberOfWorkers;

    public SumQueryImplMock(Optional<GraknGraph> graph, int numberOfWorkers) {
        super(graph);
        this.numberOfWorkers = numberOfWorkers;
    }

    @Override
    protected GraknComputer getGraphComputer() {
        GraknGraphFactoryMock factory = new GraknGraphFactoryMock(keySpace, Grakn.DEFAULT_URI);
        return factory.getGraphComputer(numberOfWorkers);
    }
}
