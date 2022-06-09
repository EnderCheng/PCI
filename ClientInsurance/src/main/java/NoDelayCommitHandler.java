import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.spi.CommitHandler;
import org.hyperledger.fabric.gateway.spi.CommitListener;
import org.hyperledger.fabric.gateway.spi.PeerDisconnectEvent;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.Peer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class NoDelayCommitHandler implements CommitHandler {
    private final Network network;
    private final Set<Peer> peers;
    private final String transactionId;
    private final CountDownLatch completeLatch = new CountDownLatch(1);
    private final CommitListener listener = new CommitListener() {
        @Override
        public void acceptCommit(BlockEvent.TransactionEvent transactionEvent) {
            acceptEvent(transactionEvent.getPeer());
            System.out.println("accept Commit");
        }

        @Override
        public void acceptDisconnect(PeerDisconnectEvent disconnectEvent) {
            acceptEvent(disconnectEvent.getPeer());
            System.out.println("accept Disconnect");
        }
    };

    public NoDelayCommitHandler(Network network, Collection<Peer> peers, String transactionId) {
        this.network = network;
        this.peers = Collections.synchronizedSet(new HashSet<>(peers));
        this.transactionId = transactionId;

        if (this.peers.isEmpty()) {
            throw new IllegalArgumentException("No peers specified");
        }
    }

    @Override
    public void startListening() {
        network.addCommitListener(listener, peers, transactionId);
    }

    @Override
    public void waitForEvents(long timeout, TimeUnit timeUnit) throws TimeoutException, InterruptedException {
        cancelListening();
        try {
            boolean complete = completeLatch.await(timeout, timeUnit);
            if (!complete) {
                throw new TimeoutException("Timeout waiting for commit of transaction " + transactionId);
            }
        } finally {
            cancelListening();
        }
    }

    @Override
    public void cancelListening() {
        peers.clear();
        network.removeCommitListener(listener);
        completeLatch.countDown();
    }

    private void acceptEvent(Peer peer) {
        System.out.println("acceptEvent:"+peer.getName());
        if (peers.remove(peer) && peers.isEmpty()) {
            cancelListening();
        }
    }
}
