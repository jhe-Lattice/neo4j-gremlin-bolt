/*
 *  Copyright 2016 SteelBridge Laboratories, LLC.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  For more information: http://steelbridgelabs.com
 */

package com.steelbridgelabs.oss.neo4j.structure;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.neo4j.driver.Result;
import org.neo4j.driver.Values;
import org.neo4j.driver.summary.ResultSummary;
import org.neo4j.driver.types.Node;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Rogelio J. Baucells
 */
@RunWith(MockitoJUnitRunner.class)
public class Neo4JVertexWhileGettingInOutEdgesTest {

    @Mock
    private Neo4JGraph graph;

    @Mock
    private Transaction transaction;

    @Mock
    private Neo4JSession session;

    @Mock
    private Neo4JReadPartition partition;

    @Mock
    private Node node;

    @Mock
    private Neo4JElementIdProvider vertexIdProvider;

    @Mock
    private Neo4JElementIdProvider edgeIdProvider;

    @Mock
    private Graph.Features.VertexFeatures vertexFeatures;

    @Mock
    private Graph.Features features;

    @Mock
    private Neo4JEdge edge1;

    @Mock
    private Neo4JEdge edge2;

    @Mock
    private Neo4JEdge edge3;

    @Mock
    private Neo4JEdge edge4;

    @Mock
    private Result statementResult;

    @Mock
    private ResultSummary resultSummary;

    @Test
    public void givenNoLabelsShouldGetEdges() {
        // arrange
        Mockito.when(vertexFeatures.getCardinality(Mockito.anyString())).thenAnswer(invocation -> VertexProperty.Cardinality.single);
        Mockito.when(features.vertex()).thenAnswer(invocation -> vertexFeatures);
        Mockito.when(partition.validateLabel(Mockito.anyString())).thenAnswer(invocation -> true);
        Mockito.when(graph.tx()).thenAnswer(invocation -> transaction);
        Mockito.when(graph.getPartition()).thenAnswer(invocation -> partition);
        Mockito.when(graph.features()).thenAnswer(invocation -> features);
        Mockito.when(node.get(Mockito.eq("id"))).thenAnswer(invocation -> Values.value(1L));
        Mockito.when(node.labels()).thenAnswer(invocation -> Collections.singletonList("l1"));
        Mockito.when(node.keys()).thenAnswer(invocation -> Collections.singleton("key1"));
        Mockito.when(node.get(Mockito.eq("key1"))).thenAnswer(invocation -> Values.value("value1"));
        Mockito.when(vertexIdProvider.get(Mockito.any())).thenAnswer(invocation -> 1L);
        Mockito.when(vertexIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(vertexIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "n.id");
        Mockito.when(edgeIdProvider.get(Mockito.any())).thenAnswer(invocation -> 2L);
        Mockito.when(edgeIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(edgeIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "r.id");
        Mockito.when(session.executeStatement(Mockito.eq("MATCH (n:`l1`)-[r]-(m) WHERE n.id = $id RETURN n, r, m"), Mockito.eq(Collections.singletonMap("id", 1L)))).thenAnswer(invocation -> statementResult);
        Mockito.when(session.edges(Mockito.eq(statementResult))).thenAnswer(invocation -> Stream.of(edge1, edge3));
        Mockito.when(statementResult.consume()).thenAnswer(invocation -> resultSummary);
        Neo4JVertex vertex = new Neo4JVertex(graph, session, vertexIdProvider, edgeIdProvider, node);
        // act
        Iterator<Edge> edges = vertex.edges(Direction.BOTH);
        // assert
        Assert.assertNotNull("Failed to get edge iterator", edges);
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
    }

    @Test
    public void givenNoLabelsShouldGetDatabaseAndTransientEdges() {
        // arrange
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 1L);
        parameters.put("ids", Arrays.asList(200L, 400L));
        Mockito.when(vertexFeatures.getCardinality(Mockito.anyString())).thenAnswer(invocation -> VertexProperty.Cardinality.single);
        Mockito.when(features.vertex()).thenAnswer(invocation -> vertexFeatures);
        Mockito.when(partition.validateLabel(Mockito.anyString())).thenAnswer(invocation -> true);
        Mockito.when(graph.tx()).thenAnswer(invocation -> transaction);
        Mockito.when(graph.getPartition()).thenAnswer(invocation -> partition);
        Mockito.when(graph.features()).thenAnswer(invocation -> features);
        Mockito.when(node.get(Mockito.eq("id"))).thenAnswer(invocation -> Values.value(1L));
        Mockito.when(node.labels()).thenAnswer(invocation -> Collections.singletonList("l1"));
        Mockito.when(node.keys()).thenAnswer(invocation -> Collections.singleton("key1"));
        Mockito.when(node.get(Mockito.eq("key1"))).thenAnswer(invocation -> Values.value("value1"));
        Mockito.when(vertexIdProvider.get(Mockito.any())).thenAnswer(invocation -> 1L);
        Mockito.when(vertexIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(vertexIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "n.id");
        Mockito.when(edgeIdProvider.get(Mockito.any())).thenAnswer(invocation -> 2L);
        Mockito.when(edgeIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(edgeIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "r.id");
        Mockito.when(edge1.id()).thenAnswer(invocation -> 100L);
        Mockito.when(edge1.label()).thenAnswer(invocation -> "EL1");
        Mockito.when(edge2.id()).thenAnswer(invocation -> 200L);
        Mockito.when(edge2.label()).thenAnswer(invocation -> "EL2");
        Mockito.when(edge3.id()).thenAnswer(invocation -> 300L);
        Mockito.when(edge3.label()).thenAnswer(invocation -> "EL1");
        Mockito.when(edge4.id()).thenAnswer(invocation -> 400L);
        Mockito.when(edge4.label()).thenAnswer(invocation -> "EL2");
        Mockito.when(session.executeStatement(Mockito.eq("MATCH (n:`l1`)-[r]-(m) WHERE n.id = $id AND NOT r.id IN $ids RETURN n, r, m"), Mockito.eq(parameters))).thenAnswer(invocation -> statementResult);
        Mockito.when(session.edges(Mockito.eq(statementResult))).thenAnswer(invocation -> Stream.of(edge1, edge3));
        Mockito.when(statementResult.consume()).thenAnswer(invocation -> resultSummary);
        Neo4JVertex vertex = new Neo4JVertex(graph, session, vertexIdProvider, edgeIdProvider, node);
        vertex.addOutEdge(edge2);
        vertex.addInEdge(edge4);
        // act
        Iterator<Edge> edges = vertex.edges(Direction.BOTH);
        // assert
        Assert.assertNotNull("Failed to get edge iterator", edges);
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
    }

    @Test
    public void givenLabelShouldGetDatabaseAndTransientEdges() {
        // arrange
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 1L);
        parameters.put("ids", Arrays.asList(200L, 400L));
        Mockito.when(vertexFeatures.getCardinality(Mockito.anyString())).thenAnswer(invocation -> VertexProperty.Cardinality.single);
        Mockito.when(features.vertex()).thenAnswer(invocation -> vertexFeatures);
        Mockito.when(partition.validateLabel(Mockito.anyString())).thenAnswer(invocation -> true);
        Mockito.when(graph.tx()).thenAnswer(invocation -> transaction);
        Mockito.when(graph.getPartition()).thenAnswer(invocation -> partition);
        Mockito.when(graph.features()).thenAnswer(invocation -> features);
        Mockito.when(node.get(Mockito.eq("id"))).thenAnswer(invocation -> Values.value(1L));
        Mockito.when(node.labels()).thenAnswer(invocation -> Collections.singletonList("l1"));
        Mockito.when(node.keys()).thenAnswer(invocation -> Collections.singleton("key1"));
        Mockito.when(node.get(Mockito.eq("key1"))).thenAnswer(invocation -> Values.value("value1"));
        Mockito.when(vertexIdProvider.get(Mockito.any())).thenAnswer(invocation -> 1L);
        Mockito.when(vertexIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(vertexIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "n.id");
        Mockito.when(edgeIdProvider.get(Mockito.any())).thenAnswer(invocation -> 2L);
        Mockito.when(edgeIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(edgeIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "r.id");
        Mockito.when(edge1.id()).thenAnswer(invocation -> 100L);
        Mockito.when(edge1.label()).thenAnswer(invocation -> "EL");
        Mockito.when(edge2.id()).thenAnswer(invocation -> 200L);
        Mockito.when(edge2.label()).thenAnswer(invocation -> "EL");
        Mockito.when(edge3.id()).thenAnswer(invocation -> 300L);
        Mockito.when(edge3.label()).thenAnswer(invocation -> "EL");
        Mockito.when(edge4.id()).thenAnswer(invocation -> 400L);
        Mockito.when(edge4.label()).thenAnswer(invocation -> "EL");
        Mockito.when(session.executeStatement(Mockito.eq("MATCH (n:`l1`)-[r:`EL`]-(m) WHERE n.id = $id AND NOT r.id IN $ids RETURN n, r, m"), Mockito.eq(parameters))).thenAnswer(invocation -> statementResult);
        Mockito.when(session.edges(Mockito.eq(statementResult))).thenAnswer(invocation -> Stream.of(edge1, edge3));
        Mockito.when(statementResult.consume()).thenAnswer(invocation -> resultSummary);
        Neo4JVertex vertex = new Neo4JVertex(graph, session, vertexIdProvider, edgeIdProvider, node);
        vertex.addOutEdge(edge2);
        vertex.addInEdge(edge4);
        // act
        Iterator<Edge> edges = vertex.edges(Direction.BOTH, "EL");
        // assert
        Assert.assertNotNull("Failed to get edge iterator", edges);
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
    }

    @Test
    public void givenLabelsShouldGetDatabaseAndTransientEdges() {
        // arrange
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 1L);
        parameters.put("ids", Arrays.asList(200L, 400L));
        Mockito.when(vertexFeatures.getCardinality(Mockito.anyString())).thenAnswer(invocation -> VertexProperty.Cardinality.single);
        Mockito.when(features.vertex()).thenAnswer(invocation -> vertexFeatures);
        Mockito.when(partition.validateLabel(Mockito.anyString())).thenAnswer(invocation -> true);
        Mockito.when(graph.tx()).thenAnswer(invocation -> transaction);
        Mockito.when(graph.getPartition()).thenAnswer(invocation -> partition);
        Mockito.when(graph.features()).thenAnswer(invocation -> features);
        Mockito.when(node.get(Mockito.eq("id"))).thenAnswer(invocation -> Values.value(1L));
        Mockito.when(node.labels()).thenAnswer(invocation -> Collections.singletonList("l1"));
        Mockito.when(node.keys()).thenAnswer(invocation -> Collections.singleton("key1"));
        Mockito.when(node.get(Mockito.eq("key1"))).thenAnswer(invocation -> Values.value("value1"));
        Mockito.when(vertexIdProvider.get(Mockito.any())).thenAnswer(invocation -> 1L);
        Mockito.when(vertexIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(vertexIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "n.id");
        Mockito.when(edgeIdProvider.get(Mockito.any())).thenAnswer(invocation -> 2L);
        Mockito.when(edgeIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(edgeIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "r.id");
        Mockito.when(edge1.id()).thenAnswer(invocation -> 100L);
        Mockito.when(edge1.label()).thenAnswer(invocation -> "EL1");
        Mockito.when(edge2.id()).thenAnswer(invocation -> 200L);
        Mockito.when(edge2.label()).thenAnswer(invocation -> "EL2");
        Mockito.when(edge3.id()).thenAnswer(invocation -> 300L);
        Mockito.when(edge3.label()).thenAnswer(invocation -> "EL1");
        Mockito.when(edge4.id()).thenAnswer(invocation -> 400L);
        Mockito.when(edge4.label()).thenAnswer(invocation -> "EL2");
        Mockito.when(session.executeStatement(Mockito.eq("MATCH (n:`l1`)-[r:`EL2`|`EL1`]-(m) WHERE n.id = $id AND NOT r.id IN $ids RETURN n, r, m"), Mockito.eq(parameters))).thenAnswer(invocation -> statementResult);
        Mockito.when(session.edges(Mockito.eq(statementResult))).thenAnswer(invocation -> Stream.of(edge1, edge3));
        Mockito.when(statementResult.consume()).thenAnswer(invocation -> resultSummary);
        Neo4JVertex vertex = new Neo4JVertex(graph, session, vertexIdProvider, edgeIdProvider, node);
        vertex.addOutEdge(edge2);
        vertex.addInEdge(edge4);
        // act
        Iterator<Edge> edges = vertex.edges(Direction.BOTH, "EL1", "EL2");
        // assert
        Assert.assertNotNull("Failed to get edge iterator", edges);
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
    }

    @Test
    public void givenLabelShouldGetDatabaseEdges() {
        // arrange
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 1L);
        parameters.put("ids", Arrays.asList(200L, 400L));
        Mockito.when(vertexFeatures.getCardinality(Mockito.anyString())).thenAnswer(invocation -> VertexProperty.Cardinality.single);
        Mockito.when(features.vertex()).thenAnswer(invocation -> vertexFeatures);
        Mockito.when(partition.validateLabel(Mockito.anyString())).thenAnswer(invocation -> true);
        Mockito.when(graph.tx()).thenAnswer(invocation -> transaction);
        Mockito.when(graph.getPartition()).thenAnswer(invocation -> partition);
        Mockito.when(graph.features()).thenAnswer(invocation -> features);
        Mockito.when(node.get(Mockito.eq("id"))).thenAnswer(invocation -> Values.value(1L));
        Mockito.when(node.labels()).thenAnswer(invocation -> Collections.singletonList("l1"));
        Mockito.when(node.keys()).thenAnswer(invocation -> Collections.singleton("key1"));
        Mockito.when(node.get(Mockito.eq("key1"))).thenAnswer(invocation -> Values.value("value1"));
        Mockito.when(vertexIdProvider.get(Mockito.any())).thenAnswer(invocation -> 1L);
        Mockito.when(vertexIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(vertexIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "n.id");
        Mockito.when(edgeIdProvider.get(Mockito.any())).thenAnswer(invocation -> 2L);
        Mockito.when(edgeIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(edgeIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "r.id");
        Mockito.when(edge1.id()).thenAnswer(invocation -> 100L);
        Mockito.when(edge1.label()).thenAnswer(invocation -> "EL1");
        Mockito.when(edge2.id()).thenAnswer(invocation -> 200L);
        Mockito.when(edge2.label()).thenAnswer(invocation -> "EL2");
        Mockito.when(edge3.id()).thenAnswer(invocation -> 300L);
        Mockito.when(edge3.label()).thenAnswer(invocation -> "EL1");
        Mockito.when(edge4.id()).thenAnswer(invocation -> 400L);
        Mockito.when(edge4.label()).thenAnswer(invocation -> "EL2");
        Mockito.when(session.executeStatement(Mockito.eq("MATCH (n:`l1`)-[r:`EL1`]-(m) WHERE n.id = $id AND NOT r.id IN $ids RETURN n, r, m"), Mockito.eq(parameters))).thenAnswer(invocation -> statementResult);
        Mockito.when(session.edges(Mockito.eq(statementResult))).thenAnswer(invocation -> Stream.of(edge1, edge3));
        Mockito.when(statementResult.consume()).thenAnswer(invocation -> resultSummary);
        Neo4JVertex vertex = new Neo4JVertex(graph, session, vertexIdProvider, edgeIdProvider, node);
        vertex.addOutEdge(edge2);
        vertex.addInEdge(edge4);
        // act
        Iterator<Edge> edges = vertex.edges(Direction.BOTH, "EL1");
        // assert
        Assert.assertNotNull("Failed to get edge iterator", edges);
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertFalse("Edges iterator cannot not contain three elements", edges.hasNext());
    }

    @Test
    public void givenLabelShouldGetTransientEdges() {
        // arrange
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 1L);
        parameters.put("ids", Arrays.asList(200L, 400L));
        Mockito.when(vertexFeatures.getCardinality(Mockito.anyString())).thenAnswer(invocation -> VertexProperty.Cardinality.single);
        Mockito.when(features.vertex()).thenAnswer(invocation -> vertexFeatures);
        Mockito.when(partition.validateLabel(Mockito.anyString())).thenAnswer(invocation -> true);
        Mockito.when(graph.tx()).thenAnswer(invocation -> transaction);
        Mockito.when(graph.getPartition()).thenAnswer(invocation -> partition);
        Mockito.when(graph.features()).thenAnswer(invocation -> features);
        Mockito.when(node.get(Mockito.eq("id"))).thenAnswer(invocation -> Values.value(1L));
        Mockito.when(node.labels()).thenAnswer(invocation -> Collections.singletonList("l1"));
        Mockito.when(node.keys()).thenAnswer(invocation -> Collections.singleton("key1"));
        Mockito.when(node.get(Mockito.eq("key1"))).thenAnswer(invocation -> Values.value("value1"));
        Mockito.when(vertexIdProvider.get(Mockito.any())).thenAnswer(invocation -> 1L);
        Mockito.when(vertexIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(vertexIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "n.id");
        Mockito.when(edgeIdProvider.get(Mockito.any())).thenAnswer(invocation -> 2L);
        Mockito.when(edgeIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(edgeIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "r.id");
        Mockito.when(edge1.id()).thenAnswer(invocation -> 100L);
        Mockito.when(edge1.label()).thenAnswer(invocation -> "EL1");
        Mockito.when(edge2.id()).thenAnswer(invocation -> 200L);
        Mockito.when(edge2.label()).thenAnswer(invocation -> "EL2");
        Mockito.when(edge3.id()).thenAnswer(invocation -> 300L);
        Mockito.when(edge3.label()).thenAnswer(invocation -> "EL1");
        Mockito.when(edge4.id()).thenAnswer(invocation -> 400L);
        Mockito.when(edge4.label()).thenAnswer(invocation -> "EL2");
        Mockito.when(session.executeStatement(Mockito.eq("MATCH (n:`l1`)-[r:`EL2`]-(m) WHERE n.id = $id AND NOT r.id IN $ids RETURN n, r, m"), Mockito.eq(parameters))).thenAnswer(invocation -> statementResult);
        Mockito.when(session.edges(Mockito.eq(statementResult))).thenAnswer(invocation -> Stream.empty());
        Mockito.when(statementResult.consume()).thenAnswer(invocation -> resultSummary);
        Neo4JVertex vertex = new Neo4JVertex(graph, session, vertexIdProvider, edgeIdProvider, node);
        vertex.addOutEdge(edge2);
        vertex.addInEdge(edge4);
        // act
        Iterator<Edge> edges = vertex.edges(Direction.BOTH, "EL2");
        // assert
        Assert.assertNotNull("Failed to get edge iterator", edges);
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertFalse("Edges iterator cannot not contain three elements", edges.hasNext());
    }

    @Test
    public void givenNoLabelsAndPartitionMatchPatternShouldGetEdges() {
        // arrange
        Mockito.when(vertexFeatures.getCardinality(Mockito.anyString())).thenAnswer(invocation -> VertexProperty.Cardinality.single);
        Mockito.when(features.vertex()).thenAnswer(invocation -> vertexFeatures);
        Mockito.when(partition.validateLabel(Mockito.anyString())).thenAnswer(invocation -> true);
        Mockito.when(partition.vertexMatchPatternLabels()).thenAnswer(invocation -> new HashSet<>(Arrays.asList("P1", "P2")));
        Mockito.when(graph.tx()).thenAnswer(invocation -> transaction);
        Mockito.when(graph.getPartition()).thenAnswer(invocation -> partition);
        Mockito.when(graph.features()).thenAnswer(invocation -> features);
        Mockito.when(node.get(Mockito.eq("id"))).thenAnswer(invocation -> Values.value(1L));
        Mockito.when(node.labels()).thenAnswer(invocation -> Arrays.asList("l1", "P1", "P2"));
        Mockito.when(node.keys()).thenAnswer(invocation -> Collections.singleton("key1"));
        Mockito.when(node.get(Mockito.eq("key1"))).thenAnswer(invocation -> Values.value("value1"));
        Mockito.when(vertexIdProvider.get(Mockito.any())).thenAnswer(invocation -> 1L);
        Mockito.when(vertexIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(vertexIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "n.id");
        Mockito.when(edgeIdProvider.get(Mockito.any())).thenAnswer(invocation -> 2L);
        Mockito.when(edgeIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(edgeIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "r.id");
        Mockito.when(session.executeStatement(Mockito.eq("MATCH (n:`P1`:`P2`:`l1`)-[r]-(m:`P1`:`P2`) WHERE n.id = $id RETURN n, r, m"), Mockito.eq(Collections.singletonMap("id", 1L)))).thenAnswer(invocation -> statementResult);
        Mockito.when(session.edges(Mockito.eq(statementResult))).thenAnswer(invocation -> Stream.of(edge1, edge3));
        Mockito.when(statementResult.consume()).thenAnswer(invocation -> resultSummary);
        Neo4JVertex vertex = new Neo4JVertex(graph, session, vertexIdProvider, edgeIdProvider, node);
        // act
        Iterator<Edge> edges = vertex.edges(Direction.BOTH);
        // assert
        Assert.assertNotNull("Failed to get edge iterator", edges);
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
    }

    @Test
    public void givenNoLabelsAndPartitionMatchPredicateShouldGetEdges() {
        // arrange
        Mockito.when(vertexFeatures.getCardinality(Mockito.anyString())).thenAnswer(invocation -> VertexProperty.Cardinality.single);
        Mockito.when(features.vertex()).thenAnswer(invocation -> vertexFeatures);
        Mockito.when(partition.validateLabel(Mockito.anyString())).thenAnswer(invocation -> true);
        Mockito.when(partition.vertexMatchPatternLabels()).thenAnswer(invocation -> Collections.emptySet());
        Mockito.when(partition.vertexMatchPredicate(Mockito.eq("m"))).thenAnswer(invocation -> "(m:`P1` OR m:`P2`)");
        Mockito.when(graph.tx()).thenAnswer(invocation -> transaction);
        Mockito.when(graph.getPartition()).thenAnswer(invocation -> partition);
        Mockito.when(graph.features()).thenAnswer(invocation -> features);
        Mockito.when(node.get(Mockito.eq("id"))).thenAnswer(invocation -> Values.value(1L));
        Mockito.when(node.labels()).thenAnswer(invocation -> Arrays.asList("l1", "P1"));
        Mockito.when(node.keys()).thenAnswer(invocation -> Collections.singleton("key1"));
        Mockito.when(node.get(Mockito.eq("key1"))).thenAnswer(invocation -> Values.value("value1"));
        Mockito.when(vertexIdProvider.get(Mockito.any())).thenAnswer(invocation -> 1L);
        Mockito.when(vertexIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(vertexIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "n.id");
        Mockito.when(edgeIdProvider.get(Mockito.any())).thenAnswer(invocation -> 2L);
        Mockito.when(edgeIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(edgeIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "r.id");
        Mockito.when(session.executeStatement(Mockito.eq("MATCH (n:`P1`:`l1`)-[r]-(m) WHERE n.id = $id AND (m:`P1` OR m:`P2`) RETURN n, r, m"), Mockito.eq(Collections.singletonMap("id", 1L)))).thenAnswer(invocation -> statementResult);
        Mockito.when(session.edges(Mockito.eq(statementResult))).thenAnswer(invocation -> Stream.of(edge1, edge3));
        Mockito.when(statementResult.consume()).thenAnswer(invocation -> resultSummary);
        Neo4JVertex vertex = new Neo4JVertex(graph, session, vertexIdProvider, edgeIdProvider, node);
        // act
        Iterator<Edge> edges = vertex.edges(Direction.BOTH);
        // assert
        Assert.assertNotNull("Failed to get edge iterator", edges);
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
    }

    @Test
    public void givenTransientVertexAndNoLabelsShouldGetTransientEdges() {
        // arrange
        Mockito.when(vertexFeatures.getCardinality(Mockito.anyString())).thenAnswer(invocation -> VertexProperty.Cardinality.single);
        Mockito.when(features.vertex()).thenAnswer(invocation -> vertexFeatures);
        Mockito.when(graph.tx()).thenAnswer(invocation -> transaction);
        Mockito.when(graph.getPartition()).thenAnswer(invocation -> partition);
        Mockito.when(graph.features()).thenAnswer(invocation -> features);
        Mockito.when(vertexIdProvider.get(Mockito.any())).thenAnswer(invocation -> 1L);
        Mockito.when(vertexIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(vertexIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "n.id");
        Mockito.when(edgeIdProvider.get(Mockito.any())).thenAnswer(invocation -> 2L);
        Mockito.when(edgeIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(edgeIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "r.id");
        Mockito.when(edge2.id()).thenAnswer(invocation -> 200L);
        Mockito.when(edge2.label()).thenAnswer(invocation -> "EL2");
        Mockito.when(edge4.id()).thenAnswer(invocation -> 400L);
        Mockito.when(edge4.label()).thenAnswer(invocation -> "EL2");
        Neo4JVertex vertex = new Neo4JVertex(graph, session, vertexIdProvider, edgeIdProvider, Collections.singletonList("l1"));
        vertex.addOutEdge(edge2);
        vertex.addInEdge(edge4);
        // act
        Iterator<Edge> edges = vertex.edges(Direction.BOTH);
        // assert
        Assert.assertNotNull("Failed to get edge iterator", edges);
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertFalse("Edges iterator cannot not contain three elements", edges.hasNext());
    }

    @Test
    public void givenTransientVertexAndLabelsShouldGetTransientEdges() {
        // arrange
        Mockito.when(vertexFeatures.getCardinality(Mockito.anyString())).thenAnswer(invocation -> VertexProperty.Cardinality.single);
        Mockito.when(features.vertex()).thenAnswer(invocation -> vertexFeatures);
        Mockito.when(graph.tx()).thenAnswer(invocation -> transaction);
        Mockito.when(graph.getPartition()).thenAnswer(invocation -> partition);
        Mockito.when(graph.features()).thenAnswer(invocation -> features);
        Mockito.when(vertexIdProvider.get(Mockito.any())).thenAnswer(invocation -> 1L);
        Mockito.when(vertexIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(vertexIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "n.id");
        Mockito.when(edgeIdProvider.get(Mockito.any())).thenAnswer(invocation -> 2L);
        Mockito.when(edgeIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(edgeIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "r.id");
        Mockito.when(edge2.id()).thenAnswer(invocation -> 200L);
        Mockito.when(edge2.label()).thenAnswer(invocation -> "EL2");
        Mockito.when(edge4.id()).thenAnswer(invocation -> 400L);
        Mockito.when(edge4.label()).thenAnswer(invocation -> "EL2");
        Neo4JVertex vertex = new Neo4JVertex(graph, session, vertexIdProvider, edgeIdProvider, Collections.singletonList("l1"));
        vertex.addOutEdge(edge2);
        vertex.addInEdge(edge4);
        // act
        Iterator<Edge> edges = vertex.edges(Direction.BOTH, "EL2");
        // assert
        Assert.assertNotNull("Failed to get edge iterator", edges);
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertTrue("Edges iterator is empty", edges.hasNext());
        Assert.assertNotNull("Failed to get edge", edges.next());
        Assert.assertFalse("Edges iterator cannot not contain three elements", edges.hasNext());
    }

    @Test
    public void givenTransientVertexAndLabelsShouldGetEmptyIterator() {
        // arrange
        Mockito.when(vertexFeatures.getCardinality(Mockito.anyString())).thenAnswer(invocation -> VertexProperty.Cardinality.single);
        Mockito.when(features.vertex()).thenAnswer(invocation -> vertexFeatures);
        Mockito.when(graph.tx()).thenAnswer(invocation -> transaction);
        Mockito.when(graph.getPartition()).thenAnswer(invocation -> partition);
        Mockito.when(graph.features()).thenAnswer(invocation -> features);
        Mockito.when(vertexIdProvider.get(Mockito.any())).thenAnswer(invocation -> 1L);
        Mockito.when(vertexIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(vertexIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "n.id");
        Mockito.when(edgeIdProvider.get(Mockito.any())).thenAnswer(invocation -> 2L);
        Mockito.when(edgeIdProvider.fieldName()).thenAnswer(invocation -> "id");
        Mockito.when(edgeIdProvider.matchPredicateOperand(Mockito.any())).thenAnswer(invocation -> "r.id");
        Mockito.when(edge2.id()).thenAnswer(invocation -> 200L);
        Mockito.when(edge2.label()).thenAnswer(invocation -> "EL2");
        Mockito.when(edge4.id()).thenAnswer(invocation -> 200L);
        Mockito.when(edge4.label()).thenAnswer(invocation -> "EL2");
        Neo4JVertex vertex = new Neo4JVertex(graph, session, vertexIdProvider, edgeIdProvider, Collections.singletonList("l1"));
        vertex.addOutEdge(edge2);
        vertex.addInEdge(edge4);
        // act
        Iterator<Edge> edges = vertex.edges(Direction.BOTH, "EL1");
        // assert
        Assert.assertFalse("Edges iterator should be empty", edges.hasNext());
    }
}
