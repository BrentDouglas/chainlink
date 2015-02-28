package io.machinecode.chainlink.rt.glassfish;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import io.machinecode.chainlink.core.schema.xml.XmlDeployment;
import io.machinecode.chainlink.core.schema.xml.XmlJobOperator;
import io.machinecode.chainlink.core.schema.xml.subsystem.XmlChainlinkSubSystem;
import org.apache.commons.codec.binary.Base64;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@RunAsClient
@RunWith(Arquillian.class)
public class GlassfishManagementTest extends Assert {

    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(JavaArchive.class, GlassfishManagementTest.class.getSimpleName() + ".jar");
    }

    final Base64 base64 = new Base64(true);

    private String readBase64(final String file) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final InputStream in = GlassfishManagementTest.class.getClassLoader().getResourceAsStream(file);
        int c;
        while ((c = in.read()) != -1) {
            out.write(c);
        }
        return base64.encodeAsString(out.toByteArray());
    }

    private static InputStream asStream(final JsonNode stream) throws IOException, JSONException {
        return new ByteArrayInputStream(asString(stream).getBytes(StandardCharsets.UTF_8));
    }

    private static String asString(final JsonNode stream) throws IOException, JSONException {
        return stream.getObject().getString("message");
    }

    @Test
    public void testGetChainlink() throws Exception {
        postChainlink();
        final HttpResponse<JsonNode> resp = Unirest.get("http://localhost:4848/management/domain/get-chainlink")
                .header("Accept", "application/json")
                .asJson();
        assertEquals(200, resp.getStatus());
        final XmlChainlinkSubSystem ret = XmlChainlinkSubSystem.read(asStream(resp.getBody()));
        assertNotNull(ret);
    }

    @Test
    public void testGetJobOperator() throws Exception {
        postChainlink();
        final HttpResponse<JsonNode> resp = Unirest.get("http://localhost:4848/management/domain/get-chainlink-job-operator")
                .header("Accept", "application/json")
                .queryString("job-operator", "global")
                .asJson();
        assertEquals(200, resp.getStatus());
        final XmlJobOperator ret = XmlJobOperator.read(asStream(resp.getBody()));
        assertNotNull(ret);
    }

    @Test
    public void testGetDeployment() throws Exception {
        postChainlink();
        final HttpResponse<JsonNode> resp = Unirest.get("http://localhost:4848/management/domain/get-chainlink-deployment")
                .header("Accept", "application/json")
                .queryString("deployment", "default")
                .asJson();
        assertEquals(200, resp.getStatus());
        final XmlDeployment ret = XmlDeployment.read(asStream(resp.getBody()));
        assertNotNull(ret);
    }

    @Test
    public void testGetDeploymentJobOperator() throws Exception {
        postChainlink();
        {
            final HttpResponse<JsonNode> resp = Unirest.get("http://localhost:4848/management/domain/get-chainlink-deployment-job-operator")
                    .header("Accept", "application/json")
                    .queryString("deployment", "default")
                    .queryString("job-operator", "default")
                    .asJson();
            assertEquals(200, resp.getStatus());
            final XmlJobOperator ret = XmlJobOperator.read(asStream(resp.getBody()));
            assertNotNull(ret);
        }
    }

    @Test
    public void testPostChainlink() throws Exception {
        final XmlChainlinkSubSystem ret = XmlChainlinkSubSystem.read(asStream(postChainlink()));
        assertNotNull(ret);
    }

    @Test
    public void testPostJobOperator() throws Exception {
        postChainlink();
        {
            final HttpResponse<JsonNode> resp = Unirest.post("http://localhost:4848/management/domain/set-chainlink-job-operator")
                    .header("Accept", "application/json")
                    .header("X-Requested-By", "GlassFish REST HTML interface")
                    .field("base64", readBase64("default-job-operator.xml"))
                    .asJson();
            assertEquals(200, resp.getStatus());
            assertEquals("", asString(resp.getBody()));
        }
        {
            final HttpResponse<JsonNode> resp = Unirest.post("http://localhost:4848/management/domain/set-chainlink-job-operator")
                    .header("Accept", "application/json")
                    .header("X-Requested-By", "GlassFish REST HTML interface")
                    .field("base64", readBase64("global-job-operator.xml"))
                    .asJson();
            assertEquals(200, resp.getStatus());
            final XmlJobOperator ret = XmlJobOperator.read(asStream(resp.getBody()));
            assertNotNull(ret);
        }
    }

    @Test
    public void testPostDeployment() throws Exception {
        postChainlink();
        final HttpResponse<JsonNode> resp = Unirest.post("http://localhost:4848/management/domain/set-chainlink-deployment")
                .header("Accept", "application/json")
                .header("X-Requested-By", "GlassFish REST HTML interface")
                .field("base64", readBase64("default-deployment.xml"))
                .asJson();
        assertEquals(200, resp.getStatus());
        final XmlDeployment ret = XmlDeployment.read(asStream(resp.getBody()));
        assertNotNull(ret);
    }

    @Test
    public void testPostDeploymentJobOperator() throws Exception {
        postChainlink();
        {
            final HttpResponse<JsonNode> resp = Unirest.post("http://localhost:4848/management/domain/set-chainlink-deployment-job-operator")
                    .header("Accept", "application/json")
                    .header("X-Requested-By", "GlassFish REST HTML interface")
                    .field("deployment", "default")
                    .field("base64", readBase64("global-job-operator.xml"))
                    .asJson();
            assertEquals(200, resp.getStatus());
            assertEquals("", asString(resp.getBody()));
        }
        {
            final HttpResponse<JsonNode> resp = Unirest.post("http://localhost:4848/management/domain/set-chainlink-deployment-job-operator")
                    .header("Accept", "application/json")
                    .header("X-Requested-By", "GlassFish REST HTML interface")
                    .field("deployment", "default")
                    .field("base64", readBase64("default-job-operator.xml"))
                    .asJson();
            assertEquals(200, resp.getStatus());
            final XmlJobOperator ret = XmlJobOperator.read(asStream(resp.getBody()));
            assertNotNull(ret);
        }
    }

    @Test
    public void testDeleteChainlink() throws Exception {
        postChainlink();
        final HttpResponse<JsonNode> resp = Unirest.delete("http://localhost:4848/management/domain/delete-chainlink")
                .header("Accept", "application/json")
                .header("X-Requested-By", "GlassFish REST HTML interface")
                .asJson();
        assertEquals(200, resp.getStatus());
        final XmlChainlinkSubSystem ret = XmlChainlinkSubSystem.read(asStream(resp.getBody()));
        assertNotNull(ret);
    }

    @Test
    public void testDeleteJobOperator() throws Exception {
        postChainlink();
        final HttpResponse<JsonNode> resp = Unirest.delete("http://localhost:4848/management/domain/delete-chainlink-job-operator")
                .header("Accept", "application/json")
                .header("X-Requested-By", "GlassFish REST HTML interface")
                .queryString("job-operator", "global")
                .asJson();
        assertEquals(200, resp.getStatus());
        final XmlJobOperator ret = XmlJobOperator.read(asStream(resp.getBody()));
        assertNotNull(ret);
    }

    @Test
    public void testDeleteDeployment() throws Exception {
        postChainlink();
        final HttpResponse<JsonNode> resp = Unirest.delete("http://localhost:4848/management/domain/delete-chainlink-deployment")
                .header("Accept", "application/json")
                .header("X-Requested-By", "GlassFish REST HTML interface")
                .queryString("deployment", "default")
                .asJson();
        assertEquals(200, resp.getStatus());
        final XmlDeployment ret = XmlDeployment.read(asStream(resp.getBody()));
        assertNotNull(ret);
    }

    @Test
    public void testDeleteDeploymentJobOperator() throws Exception {
        postChainlink();
        final HttpResponse<JsonNode> resp = Unirest.delete("http://localhost:4848/management/domain/delete-chainlink-deployment-job-operator")
                .header("Accept", "application/json")
                .header("X-Requested-By", "GlassFish REST HTML interface")
                .queryString("deployment", "default")
                .queryString("job-operator", "default")
                .asJson();
        assertEquals(200, resp.getStatus());
        final XmlJobOperator ret = XmlJobOperator.read(asStream(resp.getBody()));
        assertNotNull(ret);
    }

    private JsonNode postChainlink() throws Exception {
        final HttpResponse<JsonNode> resp = Unirest.post("http://localhost:4848/management/domain/set-chainlink")
                .header("Accept", "application/json")
                .header("X-Requested-By", "GlassFish REST HTML interface")
                .field("base64", readBase64("subsystem.xml"))
                .asJson();
        assertEquals(200, resp.getStatus());
        return resp.getBody();
    }
}
