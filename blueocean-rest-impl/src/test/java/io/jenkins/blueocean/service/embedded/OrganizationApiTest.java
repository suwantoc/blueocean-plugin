package io.jenkins.blueocean.service.embedded;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Ivan Meredith
 */
public class OrganizationApiTest extends BaseTest {
    @Test
    public void organizationUsers() {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        hudson.model.User alice = j.jenkins.getUser("alice");
        alice.setFullName("Alice Cooper");

        List users = request().authAlice().get("/organizations/jenkins/users/").build(List.class);

        Assert.assertEquals(users.size(), 1);
        Assert.assertEquals(((Map)users.get(0)).get("id"), "alice");
    }


    @Test
    public void createOrganiztion() throws Exception {
        Map r = request().post("/organizations/").data(ImmutableMap.of("name", "testOrg")).build(Map.class);
        Assert.assertEquals(r.get("name"), "testOrg");

        List l = request().get("/organizations/").build(List.class);

        Assert.assertEquals(l.size(), 2);

        for (Object o : l) {
            String name = (String)((Map)o).get("name");
            r = request().get("/organizations/" + name + "/").build(Map.class);
            Assert.assertEquals(name, r.get("name"));
        }
    }

    @Test
    public void deleteOrganization() throws Exception {
        Map r = request().post("/organizations/").data(ImmutableMap.of("name", "testOrg")).build(Map.class);
        Assert.assertEquals(r.get("name"), "testOrg");

        List l = request().get("/organizations/").build(List.class);

        Assert.assertEquals(l.size(), 2);

        request().delete("/organizations/testOrg/delete").build(String.class);

        l = request().get("/organizations/").build(List.class);

        Assert.assertEquals(l.size(), 1);
    }

}
