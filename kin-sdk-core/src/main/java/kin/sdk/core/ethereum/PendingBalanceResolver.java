package kin.sdk.core.ethereum;


import android.support.annotation.Nullable;

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

import java.math.BigDecimal;
import java.math.BigInteger;

import kin.sdk.core.Balance;
import kin.sdk.core.exception.OperationFailedException;
import kin.sdk.core.impl.BalanceImpl;

final class PendingBalanceResolver {
    private final EthereumClient ethereumClient;
    private final Context gethContext;

    PendingBalanceResolver(EthereumClient ethereumClient, Context gethContext) {
        this.ethereumClient = ethereumClient;
        this.gethContext = gethContext;
    }

    Balance resolvePendingBalance(Account account, Balance balance) throws OperationFailedException {
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

    private Logs getPendingTransactionsLogs(@Nullable String fromHex, @Nullable String toHex) throws OperationFailedException {
        try {
            Address kinContractAddress = Geth.newAddressFromHex(KinConsts.CONTRACT_ADDRESS_HEX);
            FilterQuery filterQuery = new FilterQuery();
            Addresses addresses = Geth.newAddressesEmpty();
            addresses.append(kinContractAddress);

            filterQuery.setAddresses(addresses);
            filterQuery.setFromBlock(Geth.newBigInt(Geth.LatestBlockNumber));
            filterQuery.setToBlock(Geth.newBigInt(Geth.PendingBlockNumber));
            Topics topics = Geth.newTopicsEmpty();
            Hashes hashes = Geth.newHashesEmpty();
            hashes.append(Geth.newHashFromHex(KinConsts.TOPIC_EVENT_NAME_SHA3_TRANSFER));
            topics.append(hashes);
            hashes = Geth.newHashesEmpty();
            if (fromHex != null) {
                hashes.append(hexAddressToTopicHash(fromHex));
            }
            topics.append(hashes);
            hashes = Geth.newHashesEmpty();
            if (toHex != null) {
                hashes.append(hexAddressToTopicHash(toHex));
            }
            topics.append(hashes);
            filterQuery.setTopics(topics);

            return ethereumClient.filterLogs(gethContext, filterQuery);
        } catch (Exception e) {
            throw new OperationFailedException(e);
        }
    }

    private Hash hexAddressToTopicHash(String hexAddress) throws Exception {
        //hex address are 40 chars (20 bytes), topic hash are 64 chars (32 bytes), add leading zeros
        return Geth.newHashFromHex("0x000000000000000000000000" + hexAddress.substring(2));
    }

    private BigInteger sumTransactionsAmount(Logs logs) throws Exception {
        BigInteger totalAmount = BigInteger.ZERO;
        for (int i = 0; i < logs.size(); i++) {
            Log log = logs.get(i);
            String txHash = log.getTxHash().getHex();
            if (txHash != null) {
                totalAmount = totalAmount.add(new BigInteger(log.getData()));
            }
        }
        return totalAmount;
    }
}
