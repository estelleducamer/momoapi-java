package ci.bamba.regis;

import java.util.Base64;
import java.util.UUID;

import ci.bamba.regis.exceptions.RequestException;
import ci.bamba.regis.models.AccountBalance;
import ci.bamba.regis.models.AccountStatus;
import ci.bamba.regis.models.ApiCredentials;
import ci.bamba.regis.models.Token;
import io.reactivex.Observable;

public class Product {

    private ApiCredentials apiCredentials;
    private String baseUrl;
    private String subscriptionKey;
    private Environment environment;

    Product(String baseUrl, Environment environment, String subscriptionKey, String apiUser, String apiKey) {
        this.baseUrl = baseUrl;
        this.environment = environment;
        this.subscriptionKey = subscriptionKey;
        this.apiCredentials = new ApiCredentials(apiUser, apiKey);
    }

    public ApiCredentials getApiCredentials() {
        return apiCredentials;
    }

    public String getSubscriptionKey() {
        return subscriptionKey;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    Observable<Token> createToken(String type) {
        return createToken(type, this.getApiCredentials().getUser(), this.getApiCredentials().getKey());
    }

    private Observable<Token> createToken(String type, String apiUser, String apiKey) {
        byte[] encodedBytes = Base64.getEncoder().encode((apiUser + ":" + apiKey).getBytes());
        String authorization = "Basic " + new String(encodedBytes);
        return RestClient.getService(getBaseUrl()).createToken(authorization, getSubscriptionKey(), type)
                .map(response -> {
                    if (response.code() == 200) {
                        return response.body();
                    } else {
                        throw new RequestException(response.code(), response.message());
                    }
                });
    }

    protected String getUUID() {
        return UUID.randomUUID().toString();
    }

    protected String getAuthHeader(String token) {
        return String.format("Bearer %s", token);
    }

    protected Observable<AccountBalance> getAccountBalance(String type, String token) {
        return RestClient.getService(getBaseUrl())
                .getAccountBalance(type, getAuthHeader(token), getSubscriptionKey(), getEnvironment().getEnv())
                .map(response -> {
                    if (response.code() == 200) {
                        return response.body();
                    } else {
                        throw new RequestException(response.code(), response.message());
                    }
                });
    }

    public Observable<AccountStatus> getAccountStatus(String type, String token, String msisdn) {
        return RestClient.getService(getBaseUrl()).getAccountStatus(type, getAuthHeader(token), getSubscriptionKey(),
                getEnvironment().getEnv(), "msisdn", msisdn).map(response -> {
                    if (response.code() == 200) {
                        return response.body();
                    } else {
                        throw new RequestException(response.code(), response.message());
                    }
                });
    }

}