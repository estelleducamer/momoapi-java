package ci.bamba.regis;

import ci.bamba.regis.exceptions.RequestException;
import ci.bamba.regis.models.AccountBalance;
import ci.bamba.regis.models.AccountStatus;
import ci.bamba.regis.models.RemittancesTransfer;
import ci.bamba.regis.models.RemittancesTransferBodyRequest;
import ci.bamba.regis.models.Token;
import io.reactivex.Observable;

public class Remittances extends Product {

    private static final String TYPE = "remittance";

    Remittances(String baseUrl, Environment environment, String subscriptionKey, String apiUser, String apiKey) {
        super(baseUrl, environment, subscriptionKey, apiUser, apiKey);
    }

    public Observable<Token> createToken() {
        return super.createToken(TYPE);
    }

    public Observable<String> transfer(float amount, String currency, String externalId, String payeePartyId,
            String payerMessage, String payeeNote) {
        return createToken().flatMap(token -> transfer(token.getAccessToken(), amount, currency, externalId,
                payeePartyId, payerMessage, payeeNote));
    }

    public Observable<String> transfer(String token, float amount, String currency, String externalId,
            String payeePartyId, String payerMessage, String payeeNote) {
        RemittancesTransferBodyRequest body = new RemittancesTransferBodyRequest(String.format("%s", amount), currency,
                externalId, payeePartyId, payerMessage, payeeNote);
        String referenceId = getUUID();
        return RestClient.getService(getBaseUrl()).remittancesCreateTransfer(getAuthHeader(token), getSubscriptionKey(),
                referenceId, getEnvironment().getEnv(), body).map(response -> {
                    if (response.code() == 202) {
                        return referenceId;
                    } else {
                        throw new RequestException(response.code(), response.message());
                    }
                });
    }

    public Observable<RemittancesTransfer> getTransfer(String referenceId) {
        return createToken().flatMap(token -> getTransfer(token.getAccessToken(), referenceId));
    }

    public Observable<RemittancesTransfer> getTransfer(String token, String referenceId) {
        return RestClient.getService(getBaseUrl()).remittancesGetTransfer(getAuthHeader(token), getSubscriptionKey(),
                getEnvironment().getEnv(), referenceId).map(response -> {
                    if (response.code() == 200) {
                        return response.body();
                    } else {
                        throw new RequestException(response.code(), response.message());
                    }
                });
    }

    public Observable<AccountBalance> getAccountBalance() {
        return createToken().flatMap(token -> getAccountBalance(token.getAccessToken()));
    }

    public Observable<AccountBalance> getAccountBalance(String token) {
        return super.getAccountBalance(TYPE, token);
    }

    public Observable<AccountStatus> getAccountStatus(String msisdn) {
        return createToken().flatMap(token -> getAccountStatus(token.getAccessToken(), msisdn));
    }

    public Observable<AccountStatus> getAccountStatus(String token, String msisdn) {
        return super.getAccountStatus(TYPE, token, msisdn);
    }
}
