package io.jenkins.blueocean.rest.impl.pipeline;

import io.jenkins.blueocean.rest.hal.LinkResolver;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.graph.FlowNode;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.support.visualization.table.FlowGraphTable;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Vivek Pandey
 */
public class LinkResolverTest extends PipelineBaseTest {

    @Override
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void resolveNodeLink() throws Exception {
        {
            WorkflowJob job1 = j.jenkins.createProject(WorkflowJob.class, "pipeline1");
            job1.setDefinition(new CpsFlowDefinition("stage \"Build\"\n" +
                "    node {\n" +
                "       sh \"echo here\"\n" +
                "    }\n" +
                "\n" +
                "stage \"Test\"\n" +
                "    parallel (\n" +
                "        \"Firefox\" : {\n" +
                "            node {\n" +
                "                sh \"echo ffox\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"Chrome\" : {\n" +
                "            node {\n" +
                "                sh \"echo chrome\"\n" +
                "            }\n" +
                "        }\n" +
                "    )\n" +
                "\n" +
                "stage \"CrashyMcgee\"\n" +
                "  parallel (\n" +
                "    \"SlowButSuccess\" : {\n" +
                "        node {\n" +
                "            echo 'This is time well spent.'\n" +
                "        }\n" +
                "    },\n" +
                "    \"DelayThenFail\" : {\n" +
                "        node {\n" +
                "            echo 'Not yet.'\n" +
                "        }\n" +
                "    }\n" +
                "  )\n" +
                "\n" +
                "\n" +
                "stage \"Deploy\"\n" +
                "    node {\n" +
                "        sh \"echo deploying\"\n" +
                "    }"));

            WorkflowRun b1 = job1.scheduleBuild2(0).get();
            j.assertBuildStatusSuccess(b1);

            FlowGraphTable nodeGraphTable = new FlowGraphTable(b1.getExecution());
            nodeGraphTable.build();
            List<FlowNode> nodes = getStages(nodeGraphTable);
            List<FlowNode> parallelNodes = getParallelNodes(nodeGraphTable);

            Assert.assertEquals(String.format("/blue/rest/organizations/jenkins/pipelines/pipeline1/runs/%s/nodes/%s/",
                b1.getId(),nodes.get(0).getId()),
                LinkResolver.resolveLink(nodes.get(0)).getHref());

            Assert.assertEquals(String.format("/blue/rest/organizations/jenkins/pipelines/pipeline1/runs/%s/nodes/%s/",
                b1.getId(),parallelNodes.get(0).getId()),
                LinkResolver.resolveLink(parallelNodes.get(0)).getHref());

            PipelineNodeGraphBuilder graphBuilder = new PipelineNodeGraphBuilder(b1);

            List<FlowNode> steps = graphBuilder.getAllSteps();

            Assert.assertEquals(String.format("/blue/rest/organizations/jenkins/pipelines/pipeline1/runs/%s/steps/%s/",
                b1.getId(),steps.get(0).getId()),
                LinkResolver.resolveLink(steps.get(0)).getHref());

        }
    }
}
