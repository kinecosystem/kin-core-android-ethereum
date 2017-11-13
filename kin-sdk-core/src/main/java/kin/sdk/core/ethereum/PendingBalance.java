package kin.sdk.core.ethereum;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import kin.sdk.core.Balance;
import kin.sdk.core.exception.OperationFailedException;
import kin.sdk.core.impl.BalanceImpl;
import org.ethereum.geth.Account;
import org.ethereum.geth.Address;
import org.ethereum.geth.Addresses;
import org.ethereum.geth.Context;
import org.ethereum.geth.EthereumClient;
import org.ethereum.geth.FilterQuery;
import org.ethereum.geth.Geth;
import org.ethereum.geth.Hash;
import org.ethereum.geth.Hashes;
import org.ethereum.geth.Log;
import org.ethereum.geth.Logs;
import org.ethereum.geth.Topics;

/**
 * Calculate pending balance based on current balance, using ethereum contracts events/logs mechanism to iterate all
 * relevant transactions both outgoing and incoming, and summing all transactions amounts.
 */
final class PendingBalance {

    private final EthereumClient ethereumClient;
    private final Context gethContext;

    PendingBalance(EthereumClient ethereumClient, Context gethContext) {
        this.ethereumClient = ethereumClient;
        this.gethContext = gethContext;
    }

    Balance calculate(Account account, Balance balance) throws OperationFailedException {
        try {
            String accountAddressHex = account.getAddress().getHex();

            BigInteger pendingSpentAmount = getPendingSpentAmount(accountAddressHex);
            BigInteger pendingEarnAmount = getPendingEarnAmount(accountAddressHex);

            BigDecimal totalPendingAmount = KinConverter.toKin(pendingEarnAmount.subtract(pendingSpentAmount));
            return new BalanceImpl(balance.value().add(totalPendingAmount));
        } catch (Exception e) {
            throw new OperationFailedException(e);
        }
    }

    private BigInteger getPendingSpentAmount(String accountAddressHex) throws Exception {
        Logs pendingSpentLogs = getPendingTransactionsLogs(accountAddressHex, null);
        return sumTransactionsAmount(pendingSpentLogs);
    }

    private BigInteger getPendingEarnAmount(String accountAddressHex) throws Exception {
        Logs pendingEarnLogs = getPendingTransactionsLogs(null, accountAddressHex);
        return sumTransactionsAmount(pendingEarnLogs);
    }

    private Logs getPendingTransactionsLogs(@Nullable String fromHexAddress, @Nullable String toHexAddress)
        throws OperationFailedException {
        try {
            Address kinContractAddress = Geth.newAddressFromHex(KinConsts.CONTRACT_ADDRESS_HEX);
            Addresses addresses = Geth.newAddressesEmpty();
            addresses.append(kinContractAddress);

            Topics topics = createFilterLogTopicsArray(fromHexAddress, toHexAddress);

            FilterQuery filterQuery = new FilterQuery();
            filterQuery.setAddresses(addresses);
            filterQuery.setFromBlock(Geth.newBigInt(Geth.LatestBlockNumber));
            filterQuery.setToBlock(Geth.newBigInt(Geth.PendingBlockNumber));
            filterQuery.setTopics(topics);

            return ethereumClient.filterLogs(gethContext, filterQuery);
        } catch (Exception e) {
            throw new OperationFailedException(e);
        }
    }

    /**
     * @param fromHexAddress filter transaction by 'from' hex address, use null for any 'from' address (no filter)
     * @param toHexAddress filter transaction by 'to' hex address, use null for any 'to' address (no filter)
     */
    @NonNull
    private Topics createFilterLogTopicsArray(@Nullable String fromHexAddress, @Nullable String toHexAddress)
        throws Exception {
        //Topics array is 32 bytes array, the first position is topic event signature hash,
        // the rest (up to 3 params) are indexed (= filterable) parameters for the desired event, in our case, transfer indexed params are 'from address' and 'to address'
        // in this order,  param can be 32 bytes hex value representing address, or null, to allow any address (no filter).
        // so if we want to filter by To address only, the first parameter will be null, and the second param will be 32 byte To address.
        // see https://github.com/ethereum/wiki/wiki/Ethereum-Contract-ABI#events
        Topics topics = Geth.newTopicsEmpty();
        Hashes hashes = Geth.newHashesEmpty();
        hashes.append(Geth.newHashFromHex(KinConsts.TOPIC_EVENT_NAME_SHA3_TRANSFER));
        topics.append(hashes);
        hashes = Geth.newHashesEmpty();
        if (fromHexAddress != null) {
            hashes.append(hexAddressToTopicHash(fromHexAddress));
        }
        topics.append(hashes);
        hashes = Geth.newHashesEmpty();
        if (toHexAddress != null) {
            hashes.append(hexAddressToTopicHash(toHexAddress));
        }
        topics.append(hashes);
        return topics;
    }

    private Hash hexAddressToTopicHash(String hexAddress) throws Exception {
        //add leading zeros to match 32 bytes
        // hex address are 40 chars (20 bytes), topic data is 64 chars (32 bytes)
        return Geth.newHashFromHex("0x000000000000000000000000" + hexAddress.substring(2));
    }

    private BigInteger sumTransactionsAmount(Logs logs) throws Exception {
        BigInteger totalAmount = BigInteger.ZERO;
        for (int i = 0; i < logs.size(); i++) {
            Log log = logs.get(i);
            String txHash = log.getTxHash().getHex();
            if (txHash != null) {
                //use big integer to construct
                totalAmount = totalAmount.add(new BigInteger(log.getData()));
            }
        }
        return totalAmount;
    }

}
