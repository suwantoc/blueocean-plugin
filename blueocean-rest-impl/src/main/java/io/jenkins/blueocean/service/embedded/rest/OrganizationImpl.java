package io.jenkins.blueocean.service.embedded.rest;

import hudson.Extension;
import io.jenkins.blueocean.commons.ServiceException;
import io.jenkins.blueocean.commons.stapler.JsonBody;
import io.jenkins.blueocean.rest.model.BlueOrganization;
import io.jenkins.blueocean.rest.model.BluePipelineContainer;
import io.jenkins.blueocean.rest.model.BlueUserContainer;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.jenkinsorganizations.JenkinsOrganizationFolder;
import org.kohsuke.stapler.WebMethod;
import org.kohsuke.stapler.verb.DELETE;
import org.kohsuke.stapler.verb.PUT;

import javax.inject.Inject;
import java.io.IOException;

/**
 * {@link BlueOrganization} implementation for the embedded use.
 *
 * @author Vivek Pandey
 * @author Kohsuke Kawaguchi
 */
public class OrganizationImpl extends BlueOrganization {

    UserContainerImpl users;
    JenkinsOrganizationFolder organizationFolder;

    public OrganizationImpl(JenkinsOrganizationFolder organizationFolder) {
        this.organizationFolder = organizationFolder;
        users = new UserContainerImpl();
    }

    public OrganizationImpl() {
        this(null);
    }
    /**
     * In embedded mode, there's only one organization
     */
    public String getName() {
        if(organizationFolder != null) {
            return organizationFolder.getDisplayName().toLowerCase();
        } else {
            return Jenkins.getInstance().getDisplayName().toLowerCase();
        }
    }

    @Override
    public BluePipelineContainer getPipelines() {
        return new PipelineContainerImpl(this);
    }

    @WebMethod(name="") @DELETE
    public void delete() {
        throw new ServiceException.NotImplementedException("Not implemented yet");
    }

    @WebMethod(name="") @PUT
    public void update(@JsonBody OrganizationImpl given) throws IOException {
        given.validate();
        throw new ServiceException.NotImplementedException("Not implemented yet");
//        getXmlFile().write(given);
    }

    private void validate() {
//        if (name.length()<2)
//            throw new IllegalArgumentException("Invalid name: "+name);
    }

    /**
     * In the embedded case, there's only one organization and everyone belongs there,
     * so we can just return that singleton.
     */
    @Override
    public BlueUserContainer getUsers() {
        return users;
    }

    @Override
    public void deleteOrganiztion() {
        if(getName().equals(Jenkins.getInstance().getDisplayName().toLowerCase())) {
            throw new ServiceException.BadRequestExpception("Default organization can not be deleted");
        }
        try {
            organizationFolder.delete();
        } catch (Throwable t) {
            throw new ServiceException.UnexpectedErrorException("Could not remove organization", t);
        }
    }
}
