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

package org.openecomp.sdc.be.resources.data.auditing;

import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.openecomp.sdc.be.resources.data.auditing.model.CommonAuditData;
import org.openecomp.sdc.common.datastructure.AuditingFieldsKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

@Table(keyspace = AuditingTypesConstants.AUDIT_KEYSPACE, name = AuditingTypesConstants.CONSUMER_EVENT_TYPE)
public class ConsumerEvent extends AuditingGenericEvent {

    @PartitionKey
    protected UUID timebaseduuid;

    @ClusteringColumn
    protected Date timestamp1;

    @Column(name = "request_id")
    protected String requestId;
    @Column
    protected String action;
    @Column
    protected String status;

    @Column(name = "description")
    protected String desc;

    @Column
    private String modifier;

    @Column(name = "ecomp_user")
    private String ecompUser;

    public ConsumerEvent(String action, CommonAuditData commonAuditData, String ecompUser, String modifier) {
        this();
        this.action = action;
        this.requestId = commonAuditData.getRequestId();
        this.status = commonAuditData.getStatus();
        this.desc = commonAuditData.getDescription();
        this.modifier = modifier;
        this.ecompUser = ecompUser;
    }

    //Required to be public as it is used by Cassandra driver on get operation
    public ConsumerEvent() {
        timestamp1 = new Date();
        timebaseduuid = UUIDs.timeBased();
    }

    public void setTimestamp1(String timestamp) {
        this.timestamp1 = parseDateFromString(timestamp);
    }

    @Override
    public void fillFields() {
        fields.put(AuditingFieldsKey.AUDIT_REQUEST_ID.getDisplayName(), getRequestId());

        fields.put(AuditingFieldsKey.AUDIT_ACTION.getDisplayName(), getAction());
        fields.put(AuditingFieldsKey.AUDIT_STATUS.getDisplayName(), getStatus());
        fields.put(AuditingFieldsKey.AUDIT_DESC.getDisplayName(), getDesc());
        fields.put(AuditingFieldsKey.AUDIT_MODIFIER_UID.getDisplayName(), getModifier());
        fields.put(AuditingFieldsKey.AUDIT_ECOMP_USER.getDisplayName(), getEcompUser());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatPattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        fields.put(AuditingFieldsKey.AUDIT_TIMESTAMP.getDisplayName(), simpleDateFormat.format(timestamp1));
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getEcompUser() {
        return ecompUser;
    }

    public void setEcompUser(String ecompUser) {
        this.ecompUser = ecompUser;
    }

    public UUID getTimebaseduuid() {
        return timebaseduuid;
    }

    public void setTimebaseduuid(UUID timebaseduuid) {
        this.timebaseduuid = timebaseduuid;
    }

    public Date getTimestamp1() {
        return timestamp1;
    }

    public void setTimestamp1(Date timestamp1) {
        this.timestamp1 = timestamp1;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "ConsumerEvent [timebaseduuid=" + timebaseduuid + ", timestamp1=" + timestamp1 + ", requestId="
                + requestId + ", action=" + action + ", status=" + status + ", desc=" + desc + ", modifier=" + modifier
                + ", ecompUser=" + ecompUser + "]";
    }

}
