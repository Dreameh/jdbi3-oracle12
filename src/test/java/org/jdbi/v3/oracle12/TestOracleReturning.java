/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.oracle12;

import java.util.List;

import oracle.jdbc.OracleTypes;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Something;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jdbi.v3.oracle12.OracleReturning.returnParameters;
import static org.jdbi.v3.oracle12.OracleReturning.returningDml;

public class TestOracleReturning {

    @Rule
    public OracleDatabaseRule dbRule = new OracleDatabaseRule().withPlugin(new SqlObjectPlugin());

    @Test
    public void testReturningDmlPositionalParams() {
        Handle h = dbRule.getSharedHandle();

        List<Integer> ids = h.createUpdate("insert into something(id, name) values (?, ?) returning id into ?")
                .bind(0, 17)
                .bind(1, "Brian")
                .addCustomizer(returnParameters().register(2, OracleTypes.INTEGER))
                .execute(returningDml())
                .mapTo(int.class)
                .list();

        assertThat(ids).containsExactly(17);
    }

    @Test
    public void testReturningDmlNamedParams() {
        Handle h = dbRule.getSharedHandle();

        List<Integer> ids = h.createUpdate("insert into something(id, name) values (:id, :name) returning id into :result")
                .bindBean(new Something(17, "Brian"))
                .addCustomizer(returnParameters().register("result", OracleTypes.INTEGER))
                .execute(returningDml())
                .mapTo(int.class)
                .list();

        assertThat(ids).containsExactly(17);
    }
}
