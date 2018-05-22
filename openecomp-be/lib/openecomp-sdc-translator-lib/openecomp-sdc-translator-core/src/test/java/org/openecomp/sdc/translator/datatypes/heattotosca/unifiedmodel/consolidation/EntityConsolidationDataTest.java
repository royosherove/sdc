/*
 * Copyright © 2016-2018 European Support Limited
 *
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
 */

package org.openecomp.sdc.translator.datatypes.heattotosca.unifiedmodel.consolidation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.onap.sdc.tosca.datatypes.model.RequirementAssignment;

@SuppressWarnings("Duplicates")
public class EntityConsolidationDataTest {

    private static final String NODE_TEMPLATE_ID_1 = "nodeTemplateId1";
    private static final String NODE_TEMPLATE_ID_2 = "nodeTemplateId2";
    private static final String REQUIREMENT_ID_1 = "requirementId1";
    private static final String REQUIREMENT_ID_2 = "requirementId2";

    @Test
    public void testAddNodesConnectedIn_SameNodeTemplateIds() {
        EntityConsolidationData consolidationData = new EntityConsolidationData();
        Map<String, String[]> expectedNodesConnectedData = new HashMap<>();

        addNodesConnectedIn(consolidationData,NODE_TEMPLATE_ID_1, REQUIREMENT_ID_1);
        expectedNodesConnectedData.put(NODE_TEMPLATE_ID_1, new String[]{REQUIREMENT_ID_1});
        checkNodesConnected(consolidationData.getNodesConnectedIn(), expectedNodesConnectedData);

        addNodesConnectedIn(consolidationData,NODE_TEMPLATE_ID_1, REQUIREMENT_ID_2);
        expectedNodesConnectedData.put(NODE_TEMPLATE_ID_1, new String[]{REQUIREMENT_ID_1, REQUIREMENT_ID_2});
        checkNodesConnected(consolidationData.getNodesConnectedIn(), expectedNodesConnectedData);
    }

    @Test
    public void testAddNodesConnectedIn_DiffNodeTemplateIds() {
        EntityConsolidationData consolidationData = new EntityConsolidationData();
        Map<String, String[]> expectedNodesConnectedData = new HashMap<>();

        addNodesConnectedIn(consolidationData, NODE_TEMPLATE_ID_1, REQUIREMENT_ID_1);
        expectedNodesConnectedData.put(NODE_TEMPLATE_ID_1, new String[]{REQUIREMENT_ID_1});
        checkNodesConnected(consolidationData.getNodesConnectedIn(), expectedNodesConnectedData);

        addNodesConnectedIn(consolidationData, NODE_TEMPLATE_ID_2, REQUIREMENT_ID_2);
        expectedNodesConnectedData.put(NODE_TEMPLATE_ID_2, new String[]{REQUIREMENT_ID_2});
        checkNodesConnected(consolidationData.getNodesConnectedIn(), expectedNodesConnectedData);
    }

    @Test
    public void testAddNodesConnectedOut_SameNodeTemplateIds() {
        EntityConsolidationData consolidationData = new EntityConsolidationData();
        Map<String, String[]> expectedNodesConnectedData = new HashMap<>();

        addNodesConnectedOut(consolidationData, NODE_TEMPLATE_ID_1, REQUIREMENT_ID_1);
        expectedNodesConnectedData.put(NODE_TEMPLATE_ID_1, new String[]{REQUIREMENT_ID_1});
        checkNodesConnected(consolidationData.getNodesConnectedOut(), expectedNodesConnectedData);

        addNodesConnectedOut(consolidationData, NODE_TEMPLATE_ID_1, REQUIREMENT_ID_2);
        expectedNodesConnectedData.put(NODE_TEMPLATE_ID_1, new String[]{REQUIREMENT_ID_1, REQUIREMENT_ID_2});
        checkNodesConnected(consolidationData.getNodesConnectedOut(), expectedNodesConnectedData);
    }

    @Test
    public void testAddNodesConnectedOut_DiffNodeTemplateIds() {
        EntityConsolidationData consolidationData = new EntityConsolidationData();
        Map<String, String[]> expectedNodesConnectedData = new HashMap<>();

        addNodesConnectedOut(consolidationData, NODE_TEMPLATE_ID_1, REQUIREMENT_ID_1);
        expectedNodesConnectedData.put(NODE_TEMPLATE_ID_1, new String[]{REQUIREMENT_ID_1});
        checkNodesConnected(consolidationData.getNodesConnectedOut(), expectedNodesConnectedData);

        addNodesConnectedOut(consolidationData, NODE_TEMPLATE_ID_2, REQUIREMENT_ID_2);
        expectedNodesConnectedData.put(NODE_TEMPLATE_ID_2, new String[]{REQUIREMENT_ID_2});
        checkNodesConnected(consolidationData.getNodesConnectedOut(), expectedNodesConnectedData);
    }

    @Test
    public void testAddOutputParamGetAttrIn() {
        EntityConsolidationData consolidationData = new EntityConsolidationData();
        GetAttrFuncData getAttrFuncData1 = createGetAttrFuncData("field1");

        consolidationData.addOutputParamGetAttrIn(getAttrFuncData1);
        List<GetAttrFuncData> outputParametersGetAttrIn = consolidationData.getOutputParametersGetAttrIn();
        Assert.assertEquals(1, outputParametersGetAttrIn.size());
        Assert.assertTrue(outputParametersGetAttrIn.contains(getAttrFuncData1));

        GetAttrFuncData getAttrFuncData2 = createGetAttrFuncData("field2");
        consolidationData.addOutputParamGetAttrIn(getAttrFuncData2);
        Assert.assertEquals(2,outputParametersGetAttrIn.size());
        Assert.assertTrue(outputParametersGetAttrIn.contains(getAttrFuncData1));
        Assert.assertTrue(outputParametersGetAttrIn.contains(getAttrFuncData2));
    }

    @Test
    public void testRemoveParamNameFromAttrFuncList() {
        EntityConsolidationData consolidationData = new EntityConsolidationData();
        GetAttrFuncData getAttrFuncData1 = createGetAttrFuncData("field1");

        consolidationData.addOutputParamGetAttrIn(getAttrFuncData1);
        // verify that getAttrFuncData was added
        List<GetAttrFuncData> outputParametersGetAttrIn = consolidationData.getOutputParametersGetAttrIn();
        Assert.assertEquals(1, outputParametersGetAttrIn.size());

        consolidationData.removeParamNameFromAttrFuncList("field2");
        //verify that not existing getAttrFuncData parameter wasn't removed and no Exception
        outputParametersGetAttrIn = consolidationData.getOutputParametersGetAttrIn();
        Assert.assertEquals(1, outputParametersGetAttrIn.size());

        consolidationData.removeParamNameFromAttrFuncList("field1");
        //verify that existing getAttrFuncData parameter was removed
        outputParametersGetAttrIn = consolidationData.getOutputParametersGetAttrIn();
        Assert.assertEquals(0, outputParametersGetAttrIn.size());
    }

    private GetAttrFuncData createGetAttrFuncData(String field) {
        GetAttrFuncData getAttrFuncData = new GetAttrFuncData();
        getAttrFuncData.setFieldName(field);
        getAttrFuncData.setAttributeName("attribute");
        return  getAttrFuncData;
    }

    private void checkNodesConnected(Map<String, List<RequirementAssignmentData>> actualNodesConnected,
                                              Map<String, String[]> expectedNodesConnected) {
        Assert.assertNotNull(actualNodesConnected);

        expectedNodesConnected.keySet().forEach(expectedNodeTemplateId -> {
            Assert.assertTrue(actualNodesConnected.containsKey(expectedNodeTemplateId));
            Assert.assertEquals(expectedNodesConnected.size(), actualNodesConnected.size());

            List<RequirementAssignmentData> actualRequirementAssignmentData =
                    actualNodesConnected.get(expectedNodeTemplateId);
            List<String> expectedRequirementIdList =
                    Arrays.asList(expectedNodesConnected.get(expectedNodeTemplateId));
            Assert.assertEquals(expectedRequirementIdList.size(), actualRequirementAssignmentData.size());

            actualRequirementAssignmentData.forEach(actualRequirementAssignment ->
                    Assert.assertTrue(expectedRequirementIdList
                            .contains(actualRequirementAssignment.getRequirementId())));
        });

    }

    private void addNodesConnectedIn(EntityConsolidationData consolidationData,
            String nodeTemplateId, String requirementId) {
        RequirementAssignment requirementAssignment = new RequirementAssignment();
        consolidationData.addNodesConnectedIn(nodeTemplateId, requirementId, requirementAssignment);
    }

    private void addNodesConnectedOut(EntityConsolidationData consolidationData,
            String nodeTemplateId, String requirementId) {
        RequirementAssignment requirementAssignment = new RequirementAssignment();
        consolidationData.addNodesConnectedOut(nodeTemplateId, requirementId, requirementAssignment);
    }
}
