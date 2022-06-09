package ca.uwaterloo;

import java.math.BigInteger;

public class RangeProofParameters {

    private BigInteger tau, z_0, z_1, z_2, z_3, t_0, t_1, t_2, t_3, delta, c, c_1, c_2, c_3;

    public RangeProofParameters(BigInteger tau, BigInteger z_0, BigInteger z_1, BigInteger z_2,
                                BigInteger z_3, BigInteger t_0, BigInteger t_1, BigInteger t_2,
                                BigInteger t_3, BigInteger delta, BigInteger c,
                                BigInteger c_1, BigInteger c_2, BigInteger c_3)
    {
        this.tau = tau;

        this.z_0 = z_0;
        this.z_1 = z_1;
        this.z_2 = z_2;
        this.z_3 = z_3;

        this.t_0 = t_0;
        this.t_1 = t_1;
        this.t_2 = t_2;
        this.t_3 = t_3;

        this.delta = delta;

        this.c = c;
        this.c_1 = c_1;
        this.c_2 = c_2;
        this.c_3 = c_3;
    }

    public BigInteger getT_0() {
        return t_0;
    }

    public BigInteger getT_1() {
        return t_1;
    }

    public BigInteger getT_2() {
        return t_2;
    }

    public BigInteger getT_3() {
        return t_3;
    }

    public BigInteger getZ_0() {
        return z_0;
    }

    public BigInteger getZ_1() {
        return z_1;
    }

    public BigInteger getZ_2() {
        return z_2;
    }

    public BigInteger getZ_3() {
        return z_3;
    }

    public BigInteger getTau() {
        return tau;
    }

    public BigInteger getDelta() {
        return delta;
    }

    public BigInteger getC() {
        return c;
    }

    public BigInteger getC_1() {
        return c_1;
    }

    public BigInteger getC_2() {
        return c_2;
    }

    public BigInteger getC_3() {
        return c_3;
    }
}
