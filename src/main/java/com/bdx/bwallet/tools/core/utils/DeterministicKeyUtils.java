/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bdx.bwallet.tools.core.utils;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableList;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Nullable;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.spongycastle.math.ec.ECPoint;

/**
 *
 * @author Administrator
 */
@Deprecated
public class DeterministicKeyUtils {

	/** The 4 byte header that serializes in base58 to "xpub" */
    public static final int HEADER_PUB = 0x0488B21E;
    /** The 4 byte header that serializes in base58 to "xprv" */
    public static final int HEADER_PRIV = 0x0488ADE4;
	
    static byte[] addChecksum(byte[] input) {
        int inputLength = input.length;
        byte[] checksummed = new byte[inputLength + 4];
        System.arraycopy(input, 0, checksummed, 0, inputLength);
        byte[] checksum = Utils.doubleDigest(input);
        System.arraycopy(checksum, 0, checksummed, inputLength, 4);
        return checksummed;
    }
    
    static String toBase58(byte[] ser) {
        return Base58.encode(addChecksum(ser));
    }

    /**
     * Deserialize a base-58-encoded HD Key with no parent
     */
    public static DeterministicKey deserializeB58(String base58) {
        return deserializeB58(null, base58);
    }

    /**
     * Deserialize a base-58-encoded HD Key.
     *
     * @param parent The parent node in the given key's deterministic hierarchy.
     */
    public static DeterministicKey deserializeB58(@Nullable DeterministicKey parent, String base58) {
        try {
            return deserialize(parent, Base58.decodeChecked(base58));
        } catch (AddressFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Deserialize an HD Key with no parent
     */
    public static DeterministicKey deserialize(byte[] serializedKey) {
        return deserialize(null, serializedKey);
    }

    /**
     * Deserialize an HD Key.
     *
     * @param parent The parent node in the given key's deterministic hierarchy.
     */
    public static DeterministicKey deserialize(@Nullable DeterministicKey parent, byte[] serializedKey) {
        ByteBuffer buffer = ByteBuffer.wrap(serializedKey);
        int header = buffer.getInt();
        if (header != HEADER_PRIV && header != HEADER_PUB) {
            throw new IllegalArgumentException("Unknown header bytes: " + toBase58(serializedKey).substring(0, 4));
        }
        boolean pub = header == HEADER_PUB;
        byte depth = buffer.get();
        byte[] parentFingerprint = new byte[4];
        buffer.get(parentFingerprint);
        final int i = buffer.getInt();
        final ChildNumber childNumber = new ChildNumber(i);
        ImmutableList<ChildNumber> path;
        if (parent != null) {
            if (Arrays.equals(parentFingerprint, longTo4ByteArray(0))) {
                throw new IllegalArgumentException("Parent was provided but this key doesn't have one");
            }
//            if (!Arrays.equals(parent.getFingerprint(), parentFingerprint)) {
//                throw new IllegalArgumentException("Parent fingerprints don't match");
//            }
            path = HDUtils.append(parent.getPath(), childNumber);
            if (path.size() != depth) {
                throw new IllegalArgumentException("Depth does not match");
            }
        } else {
            if (depth >= 1) // We have been given a key that is not a root key, yet we lack any object representing the parent.
            // This can happen when deserializing an account key for a watching wallet. In this case, we assume that
            // the client wants to conceal the key's position in the hierarchy. The parent is deemed to be the
            // root of the hierarchy.
            {
                path = ImmutableList.of(childNumber);
            } else {
                path = ImmutableList.of();
            }
        }
        byte[] chainCode = new byte[32];
        buffer.get(chainCode);
        byte[] data = new byte[33];
        buffer.get(data);
        checkArgument(!buffer.hasRemaining(), "Found unexpected data in key");
        if (pub) {
            ECPoint point = ECKey.CURVE.getCurve().decodePoint(data);
            return new DeterministicKey(path, chainCode, point, null, parent);
        } else {
            return new DeterministicKey(path, chainCode, new BigInteger(1, data), parent);
        }
    }
    
    static byte[] longTo4ByteArray(long n) {
        byte[] bytes = Arrays.copyOfRange(ByteBuffer.allocate(8).putLong(n).array(), 4, 8);
        assert bytes.length == 4 : bytes.length;
        return bytes;
    }
}
