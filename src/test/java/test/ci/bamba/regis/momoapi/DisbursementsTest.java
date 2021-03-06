/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package test.ci.bamba.regis.momoapi;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import ci.bamba.regis.Disbursements;
import ci.bamba.regis.Environment;
import ci.bamba.regis.MoMo;
import ci.bamba.regis.SandboxProvisioning;
import ci.bamba.regis.exceptions.RequestException;
import io.reactivex.disposables.Disposable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class DisbursementsTest extends BaseTest {

    private Disposable disposable;

    public void init() {
        this.type = "disbursements";
        super.init();
    }

    @After
    public void takeDown() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Test
    public void testDisbursementsCreateTokenSuccess() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey)
                .flatMap(
                        apiCredentials -> {
                            Disbursements disbursements = momo.createDisbursements(
                                    subscriptionKey, apiCredentials.getUser(), apiCredentials.getKey()
                            );
                            assertEquals(disbursements.getApiCredentials().getUser(), apiCredentials.getUser());
                            assertEquals(disbursements.getApiCredentials().getKey(), apiCredentials.getKey());
                            assertNotNull(disbursements.getBaseUrl());
                            return disbursements.createToken();
                        }
                ).subscribe(token -> {
                    assertNotNull(token);
                    assertNotEquals(0, token.getAccessToken().length());
                    assertEquals(3600, token.getExpiresIn());
                    assertEquals("access_token", token.getTokenType());
                });
    }

    @Test
    public void testDisbursementsCreateTokenError() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey)
                .flatMap(
                        apiCredentials -> {
                            Disbursements disbursements = momo.createDisbursements(
                                    subscriptionKey, "err" + apiCredentials.getUser(), apiCredentials.getKey()
                            );
                            return disbursements.createToken();
                        }
                ).subscribe(
                        Assert::assertNull,
                        throwable -> {
                            RequestException e = (RequestException) throwable;
                            assertEquals(500, e.getCode());
                        }
                );
    }


    @Test
    public void testDisbursementsTransferSuccess() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey)
                .flatMap(
                        apiCredentials -> {
                            Disbursements disbursements = momo.createDisbursements(
                                subscriptionKey, apiCredentials.getUser(), apiCredentials.getKey()
                            );
                            return disbursements.transfer(900, "EUR", "test", "0022505777777", "test", "test");
                        }
                ).subscribe(Assert::assertNotNull);
    }

    @Test
    public void testDisbursementsTransferError() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey)
            .flatMap(
                apiCredentials -> {
                    Disbursements disbursements = momo.createDisbursements(
                        subscriptionKey, apiCredentials.getUser(), apiCredentials.getKey()
                    );
                    return disbursements.transfer(900, "USD", "test", "0022505777777", "test", "test");
                }
            )
            .subscribe(
                referenceId -> fail("request succeeded but should not."),
                throwable -> {
                    RequestException e = (RequestException) throwable;
                    assertNotNull(e);
                    assertEquals(500, e.getCode());
                }
            );
    }

    @Test
    public void  testDisbursementsGetTransferSuccess() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey)
                .flatMap(
                        apiCredentials -> {
                            Disbursements disbursements = momo.createDisbursements(
                                    subscriptionKey, apiCredentials.getUser(), apiCredentials.getKey()
                            );
                            return disbursements.transfer(
                                    900, "EUR", "test",
                                    "0022505777777", "test", "test"
                            ).flatMap(disbursements::getTransfer);
                        }
                ).subscribe(disbursementsTransfer -> {
                    assertNotNull(disbursementsTransfer);
                    assertEquals(900, disbursementsTransfer.getAmount(), 0);
                    assertEquals("0022505777777", disbursementsTransfer.getPayee().getPartyId());
                });
    }

    @Test
    public void  testDisbursementsGetTransferError() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey)
                .flatMap(
                        apiCredentials -> {
                            Disbursements disbursements = momo.createDisbursements(
                                    subscriptionKey, apiCredentials.getUser(), apiCredentials.getKey()
                            );
                            return disbursements.transfer(
                                    900, "EUR", "test",
                                    "0022505777777", "test", "test"
                            ).flatMap(referenceId -> disbursements.getTransfer("err" + referenceId));
                        }
                ).subscribe(transfer -> {
                    fail("should not be here");
                }, throwable -> {
                    RequestException e = (RequestException) throwable;
                    assertNotNull(e);
                    assertEquals(400, e.getCode());
                });
    }

    @Test
    public void testDisbursementsGetAccountBalance() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey)
                .flatMap(
                        apiCredentials -> {
                            Disbursements disbursements = momo.createDisbursements(
                                    subscriptionKey, apiCredentials.getUser(), apiCredentials.getKey()
                            );
                            return disbursements.getAccountBalance();
                        }
                ).subscribe(
                    Assert::assertNotNull,
                    throwable -> {
                        RequestException e = (RequestException) throwable;
                        assertNotNull(e);
                    }
                );
    }

    @Test
    public void testDisbursementsGetAccountStatus() {
        MoMo momo = new MoMo(Environment.SANDBOX);
        SandboxProvisioning provisioning = momo.createSandboxProvisioning(subscriptionKey);
        disposable = provisioning.createApiUser().flatMap(provisioning::createApiKey)
                .flatMap(
                        apiCredentials -> {
                            Disbursements disbursements = momo.createDisbursements(
                                    subscriptionKey, apiCredentials.getUser(), apiCredentials.getKey()
                            );
                            return disbursements.getAccountStatus("46733123453");
                        }
                ).subscribe(
                    Assert::assertNotNull,
                    throwable -> {
                        RequestException e = (RequestException) throwable;
                        assertNotNull(e);
                    }
                );
    }
}
