/*
 * Grakn - A Distributed Semantic Database
 * Copyright (C) 2016  Grakn Labs Limited
 *
 * Grakn is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Grakn is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Grakn. If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */

package ai.grakn.test.graql.reasoner.inference;

import ai.grakn.GraknGraph;
import ai.grakn.concept.Concept;
import ai.grakn.graql.MatchQuery;
import ai.grakn.graql.QueryBuilder;
import ai.grakn.graql.internal.reasoner.Reasoner;
import ai.grakn.graql.VarName;
import ai.grakn.graql.internal.util.CommonUtil;
import ai.grakn.test.AbstractGraknTest;
import ai.grakn.test.graql.reasoner.graphs.AbstractGraph;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;


public class AbstractInferenceTest extends AbstractGraknTest {
    private static QueryBuilder qb;

    @BeforeClass
    public static void setUpClass() throws Exception {
        GraknGraph graph = AbstractGraph.getGraph();
        qb = graph.graql().infer(false);
    }

    /**silently allows multiple isas*/
    @Test
    @Ignore
    public void testQuery() {
        String queryString = "match $x isa Q;";
        MatchQuery query = qb.parse(queryString);

        String explicitQuery = "match " +
                "{$x isa Q} or {\n" +
                "{$y isa q} or {$y isa t};\n" +
                "{{$x isa p} or {$x isa s}} or {{$x isa r} or {$x isa u}};\n" +
                "($x, $y) isa rel\n" +
                "}; select $x";

        assertQueriesEqual(Reasoner.resolve(query, false), qb.<MatchQuery>parse(explicitQuery).stream());
    }

    /**silently allows multiple isas*/
    @Test
    @Ignore
    public void testQuery2() {
        String queryString = "match " +
                        "$yy isa Q;$y isa P;($y, $yy) isa REL; select $yy";
        MatchQuery query = qb.parse(queryString);

        String explicitQuery = "match " +
                                "{$yy isa Q} or {" +
                                "{$yyy isa q} or {$yyy isa t};\n" +
                                "($yy, $yyy) isa rel;\n" +
                                "{{$yy isa p} or {$yy isa s}} or {{yy isa r} or {$yy isa u}}" +
                                "};" +
                                "$y isa P;\n" +
                                "($y, $yy) isa REL; select $yy";

        assertQueriesEqual(Reasoner.resolve(query, false), qb.<MatchQuery>parse(explicitQuery).stream());
    }

    private void assertQueriesEqual(Stream<Map<VarName, Concept>> s1, Stream<Map<String, Concept>> s2) {
        assertEquals(s1.map(CommonUtil::resultVarNameToString).collect(Collectors.toSet()), s2.collect(Collectors.toSet()));
    }

}
