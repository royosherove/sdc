/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.core.tools.store;

import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.datastax.driver.mapping.annotations.QueryParameters;
import org.openecomp.core.nosqldb.api.NoSqlDb;
import org.openecomp.core.nosqldb.factory.NoSqlDbFactory;
import org.openecomp.sdc.versioning.dao.VersionInfoDao;
import org.openecomp.sdc.versioning.dao.VersionInfoDaoFactory;
import org.openecomp.sdc.versioning.dao.types.VersionInfoEntity;

import java.util.Collection;

public class VersionInfoCassandraLoader {

    private static NoSqlDb noSqlDb = NoSqlDbFactory.getInstance().createInterface();
    private static Mapper<VersionInfoEntity> mapper =
            noSqlDb.getMappingManager().mapper(VersionInfoEntity.class);
    private static VersionInfoAccessor accessor =
            noSqlDb.getMappingManager().createAccessor(VersionInfoAccessor.class);
    private static VersionInfoDao versionInfoDao =
            VersionInfoDaoFactory.getInstance().createInterface();

    public void insertVersionInfo(VersionInfoEntity versionInfoEntity) {
        versionInfoDao.create(versionInfoEntity);
    }

    public Collection<VersionInfoEntity> list() {
        return accessor.getAll().all();
    }

    @Accessor
    interface VersionInfoAccessor {
        @Query("select * from dox.version_info ")
        @QueryParameters(fetchSize = 400)
        Result<VersionInfoEntity> getAll();
    }


}
