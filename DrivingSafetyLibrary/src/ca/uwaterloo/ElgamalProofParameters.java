package ca.uwaterloo;

import java.math.BigInteger;

public class ElgamalProofParameters {
    public byte[] ct_1, ct_2;
    public BigInteger ss_m, ss_r;

    public ElgamalProofParameters(byte[] ct_1, byte[] ct_2, BigInteger ss_m, BigInteger ss_r)
    {
        this.ct_1 = ct_1;
        this.ct_2 = ct_2;
        this.ss_m = ss_m;
        this.ss_r = ss_r;
    }

    public BigInteger getSs_m() {
        return ss_m;
    }

    public BigInteger getSs_r() {
        return ss_r;
    }

    public byte[] getCt_1() {
        return ct_1;
    }

    public byte[] getCt_2() {
        return ct_2;
    }
}
