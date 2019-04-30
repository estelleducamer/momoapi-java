/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package test.ci.bamba.regis.momoapi;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import ci.bamba.regis.Collections;
import ci.bamba.regis.Environment;
import ci.bamba.regis.MoMo;
import ci.bamba.regis.SandboxProvisioning;
import ci.bamba.regis.exceptions.RequestException;
import io.reactivex.disposables.Disposable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class CollectionsTest extends BaseTest {

    private Disposable disposable;

    public void init() {
        this.type = "collections";
        super.init();
    }

    @After
    public void takeDown() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Test
    public void testCollectionsCreateTokenSuccess() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey).flatMap(apiCredentials -> {
            Collections collections = momo.createCollections(subscriptionKey, apiCredentials.getUser(),
                    apiCredentials.getKey());
            assertEquals(collections.getApiCredentials().getUser(), apiCredentials.getUser());
            assertEquals(collections.getApiCredentials().getKey(), apiCredentials.getKey());
            assertNotNull(collections.getBaseUrl());
            return collections.createToken();
        }).subscribe(token -> {
            assertNotNull(token);
            assertNotEquals(0, token.getAccessToken().length());
            assertEquals(3600, token.getExpiresIn());
            assertEquals("access_token", token.getTokenType());
        });
    }

    @Test
    public void testCollectionsCreateTokenError() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey).flatMap(apiCredentials -> {
            Collections collections = momo.createCollections(subscriptionKey, "err" + apiCredentials.getUser(),
                    apiCredentials.getKey());
            return collections.createToken();
        }).subscribe(Assert::assertNull, throwable -> {
            RequestException e = (RequestException) throwable;
            assertEquals(500, e.getCode());
        });
    }

    @Test
    public void testCollectionsRequestToPaySuccess() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey).flatMap(apiCredentials -> {
            Collections collections = momo.createCollections(subscriptionKey, apiCredentials.getUser(),
                    apiCredentials.getKey());
            return collections.requestToPay(900, "EUR", "test", "0022505777777", "test", "test");
        }).subscribe(Assert::assertNotNull);
    }

    @Test
    public void testCollectionsRequestToPayError() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey).flatMap(apiCredentials -> {
            Collections collections = momo.createCollections(subscriptionKey, apiCredentials.getUser(),
                    apiCredentials.getKey());
            return collections.requestToPay(900, "USD", "test", "0022505777777", "test", "test");
        }).subscribe(referenceId -> fail("request succeeded but should not."), throwable -> {
            RequestException e = (RequestException) throwable;
            assertNotNull(e);
            assertEquals(500, e.getCode());
        });
    }

    @Test
    public void testCollectionsGetRequestToPaySuccess() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey).flatMap(apiCredentials -> {
            Collections collections = momo.createCollections(subscriptionKey, apiCredentials.getUser(),
                    apiCredentials.getKey());
            return collections.requestToPay(900, "EUR", "test", "0022505777777", "test", "test")
                    .flatMap(collections::getRequestToPay);
        }).subscribe(collectionsRequestToPay -> {
            assertNotNull(collectionsRequestToPay);
            assertEquals(900, collectionsRequestToPay.getAmount(), 0);
            assertEquals("0022505777777", collectionsRequestToPay.getPayer().getPartyId());
        });
    }

    @Test
    public void testCollectionsGetRequestToPayError() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey).flatMap(apiCredentials -> {
            Collections collections = momo.createCollections(subscriptionKey, apiCredentials.getUser(),
                    apiCredentials.getKey());
            return collections.requestToPay(900, "EUR", "test", "0022505777777", "test", "test")
                    .flatMap(referenceId -> collections.getRequestToPay("err" + referenceId));
        }).subscribe(requestToPay -> {
            fail("should not be here");
        }, throwable -> {
            RequestException e = (RequestException) throwable;
            assertNotNull(e);
            assertEquals(400, e.getCode());
        });
    }

    @Test
    public void testCollectionsGetAccountBalance() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey).flatMap(apiCredentials -> {
            Collections collections = momo.createCollections(subscriptionKey, apiCredentials.getUser(),
                    apiCredentials.getKey());
            return collections.getAccountBalance();
        }).subscribe(Assert::assertNotNull, throwable -> {
            RequestException e = (RequestException) throwable;
            assertNotNull(e);
        });
    }

    @Test
    public void testCollectionsGetAccountStatus() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey).flatMap(apiCredentials -> {
            Collections collections = momo.createCollections(subscriptionKey, apiCredentials.getUser(),
                    apiCredentials.getKey());
            return collections.getAccountStatus("46733123453");
        }).subscribe(Assert::assertNotNull, throwable -> {
            RequestException e = (RequestException) throwable;
            assertNotNull(e);
        });
    }
}
