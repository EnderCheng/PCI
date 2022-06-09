package uwaterloo;

import java.math.BigInteger;

public class ParamsProofParameters {
    BigInteger[] CC;
    BigInteger[] EE;
    BigInteger[] ss_x;
    BigInteger[] ss_r;
    BigInteger[] ss_Ga;
    RangeProofParameters[] rps;

    public ParamsProofParameters(BigInteger[] CC, BigInteger[] EE,
                                 BigInteger[] ss_x, BigInteger[] ss_r, BigInteger[] ss_Ga, RangeProofParameters[] rps)
    {
        this.CC = CC;
        this.EE = EE;
        this.ss_x = ss_x;
        this.ss_r = ss_r;
        this.ss_Ga = ss_Ga;
        this.rps = rps;
    }

    public BigInteger[] getCC() {
        return CC;
    }

    public BigInteger[] getEE() {
        return EE;
    }

    public BigInteger[] getSs_Ga() {
        return ss_Ga;
    }

    public BigInteger[] getSs_r() {
        return ss_r;
    }

    public BigInteger[] getSs_x() {
        return ss_x;
    }

    public RangeProofParameters[] getRps() {
        return rps;
    }
}
