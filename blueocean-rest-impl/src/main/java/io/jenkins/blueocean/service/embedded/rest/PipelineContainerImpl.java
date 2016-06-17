package io.jenkins.blueocean.service.embedded.rest;

import com.google.common.collect.Lists;
import hudson.model.BuildableItem;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import io.jenkins.blueocean.commons.ServiceException;
import io.jenkins.blueocean.rest.model.BluePipeline;
import io.jenkins.blueocean.rest.model.BluePipelineContainer;
import jenkins.branch.MultiBranchProject;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.jenkinsorganizations.JenkinsOrganizationFolder;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vivek Pandey
 */
public class PipelineContainerImpl extends BluePipelineContainer {
    private final OrganizationImpl organization;
    private final ItemGroup itemGroup;

    public PipelineContainerImpl(OrganizationImpl organization) {
        this.organization = organization;
        this.itemGroup = null;
    }

    public PipelineContainerImpl(OrganizationImpl organization, ItemGroup itemGroup) {
        this.organization = organization;
        this.itemGroup = itemGroup;

    }

    @Override
    public BluePipeline get(String name) {
        Item item;

        if(itemGroup != null) {
            item = itemGroup.getItem(name);
        } else if(organization.organizationFolder != null) {
            item = organization.organizationFolder.getItem(name);
        } else {
            item = Jenkins.getInstance().getItem(name);
        }

        if(item instanceof JenkinsOrganizationFolder) {
            throw new ServiceException.NotImplementedException(("This si an organization folder"));
        }

        if(item == null){
            throw new ServiceException.NotFoundException(String.format("Pipeline %s not found", name));
        }

        if (item instanceof BuildableItem) {
            if (item instanceof MultiBranchProject) {
                return new MultiBranchPipelineImpl(organization, (MultiBranchProject) item);
            } else if (!isMultiBranchProjectJob((BuildableItem) item) && item instanceof Job) {
                return new PipelineImpl(organization, (Job) item);
            }
        } else if (item instanceof ItemGroup) {
            return new PipelineFolderImpl(organization, (ItemGroup) item);
        }

        // TODO: I'm going to turn this into a decorator annotation
        throw new ServiceException.NotFoundException(String.format("Pipeline %s not found", name));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<BluePipeline> iterator() {
        if(itemGroup != null) {
            return getPipelines(organization, itemGroup.getItems());
        } else if (organization.organizationFolder != null) {
            return getPipelines(organization, organization.organizationFolder.getItems());
        }else{
            List<TopLevelItem> items = Jenkins.getInstance().getItems(TopLevelItem.class);
            List<TopLevelItem> filteredItems = Lists.newArrayList();
            for (TopLevelItem item : items) {
                if(item instanceof JenkinsOrganizationFolder) {
                    continue;
                } else {
                    filteredItems.add(item);
                }
            }

            return getPipelines(organization, filteredItems);
        }
    }

    protected static boolean isMultiBranchProjectJob(BuildableItem item){
        return item instanceof WorkflowJob && item.getParent() instanceof MultiBranchProject;
    }

    protected static Iterator<BluePipeline> getPipelines(OrganizationImpl organization, Collection<? extends Item> items){
        List<BluePipeline> pipelines = new ArrayList<>();
        for (Item item : items) {
            BluePipeline pipeline = getPipelineFromItem(item, organization);
            if(pipeline != null) {
                pipelines.add(pipeline);
            }
        }
        return pipelines.iterator();
    }

    protected static Iterator<BluePipeline> getPipelinesCalcOrg(Collection<? extends Item> items){
        List<BluePipeline> pipelines = new ArrayList<>();
        for (Item item : items) {
            OrganizationImpl organization = getParentOrg(item.getParent());

            BluePipeline pipeline = getPipelineFromItem(item, organization);
            if(pipeline != null) {
                pipelines.add(pipeline);
            }
        }
        return pipelines.iterator();
    }

    protected static BluePipeline getPipelineFromItem(Item item, OrganizationImpl organization) {
        if(item instanceof MultiBranchProject){
            return new MultiBranchPipelineImpl(organization, (MultiBranchProject) item);
        }else if(item instanceof BuildableItem && !isMultiBranchProjectJob((BuildableItem) item)
            && item instanceof Job){
            return new PipelineImpl(organization, (Job) item);
        }else if(item instanceof ItemGroup){
           return new PipelineFolderImpl(organization, (ItemGroup) item);
        }
        return null;
    }
    protected static OrganizationImpl getParentOrg(ItemGroup group) {
        if(group == Jenkins.getInstance()) {
            return new OrganizationImpl();
        }

        if(group instanceof JenkinsOrganizationFolder) {
            return new OrganizationImpl((JenkinsOrganizationFolder)group);
        }

        if(group instanceof Item) {
            return getParentOrg(((Item) group).getParent());
        } else {
            throw new ServiceException.UnexpectedErrorException("Can't find parent org");
        }
    }
}
