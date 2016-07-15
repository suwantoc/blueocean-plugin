package io.jenkins.blueocean.service.embedded.rest;

import com.google.common.collect.Lists;
import hudson.Extension;
import hudson.model.Item;
import io.jenkins.blueocean.commons.ServiceException;
import io.jenkins.blueocean.rest.ApiHead;
import io.jenkins.blueocean.rest.hal.Link;
import io.jenkins.blueocean.rest.model.BlueOrganization;
import io.jenkins.blueocean.rest.model.BlueOrganizationContainer;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.jenkinsorganizations.JenkinsOrganizationFolder;

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
        if(OrganizationImpl.DEFAULT_ORGANIZATION.getName().equals(name)){
            return OrganizationImpl.DEFAULT_ORGANIZATION;
        } else {
            Item item = Jenkins.getInstance().getItem(name);
            if(item instanceof JenkinsOrganizationFolder) {
                return new OrganizationImpl((JenkinsOrganizationFolder) item);
            } else {
                return null;
            }
        }
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

    @Override
    public Link getLink() {
        return ApiHead.INSTANCE().getLink().rel("organizations");
    }
}
