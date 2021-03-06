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

package ai.grakn.graql.internal.pattern.property;

import ai.grakn.graql.Pattern;
import ai.grakn.graql.admin.UniqueVarProperty;
import ai.grakn.graql.VarName;
import ai.grakn.graql.internal.gremlin.EquivalentFragmentSet;
import ai.grakn.util.ErrorMessage;

import java.util.Collection;

public class RhsProperty extends AbstractVarProperty implements UniqueVarProperty, NamedProperty{

    private final Pattern rhs;

    public RhsProperty(Pattern rhs) {
        this.rhs = rhs;
    }

    public Pattern getRhs() {
        return rhs;
    }

    @Override
    public String getName() {
        return "rhs";
    }

    @Override
    public String getProperty() {
        return rhs.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RhsProperty that = (RhsProperty) o;

        return rhs.equals(that.rhs);

    }

    @Override
    public int hashCode() {
        return rhs.hashCode();
    }

    @Override
    public Collection<EquivalentFragmentSet> match(VarName start) {
        throw new UnsupportedOperationException(ErrorMessage.MATCH_INVALID.getMessage(this.getClass().getName()));
    }
}
