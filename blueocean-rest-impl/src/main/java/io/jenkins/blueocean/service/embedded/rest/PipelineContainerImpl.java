package io.jenkins.blueocean.service.embedded.rest;

import hudson.model.Item;
import hudson.model.ItemGroup;
import io.jenkins.blueocean.commons.ServiceException;
import io.jenkins.blueocean.rest.Reachable;
import io.jenkins.blueocean.rest.hal.Link;
import io.jenkins.blueocean.rest.model.BluePipeline;
import io.jenkins.blueocean.rest.model.BluePipelineContainer;
import jenkins.model.Jenkins;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vivek Pandey
 */
public class PipelineContainerImpl extends BluePipelineContainer {
    private final @Nonnull ItemGroup itemGroup;
    private final Link self;
    final OrganizationImpl organization;

    public PipelineContainerImpl(OrganizationImpl organization) {
        this(Jenkins.getInstance(), organization, null);
    }

    public PipelineContainerImpl(ItemGroup itemGroup, OrganizationImpl organization) {
        this(itemGroup, organization, null);
    }

    public PipelineContainerImpl(ItemGroup itemGroup, OrganizationImpl organization, Reachable parent) {
        this.itemGroup = itemGroup;
        this.organization = organization;
        if(parent!=null){
            this.self = parent.getLink().rel("pipelines");
        }else{
            this.self = organization.getLink().rel("pipelines");
        }
    }
    @Override
    public Link getLink() {
        return self;
    }

    @Override
    public BluePipeline get(String name) {
        Item item;
        item = itemGroup.getItem(name);

        if(item == null){
            throw new ServiceException.NotFoundException(String.format("Pipeline %s not found", name));
        }
        return get(item);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<BluePipeline> iterator() {
        return getPipelines(itemGroup.getItems());
    }

    protected  Iterator<BluePipeline> getPipelines(Collection<? extends Item> items){
        List<BluePipeline> pipelines = new ArrayList<>();
        for (Item item : items) {
            BluePipeline pipeline  = get(item);
            if(pipeline != null){
                pipelines.add(pipeline);
            }
        }
        return pipelines.iterator();
    }

    private BluePipeline get(Item item){

        for(BluePipelineFactory factory:BluePipelineFactory.all()){
            BluePipeline pipeline = factory.getPipeline(item, this);
            if( pipeline!= null){
                return pipeline;
            }
        }
        return null;
    }
}
