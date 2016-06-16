package io.jenkins.blueocean.service.embedded.rest;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import hudson.Extension;
import hudson.model.Item;
import io.jenkins.blueocean.commons.ServiceException;
import io.jenkins.blueocean.rest.model.BlueOrganization;
import io.jenkins.blueocean.rest.model.BlueOrganizationContainer;
import io.jenkins.blueocean.rest.model.BlueUserContainer;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.jenkinsorganizations.JenkinsOrganizationFolder;
import org.kohsuke.stapler.json.JsonBody;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * {@link BlueOrganizationContainer} for the embedded use
 *
 * @author Vivek Pandey
 * @author Kohsuke Kawaguchi
 */
@Extension
public class OrganizationContainerImpl extends BlueOrganizationContainer {
    @Override
    public BlueOrganization get(String name) {
        if(name.equals(Jenkins.getInstance().getDisplayName().toLowerCase())) {
            return new OrganizationImpl();
        }

        Item item = Jenkins.getInstance().getItem(name);

        if(item instanceof JenkinsOrganizationFolder) {
            return new OrganizationImpl((JenkinsOrganizationFolder) item);
        }

        return null;
    }

    @Override
    public Iterator<BlueOrganization> iterator() {
        List<JenkinsOrganizationFolder> orgs = Jenkins.getInstance().getAllItems(JenkinsOrganizationFolder.class);
        List<BlueOrganization> ret = Lists.newArrayList();
        for (JenkinsOrganizationFolder org : orgs) {
            ret.add(new OrganizationImpl(org));
        }

        ret.add(new OrganizationImpl());

        return ret.iterator();

    }

    /**
     * Creates a new JenkinsOrganizationFolder.
     *
     * If trying to create something that already exists, it will return success.
     *
     * @param createRequest
     * @return
     */
    @Override
    public BlueOrganizationCreateResponse createOrganization(@JsonBody BlueOrganizationCreateRequest createRequest) {
        Jenkins j = Jenkins.getInstance();
        if(createRequest == null || Strings.isNullOrEmpty(createRequest.getName())) {
            throw new ServiceException.BadRequestExpception("Organization name missing.");
        }

        // Check to see if the organization makes the catch-all org.
        if(createRequest.getName().equals(j.getDisplayName().toLowerCase())) {
            return new BlueOrganizationCreateResponse(j.getDisplayName().toLowerCase());
        }

        // Check to see if it already exists
        Item item = j.getItem(createRequest.getName());
        if(item != null) {
            if(item instanceof JenkinsOrganizationFolder) {
                // Org already exists, nothing needed to be done.
                return new BlueOrganizationCreateResponse(createRequest.getName());
            } else {
                throw new ServiceException.BadRequestExpception("A non-organization item with this name already exists. Please rename it and try again.");
            }
        }

        // Create the folder.

        try {
            j.createProject(JenkinsOrganizationFolder.class, createRequest.getName());
        } catch (IOException e) {
            throw new ServiceException.UnexpectedErrorException("Error created organization", e);
        }

        return new BlueOrganizationCreateResponse(createRequest.getName());
    }
}
