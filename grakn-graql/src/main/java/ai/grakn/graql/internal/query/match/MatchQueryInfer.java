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

package ai.grakn.graql.internal.query.match;

import ai.grakn.GraknGraph;
import ai.grakn.concept.Concept;
import ai.grakn.graql.internal.reasoner.Reasoner;
import ai.grakn.graql.VarName;
import ai.grakn.util.ErrorMessage;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static ai.grakn.graql.internal.util.CommonUtil.optionalOr;

/**
 * Modifier that specifies the graph to execute the match query with.
 */
class MatchQueryInfer extends MatchQueryModifier {

    private final boolean materialise;

    MatchQueryInfer(AbstractMatchQuery inner, boolean materialise) {
        super(inner);
        this.materialise = materialise;
    }

    @Override
    public Stream<Map<VarName, Concept>> stream(Optional<GraknGraph> optionalGraph) {
        GraknGraph graph = optionalOr(optionalGraph, inner.getGraph()).orElseThrow(
                () -> new IllegalStateException(ErrorMessage.NO_GRAPH.getMessage())
        );

        return Reasoner.resolve(inner.withGraph(graph), materialise);
    }

    @Override
    protected String modifierString() {
        return "";
    }
}
