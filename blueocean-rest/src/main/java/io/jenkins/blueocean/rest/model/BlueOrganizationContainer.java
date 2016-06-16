package io.jenkins.blueocean.rest.model;

import hudson.ExtensionPoint;
import io.jenkins.blueocean.rest.ApiRoutable;
import org.kohsuke.stapler.WebMethod;
import org.kohsuke.stapler.json.JsonResponse;
import org.kohsuke.stapler.verb.DELETE;
import org.kohsuke.stapler.verb.POST;

/**
 * This is the head of the blue ocean API.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class BlueOrganizationContainer extends Container<BlueOrganization> implements ApiRoutable, ExtensionPoint {

    @Override
    public final String getUrlName() {
        return "organizations";
    }


    /**
     * Creates an organization. If organization exists, return success.
     *
     * @param blueOrganizationCreateRequest Name of organization to be created
     * @return Name of the created organization
     */
    @POST @WebMethod(name = "") @JsonResponse
    public abstract BlueOrganizationCreateResponse createOrganization(BlueOrganizationCreateRequest blueOrganizationCreateRequest);

    /**
     * Request body wrapper for organization creation request
     */
    public static class BlueOrganizationCreateRequest {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Response body wrapper for organization creation request
     */
    public static class BlueOrganizationCreateResponse {
        private String name;

        public String getName() {
            return name;
        }

        public BlueOrganizationCreateResponse(String name) {
            this.name = name;
        }
    }
}
